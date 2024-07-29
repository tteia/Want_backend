package com.example.want.api.block.service;

import com.example.want.api.block.domain.Block;
import com.example.want.api.block.domain.Category;
import com.example.want.api.block.dto.*;
import com.example.want.api.block.repository.BlockRepository;
import com.example.want.api.heart.domain.Heart;
import com.example.want.api.heart.dto.HeartListResDto;
import com.example.want.api.heart.repository.HeartRepository;
import com.example.want.api.member.domain.Member;
import com.example.want.api.member.login.UserInfo;
import com.example.want.api.member.repository.MemberRepository;
import com.example.want.api.projectMember.Repository.ProjectMemberRepository;
import com.example.want.api.projectMember.domain.ProjectMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public Block createBlock(CreateBlockRqDto request, UserInfo userInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), formatter);
        Block createdBlock = blockRepository.save(request.toEntity(request.getLatitude(), request.getLongitude(),startTime, endTime));
        createdBlock.initializeFields();
        return createdBlock;
    }

    public Page<BlockActiveListRsDto> getNotActiveBlockList(Pageable pageable, String memberEmail) {
        memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));
        Page<Block> block = blockRepository.findAllByIsActivated("N", pageable);
        return block.map(BlockActiveListRsDto::new);
    }

    public Page<BlockActiveListRsDto> getActiveBlockList(Pageable pageable) {
        Page<Block> blockList = blockRepository.findAllByIsActivatedOrderByStartTimeAsc("Y", pageable);
        return blockList.map(b -> BlockActiveListRsDto.fromEntity(b));
    }

    public BlockDetailRsDto getBlockDetail(Long id) {
        Block block = blockRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        return block.toDetailDto();
    }
    @Transactional
    public void addLikeToPost(Long blockId, String memberEmail) {
        Block block = getBlockById(blockId);
        Member member = getMemberByEmail(memberEmail);

        boolean alreadyLiked = heartRepository.existsByMemberAndBlock(member, block);

        if (alreadyLiked) {
            removeLike(member, block);
        } else {
            addLike(member, block);
        }

        updateRedisHeartCount(block);
    }

    private Block getBlockById(Long blockId) {
        return blockRepository.findById(blockId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));
    }

    private void removeLike(Member member, Block block) {
        Heart heart = heartRepository.findByMemberAndBlock(member, block)
                .orElseThrow(() -> new IllegalStateException("좋아요가 존재하지 않습니다."));
        heartRepository.delete(heart);
        block.decrementHearts();
        blockRepository.save(block);
    }

    private void addLike(Member member, Block block) {
        Heart heart = Heart.builder().member(member).block(block).build();
        heartRepository.save(heart);
        block.incrementHearts();
        blockRepository.save(block);
    }

    private void updateRedisHeartCount(Block block) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = "blockId::" + block.getId();
        String hashKey = "heartCount";

        // Redis에서 좋아요 수를 업데이트
        hashOperations.put(key, hashKey, block.getHeartCount());
    }

    public Long getLikesCount(Long blockId) {
        String key = "blockId::" + blockId;
        String hashKey = "heartCount";

        Long likesCount = getLikesCountFromCache(key, hashKey);
        if (likesCount == null) {
            likesCount = getLikesCountFromDb(blockId);
            updateCache(key, hashKey, likesCount);
        }

        return likesCount;
    }

    private Long getLikesCountFromCache(String key, String hashKey) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        Object cachedValue = hashOperations.get(key, hashKey);

        if (cachedValue instanceof Long) {
            return (Long) cachedValue;
        } else if (cachedValue instanceof Integer) {
            return ((Integer) cachedValue).longValue();
        }

        return null;
    }

    private Long getLikesCountFromDb(Long blockId) {
        return blockRepository.findById(blockId)
                .map(Block::getHeartCount)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
    }

    private void updateCache(String key, String hashKey, Long likesCount) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, hashKey, likesCount);
    }

    // Block 을 끌어다 놓음 -> Block 에 일정 날짜 등록
    @Transactional
    public Block addDateBlock(AddDateBlockRqDto addDateRqDto) {
        Block block = blockRepository.findById(addDateRqDto.getBlockId()).orElseThrow(() -> new IllegalArgumentException("블럭을 찾을 수 없습니다."));
        block.updatePlan(addDateRqDto.getStartTime(), addDateRqDto.getEndTime());
        return blockRepository.save(block);
    }

    // 날짜별 Block 조회
    public Page<Block> getBlocksByDate(String startTime, Pageable pageable) {// "2024-07-26T09:00" 형식의 문자열
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime date = LocalDateTime.parse(startTime,formatter);
        LocalDate startDate = date.toLocalDate();
        LocalDate endDate = startDate.plusDays(1);
        Page<Block> blocks = blockRepository.findAllByStartTimeBetweenOrderByStartTimeAsc(startDate.atStartOfDay(),endDate.atStartOfDay(), pageable);
        return blocks;
    }

    // 좋아요 수에 따라 Block 을 정렬하여 반환하는 메서드
    @Transactional
    public Page<HeartListResDto> activeBlocksByPopular(Pageable pageable) {
        Page<Block> blocks = blockRepository.findByIsActivatedOrderByHeartCountDesc("Y", pageable);
        return blocks.map(HeartListResDto::fromEntity);
    }

    // 카테고리 별로 Block 조회하기
    public Page<Block> getBlocksByCategory(Category category, Pageable pageable) {
        return blockRepository.findByCategory(category, pageable);
    }

