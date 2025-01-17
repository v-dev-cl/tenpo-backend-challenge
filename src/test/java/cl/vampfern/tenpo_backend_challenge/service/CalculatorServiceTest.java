package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CalculatorServiceTest {

    @Mock
    private CalculatorConfigService calculatorConfigService;

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculate_withValidInputs_shouldReturnCalculatedResult() {
        CalculateRequestDto calculateRequestDto = new CalculateRequestDto();
        calculateRequestDto.setNum1(100F);
        calculateRequestDto.setNum2(50F);

        when(calculatorConfigService.getAdditionalPercentage(any(Optional.class)))
                .thenReturn(Mono.just(10F)); // Mock 10% additional percentage

        Mono<Float> result = calculatorService.calculate(calculateRequestDto);

        StepVerifier.create(result)
                .expectNext(165F)
                .verifyComplete();
    }

    @Test
    void calculate_withZeroInputs_shouldReturnZero() {
        CalculateRequestDto calculateRequestDto = new CalculateRequestDto();
        calculateRequestDto.setNum1(0F);
        calculateRequestDto.setNum2(0F);

        when(calculatorConfigService.getAdditionalPercentage(any(Optional.class)))
                .thenReturn(Mono.just(10F)); // Mock 10% additional percentage

        Mono<Float> result = calculatorService.calculate(calculateRequestDto);

        StepVerifier.create(result)
                .expectNext(0F)
                .verifyComplete();
    }

    @Test
    void calculate_withNegativeInputs_shouldReturnCalculatedResult() {
        CalculateRequestDto calculateRequestDto = new CalculateRequestDto();
        calculateRequestDto.setNum1(-10F);
        calculateRequestDto.setNum2(-20F);

        when(calculatorConfigService.getAdditionalPercentage(any(Optional.class)))
                .thenReturn(Mono.just(5F)); // Mock 5% additional percentage

        Mono<Float> result = calculatorService.calculate(calculateRequestDto);

        StepVerifier.create(result)
                .expectNext(-31.499998F)
                .verifyComplete();
    }

    @Test
    void calculate_withExceptionInConfigService_shouldReturnError() {
        CalculateRequestDto calculateRequestDto = new CalculateRequestDto();
        calculateRequestDto.setNum1(10F);
        calculateRequestDto.setNum2(20F);

        when(calculatorConfigService.getAdditionalPercentage(any(Optional.class)))
                .thenReturn(Mono.error(new RuntimeException("Config service error")));

        Mono<Float> result = calculatorService.calculate(calculateRequestDto);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Config service error"))
                .verify();
    }
}