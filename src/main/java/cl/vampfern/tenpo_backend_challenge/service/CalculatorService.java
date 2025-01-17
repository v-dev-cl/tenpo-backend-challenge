package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
public class CalculatorService implements ICalculatorService {
    Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    private final CalculatorConfigService calculatorConfigService;

    @Autowired
    public CalculatorService(CalculatorConfigService calculatorConfigService) {
        this.calculatorConfigService = calculatorConfigService;
    }

    @Override
    public Mono<Float> calculate(CalculateRequestDto calculateRequestDto) {
        return this.calculatorConfigService.getAdditionalPercentage(Optional.empty())
                .doOnNext(additionalPercentage ->
                        logger.info("Additional percentage: {}", additionalPercentage))
                .map(additionalPercentage -> {
                    float sum = calculateRequestDto.getNum1() + calculateRequestDto.getNum2();
                    return sum * (1 + (additionalPercentage / 100F));
                });
    }

}
