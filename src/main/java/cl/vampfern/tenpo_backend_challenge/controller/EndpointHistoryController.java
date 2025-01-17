package cl.vampfern.tenpo_backend_challenge.controller;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import cl.vampfern.tenpo_backend_challenge.service.EndpointHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/endpoint-history")
public class EndpointHistoryController {

    private final EndpointHistoryService endpointHistoryService;

    @Autowired
    public EndpointHistoryController(EndpointHistoryService endpointHistoryService) {
        this.endpointHistoryService = endpointHistoryService;
    }

    @GetMapping()
    public Mono<Page<EndpointHistory>> getEndpointHistory(Pageable pageable) {
        return this.endpointHistoryService.findAllEndpointHistories(pageable);
    }

}
