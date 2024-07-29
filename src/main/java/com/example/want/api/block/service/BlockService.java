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
import com.example.want.api.project.domain.Project;
import com.example.want.api.project.repository.ProjectRepository;
import com.example.want.api.traveluser.Repository.TravelUserRepository;
import com.example.want.api.traveluser.domain.TravelUser;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService {
    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final ProjectRepository projectRepository;
    private final TravelUserRepository projectMemberRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    @Transactional
    public Block createBlock(CreateBlockRqDto request, UserInfo userInfo) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 없습니다."));
        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 없습니다."));
        TravelUser travelUser = projectMemberRepository.findByProjectAndMember(project, member)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원은 프로젝트에 속해있지 않습니다."));
        Block block = request.toEntity(request.getCategory(), project);
        return blockRepository.save(block);

    }

    public Page<BlockActiveListRsDto> getNotActiveBlockList(Pageable pageable, String memberEmail) {
        System.out.println(memberEmail);
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
    public Block setDateBlock(AddDateBlockRqDto setDateRqDto) {
        Block block = blockRepository.findById(setDateRqDto.getBlockId()).orElseThrow(() -> new IllegalArgumentException("블럭을 찾을 수 없습니다."));
        block.updatePlan(setDateRqDto);
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
}
