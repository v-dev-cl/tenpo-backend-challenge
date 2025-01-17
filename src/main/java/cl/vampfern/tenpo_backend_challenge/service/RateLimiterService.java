package cl.vampfern.tenpo_backend_challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

// https://medium.com/@mbanaee61/efficient-rate-limiting-in-reactive-spring-boot-applications-with-redis-and-junit-testing-20675e73104a
@Service
public class RateLimiterService implements IRateLimiterService{

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Autowired
    public RateLimiterService(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Boolean> isAllowed(String key, int limit, long durationInSeconds) {
        String countKey = key + ":count";
        String timestampKey = key + ":timestamp";

        return redisTemplate.opsForValue()
                .increment(countKey)
                .flatMap(count -> {
                    if (count == 1) {
                        return redisTemplate.opsForValue()
                                .set(timestampKey, String.valueOf(System.currentTimeMillis()))
                                .then(redisTemplate.expire(countKey, Duration.ofSeconds(durationInSeconds)))
                                .thenReturn(true);
                    } else if (count <= limit) {
                        return Mono.just(true);
                    } else {
                        return redisTemplate.opsForValue()
                                .get(timestampKey)
                                .flatMap(timestamp -> {
                                    long currentTime = System.currentTimeMillis();
                                    long storedTime = Long.parseLong(timestamp);
                                    long elapsedTime = currentTime - storedTime;

                                    if (elapsedTime > durationInSeconds * 1000) {
                                        return redisTemplate.opsForValue()
                                                .set(countKey, "1")
                                                .then(redisTemplate.opsForValue()
                                                        .set(timestampKey, String.valueOf(currentTime)))
                                                .then(redisTemplate.expire(countKey, Duration.ofSeconds(durationInSeconds)))
                                                .thenReturn(true);
                                    } else {
                                        return Mono.just(false);
                                    }
                                });
                    }
                });
    }
}
