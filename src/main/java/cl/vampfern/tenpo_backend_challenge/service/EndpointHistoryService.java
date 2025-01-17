package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import cl.vampfern.tenpo_backend_challenge.repository.EndpointHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EndpointHistoryService implements IEndpointHistoryService {

    private final Logger logger = LoggerFactory.getLogger(EndpointHistoryService.class);

    private final EndpointHistoryRepository endpointHistoryRepository;

    @Autowired
    public EndpointHistoryService(EndpointHistoryRepository endpointHistoryRepository) {
        this.endpointHistoryRepository = endpointHistoryRepository;
    }

    public Mono<Page<EndpointHistory>> findAllEndpointHistories(Pageable pageable) {
        return this.endpointHistoryRepository.findAllBy(pageable)
                .collectList()
                .zipWith(this.endpointHistoryRepository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    // TODO: Should be async, check redis or some other queue system or reactive way as it uses 2 cores
    public Mono<EndpointHistory> save(EndpointHistory endpointHistory) {
        return endpointHistoryRepository
                .save(endpointHistory)
                .doOnSuccess(savedEntity -> logger.info("Entity saved successfully {}", savedEntity))
                .doOnError(throwable -> logger.error("Error while saving entity {}", throwable.getMessage()));
    }

}
