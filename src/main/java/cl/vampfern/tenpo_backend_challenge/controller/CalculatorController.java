package cl.vampfern.tenpo_backend_challenge.controller;

import cl.vampfern.tenpo_backend_challenge.dto.CalculateRequestDto;
import cl.vampfern.tenpo_backend_challenge.exception.RateLimitException;
import cl.vampfern.tenpo_backend_challenge.service.CalculatorService;
import cl.vampfern.tenpo_backend_challenge.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final RateLimiterService rateLimiterService;
    private final CalculatorService calculatorService;

    public CalculatorController(RateLimiterService rateLimiterService, CalculatorService calculatorService) {
        this.rateLimiterService = rateLimiterService;
        this.calculatorService = calculatorService;
    }

    private String getRequesterIp(ServerHttpRequest request) {
        String ipAddress = request.getHeaders().getFirst("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        return ipAddress;
    }

    @Operation(summary = "calculate sum between 2 numbers plus an additional pct fetched from an external service with 30% probs of throwing an error")
    @GetMapping("/calculate")
    public Mono<Float> addition(@RequestParam Float number1, @RequestParam Float number2, ServerHttpRequest request) {
        String requesterIp = getRequesterIp(request);

        String key = "calculate:" + requesterIp;
        int limit = 3;
        long durationInSeconds = 60;

        return rateLimiterService.isAllowed(key, limit, durationInSeconds)
                .flatMap(allowed -> {
                    if (allowed) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(new RateLimitException());
                    }
                }).flatMap(__ -> (calculatorService.calculate(CalculateRequestDto.builder()
                        .num1(number1)
                        .num2(number2)
                        .build())
                ));
    }

}
