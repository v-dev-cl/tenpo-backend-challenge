package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface IEndpointHistoryService {

    Mono<Page<EndpointHistory>> findAllEndpointHistories(Pageable pageable);

    Mono<EndpointHistory> save(EndpointHistory endpointHistory);

}
