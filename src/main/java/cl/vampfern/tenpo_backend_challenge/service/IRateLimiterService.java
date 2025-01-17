package cl.vampfern.tenpo_backend_challenge.service;

import reactor.core.publisher.Mono;

public interface IRateLimiterService {

    Mono<Boolean> isAllowed(String key, int limit, long durationInSeconds);

}
