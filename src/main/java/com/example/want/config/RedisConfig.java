package com.example.want.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.timeout}")
    private Long timeout;

    @Bean
    @Primary
    @Qualifier("heart")
    public LettuceConnectionFactory lettuceConnectionFactory() {
        final SocketOptions socketOptions = SocketOptions.builder() // 소켓 옵션 설정
                .connectTimeout(Duration.ofSeconds(10)) // 연결 시도 시간 초과 설정 (10초)
                .build();

        final ClientOptions clientOptions = ClientOptions.builder() // 클라이언트 옵션 설정
                .socketOptions(socketOptions) // 소켓 옵션 설정 (연결 시도 시간 초과)
                .build(); // 클라이언트 옵션 설정

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder() // LettuceClientConfiguration 설정
                .clientOptions(clientOptions) // 클라이언트 옵션 설정 (소켓 옵션 설정)
                .commandTimeout(Duration.ofMinutes(1)) // 명령 시간 초과 설정 (1분)
                .shutdownTimeout(Duration.ZERO) // 종료 시간 초과 설정 (0초)
                .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port); // RedisStandaloneConfiguration 설정
        redisStandaloneConfiguration.setDatabase(0); // 데이터베이스 설정 (0번)

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration); // LettuceConnectionFactory 반환
    }

    @Bean
    @Qualifier("heart")
    public RedisTemplate<String, Object> heartRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>(); // RedisTemplate 설정
        redisTemplate.setConnectionFactory(lettuceConnectionFactory()); // 연결 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 키 직렬화 설정
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));  // 값 직렬화 설정
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); // 해시 키 직렬화 설정
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class)); // 해시 값 직렬화 설정
        redisTemplate.afterPropertiesSet(); // 속성 설정
        return redisTemplate; // RedisTemplate 반환
    }

    @Bean
    @Qualifier("login")
    public LettuceConnectionFactory loginConnectionFactory() {
        final SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        final ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .build();

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMinutes(1))
                .shutdownTimeout(Duration.ZERO)
                .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setDatabase(1);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean
    @Qualifier("login")
    public RedisTemplate<String, Object> loginRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(loginConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
