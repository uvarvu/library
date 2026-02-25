package com.aioannou.library.config;

import io.github.bucket4j.Bucket;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfiguration {

    @Bean
    public Bucket libraryBucket(final RateLimitProperties properties) {
        return Bucket.builder()
            .addLimit(limit -> limit.capacity(properties.capacity())
                .refillGreedy(properties.refillTokens(), Duration.ofSeconds(properties.refillSeconds())))
            .build();
    }
}
