package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.dto.CalculateRequestDto;
import reactor.core.publisher.Mono;

public interface ICalculatorService {
    Mono<Float> calculate(CalculateRequestDto calculateRequestDto);
}
