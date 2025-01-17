package cl.vampfern.tenpo_backend_challenge.service;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import cl.vampfern.tenpo_backend_challenge.repository.EndpointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

// https://www.baeldung.com/reactive-streams-step-verifier-test-publisher
class EndpointHistoryServiceTest {

    private EndpointHistoryRepository endpointHistoryRepository;
    private EndpointHistoryService endpointHistoryService;

    @BeforeEach
    void setUp() {
        endpointHistoryRepository = Mockito.mock(EndpointHistoryRepository.class);
        endpointHistoryService = new EndpointHistoryService(endpointHistoryRepository);
    }

    @Test
    void findAllEndpointHistories_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EndpointHistory> endpointHistories = Arrays.asList(
                new EndpointHistory(1L, "/test1", "params1", "response1", "error1", "127.0.0.1", LocalDateTime.now(), LocalDateTime.now()),
                new EndpointHistory(2L, "/test2", "params2", "response2", "error2", "127.0.0.1", LocalDateTime.now(), LocalDateTime.now())
        );
        when(endpointHistoryRepository.findAllBy(pageable)).thenReturn(Flux.fromIterable(endpointHistories));
        when(endpointHistoryRepository.count()).thenReturn(Mono.just(2L));

        Mono<Page<EndpointHistory>> result = endpointHistoryService.findAllEndpointHistories(pageable);

        StepVerifier.create(result)
                .expectNextMatches(page -> page.getContent().equals(endpointHistories) && page.getTotalElements() == 2)
                .verifyComplete();

        verify(endpointHistoryRepository, times(1)).findAllBy(pageable);
        verify(endpointHistoryRepository, times(1)).count();
    }

    @Test
    void save_ShouldSaveEntitySuccessfully() {
        EndpointHistory endpointHistory = new EndpointHistory(1L, "/test", "params", "response", "error", "127.0.0.1", LocalDateTime.now(), LocalDateTime.now());
        when(endpointHistoryRepository.save(endpointHistory)).thenReturn(Mono.just(endpointHistory));

        Mono<EndpointHistory> result = endpointHistoryService.save(endpointHistory);

        StepVerifier.create(result)
                .expectNextMatches(savedEntity -> savedEntity.equals(endpointHistory))
                .verifyComplete();

        verify(endpointHistoryRepository, times(1)).save(endpointHistory);
    }

    @Test
    void save_ShouldLogErrorWhenSaveFails() {
        EndpointHistory endpointHistory = new EndpointHistory(1L, "/test", "params", "response", "error", "127.0.0.1", LocalDateTime.now(), LocalDateTime.now());
        when(endpointHistoryRepository.save(endpointHistory)).thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<EndpointHistory> result = endpointHistoryService.save(endpointHistory);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Database error"))
                .verify();

        verify(endpointHistoryRepository, times(1)).save(endpointHistory);
    }
}