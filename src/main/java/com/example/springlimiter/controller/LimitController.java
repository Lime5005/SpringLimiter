package com.example.springlimiter.controller;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/limit")
public class LimitController {

    /**
     * Set Limiter: 2 requests per second.
     * public static RateLimiter create(double permitsPerSecond)
     */
    private final RateLimiter limiter = RateLimiter.create(2);

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("test1")
    public String testLimiter() {
        // If not getting the token in 100 milliseconds, then degrade the service.
        boolean tryAcquire = limiter.tryAcquire(100, TimeUnit.MILLISECONDS);

        if (!tryAcquire) {
            log.warn("Enter service downgrade, time {}", LocalDateTime.now().format(dtf));
            return "Too many people in the line, please retry later!";
        }

        log.info("Get token success, time {}", LocalDateTime.now().format(dtf));
        return "Request succeed!";
    }


}
