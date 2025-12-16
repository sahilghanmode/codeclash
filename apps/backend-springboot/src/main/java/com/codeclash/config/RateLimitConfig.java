package com.codeclash.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {
    
    // Store buckets per IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Bean
    public Map<String, Bucket> rateLimitBuckets() {
        return buckets;
    }
    
    // Create a bucket that allows 10 requests per minute per IP
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }
}
