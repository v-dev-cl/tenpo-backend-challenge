package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculatorConfigDto;
import cl.vampfern.tenpo_backend_challenge.exception.FetchAdditionalDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.Mockito.*;

class CalculatorConfigServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, CalculatorConfigDto> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, CalculatorConfigDto> valueOperations;

    private CalculatorConfigService calculatorConfigService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        calculatorConfigService = new CalculatorConfigService(redisTemplate);
    }

    @Test
    void getCalculatorConfig_ShouldReturnConfig() {
        CalculatorConfigService spyService = Mockito.spy(calculatorConfigService);
        doReturn(false).when(spyService).shouldThrowError();

        Mono<CalculatorConfigDto> result = spyService.getCalculatorConfig();

        StepVerifier.create(result)
                .expectNextMatches(config -> config.getAdditionalPercentage() == 10F)
                .verifyComplete();
    }

    @Test
    void getCalculatorConfig_ShouldThrowRandomizedError() {
        CalculatorConfigService spyService = Mockito.spy(calculatorConfigService);
        doReturn(true).when(spyService).shouldThrowError();

        Mono<CalculatorConfigDto> result = spyService.getCalculatorConfig();

        StepVerifier.create(result)
                .expectError(FetchAdditionalDataException.class)
                .verify();
    }

    @Test
    void getAdditionalPercentage_ShouldReturnAndCacheValue() {
        CalculatorConfigService spyService = Mockito.spy(calculatorConfigService);
        doReturn(false).when(spyService).shouldThrowError();

        when(redisTemplate.opsForValue().set(anyString(), any(CalculatorConfigDto.class), any(Duration.class)))
                .thenReturn(Mono.just(true));

        when(redisTemplate.opsForValue().get(anyString()))
                .thenReturn(Mono.empty());

        Mono<Float> result = spyService.getAdditionalPercentage(Optional.of(3));

        StepVerifier.create(result)
                .expectNext(10F)
                .verifyComplete();

        verify(redisTemplate.opsForValue(), times(1)).set(eq("calculator::service::cache"), any(CalculatorConfigDto.class), any(Duration.class));
    }

    @Test
    void getAdditionalPercentage_ShouldFallbackToCacheOnError() {
        CalculatorConfigDto cachedConfig = CalculatorConfigDto.builder().additionalPercentage(12F).build();
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(Mono.just(cachedConfig));

        CalculatorConfigService spyService = Mockito.spy(calculatorConfigService);
        doReturn(Mono.error(new FetchAdditionalDataException("Simulated fetch error"))).when(spyService).getCalculatorConfig();

        Mono<Float> result = spyService.getAdditionalPercentage(Optional.of(3));

        StepVerifier.create(result)
                .expectNext(12F)
                .verifyComplete();

        verify(redisTemplate.opsForValue(), times(1)).get("calculator::service::cache");
    }

    @Test
    void getAdditionalPercentage_ShouldThrowErrorWhenCacheIsEmpty() {
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(Mono.empty());

        CalculatorConfigService spyService = Mockito.spy(calculatorConfigService);
        doReturn(Mono.error(new FetchAdditionalDataException("Simulated fetch error"))).when(spyService).getCalculatorConfig();

        Mono<Float> result = spyService.getAdditionalPercentage(Optional.of(3));

        StepVerifier.create(result)
                .expectError(FetchAdditionalDataException.class)
                .verify();

        verify(redisTemplate.opsForValue(), times(1)).get("calculator::service::cache");
    }
}