//    public BlockDetailRsDto updateBlockTitle(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        block.updateTitle(request.getTitle());
//        return block.toDetailDto();
//    }
//
//    public BlockDetailRsDto updateBlockContent(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        block.updateContent(request.getContent());
//        return block.toDetailDto();
//    }
//
//    public BlockDetailRsDto updateBlockPlaceName(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        block.updatePlaceName(request.getPlaceName());
//        return block.toDetailDto();
//    }
//
//    public BlockDetailRsDto updateBlockCategory(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        block.updateCategory(request.getCategory());
//        return block.toDetailDto();
//    }
//
//    public BlockDetailRsDto updateBlockIsActivated(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        if (request.getIsActivated().equals("Y")) {
//            block.changeIsActivated("N");
//        } else {
//            block.changeIsActivated("Y");
//        }
//        block.changeIsActivated(request.getIsActivated());
//        return block.toDetailDto();
//    }

//    public BlockDetailRsDto updateBlockTime(Long id, UpdateBlockRqDto request, UserInfo userInfo) {
//        Member member = getMemberByEmail(userInfo.getEmail());
//        Block block = getBlockById(id);
//        LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
//        LocalDateTime endTime = LocalDateTime.parse(request.getEndTime());
//        block.updatePlan(startTime, endTime );
//        return block.toDetailDto();
//    }

    @Transactional
    public BlockDetailRsDto updateBlock(Long id, UpdateBlockRqDto updateBlockRqDto, UserInfo userInfo) {

        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Block not found"));

        if (updateBlockRqDto.getTitle() != null) {
            block.updateTitle(updateBlockRqDto.getTitle());
        }
        if (updateBlockRqDto.getContent() != null) {
            block.updateContent(updateBlockRqDto.getContent());
        }
        if (updateBlockRqDto.getPlaceName() != null) {
            block.updatePlaceName(updateBlockRqDto.getPlaceName());
        }
        if (updateBlockRqDto.getLatitude() != null && updateBlockRqDto.getLongitude() != null) {
            block.updatePoint(updateBlockRqDto.getLatitude(), updateBlockRqDto.getLongitude());
        }

        if (updateBlockRqDto.getStartTime() != null && updateBlockRqDto.getEndTime() != null) {
            block.updatePlan(updateBlockRqDto.getStartTime(), updateBlockRqDto.getEndTime());
        }
        if (updateBlockRqDto.getCategory() != null) {
            block.updateCategory(updateBlockRqDto.getCategory());

        }
        if (updateBlockRqDto.getIsActivated() != null) {
            if(updateBlockRqDto.getIsActivated().equals("Y")){
                block.changeIsActivated("N");
            } else {
                block.changeIsActivated("Y");
            }
        }
    return block.toDetailDto();
    }

    @Transactional
    public Block blockDelete(UserInfo userInfo, Long id) {
        Block block = blockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        block.getProject().getProjectMembers().stream()
                .filter(projectMember -> projectMember.getMember().getEmail().equals(userInfo.getEmail()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 속한 프로젝트의 팀원이 아닙니다."));
        block.changeIsDelete();
        return block;
    }




}
