package cl.vampfern.tenpo_backend_challenge.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

@SpringBootTest(properties = "execute.db.script=false")
@DirtiesContext
class RateLimiterServiceTest {

    private static RedisServer redisServer;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @BeforeAll
    public static void startRedisServer() {
        redisServer = new RedisServerBuilder().port(6399).setting("maxmemory 256M").build();
        redisServer.start();
    }

    @AfterAll
    public static void stopRedisServer() {
        redisServer.stop();
    }

    @Test
    void testRateLimiting() {
        RateLimiterService rateLimiterService = new RateLimiterService(redisTemplate);

        String key = "test:key";
        int limit = 2;
        long durationInSeconds = 1;

        StepVerifier.create(rateLimiterService.isAllowed(key, limit, durationInSeconds))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(rateLimiterService.isAllowed(key, limit, durationInSeconds))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(rateLimiterService.isAllowed(key, limit, durationInSeconds))
                .expectNext(false)
                .verifyComplete();

        sleep(durationInSeconds * 1000 + 100);

        StepVerifier.create(rateLimiterService.isAllowed(key, limit, durationInSeconds))
                .expectNext(true)
                .verifyComplete();
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}