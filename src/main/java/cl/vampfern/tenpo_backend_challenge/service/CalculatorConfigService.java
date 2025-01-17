package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculatorConfigDto;
import cl.vampfern.tenpo_backend_challenge.exception.FetchAdditionalDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

@Service
public class CalculatorConfigService implements ICalculatorConfigService {

    public static final Logger logger = LoggerFactory.getLogger(CalculatorConfigService.class);

    private static final float ERROR_PROBABILITY = 0.3F; // 30%
    private static final String CACHE_KEY = "calculator::service::cache";

    private final ReactiveRedisTemplate<String, CalculatorConfigDto> redisTemplate;

    @Autowired
    public CalculatorConfigService(ReactiveRedisTemplate<String, CalculatorConfigDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean shouldThrowError() {
        Random random = new Random();
        return random.nextFloat() < ERROR_PROBABILITY;
    }

    @Override
    public Mono<CalculatorConfigDto> getCalculatorConfig() {
        if (shouldThrowError()) {
            logger.error("returning randomized error on fetch external data");
            return Mono.error(new FetchAdditionalDataException("Randomized getCalculatorConfig error"));
        }

        return Mono.just(CalculatorConfigDto
                .builder()
                .additionalPercentage(10F)
                .build());
    }

    @Override
    public Mono<Float> getAdditionalPercentage(Optional<Integer> retries) {
        Integer totalRetries = retries.orElse(3);

        // TODO: retryWhen is only executing once...
        return this.getCalculatorConfig().retryWhen(Retry.max(totalRetries)).onErrorResume(s -> Mono.empty()).flatMap(config -> {
                    logger.info("Successfully retrieved external config: {}", config);
                    return redisTemplate.opsForValue()
                            .set(CACHE_KEY, config, Duration.ofMinutes(30L))
                            .doOnSuccess(success -> logger.info("Config cached successfully"))
                            .doOnError(error -> logger.error("Error caching config", error))
                            .thenReturn(config);
                }).switchIfEmpty(redisTemplate.opsForValue()
                        .get(CACHE_KEY)
                        .doOnNext(cachedConfig -> logger.info("Successfully retrieved calculator config from cache"))
                        .switchIfEmpty(Mono.error(new FetchAdditionalDataException())))
                .map(CalculatorConfigDto::getAdditionalPercentage)
                .doOnError(error -> logger.error("Could not get calculator config from cache nor service", error));
    }
}
