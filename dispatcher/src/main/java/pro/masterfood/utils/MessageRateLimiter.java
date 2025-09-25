package pro.masterfood.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class MessageRateLimiter {
    private final Cache<String, Instant> lastRequestTimes;

    public MessageRateLimiter() {
        this.lastRequestTimes = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60)) // TTL 60 секунд
                .build();
    }

    public boolean allowRequestAtomic(String userId) {
        Instant now = Instant.now();
        Instant lastRequest = lastRequestTimes.getIfPresent(userId);

        if (lastRequest == null || now.isAfter(lastRequest.plusSeconds(1))) {
            lastRequestTimes.put(userId, now);
            return true;
        } else {
            lastRequestTimes.put(userId, now);
            return false;
        }
    }
}