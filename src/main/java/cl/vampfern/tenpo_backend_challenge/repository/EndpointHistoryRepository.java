package cl.vampfern.tenpo_backend_challenge.repository;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EndpointHistoryRepository extends ReactiveCrudRepository<EndpointHistory, Long> {

    Flux<EndpointHistory> findAllBy(Pageable pageable);

}
