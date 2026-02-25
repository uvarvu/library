package com.aioannou.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "library.rate-limit")
public record RateLimitProperties(
    @Min(1) long capacity,
    @Min(1) long refillTokens,
    @Min(1) long refillSeconds
) {}
