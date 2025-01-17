package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculatorConfigDto;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ICalculatorConfigService {

    Mono<CalculatorConfigDto> getCalculatorConfig();

    Mono<Float> getAdditionalPercentage(Optional<Integer> retries);

}
