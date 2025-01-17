package cl.vampfern.tenpo_backend_challenge.config;

import cl.vampfern.tenpo_backend_challenge.model.EndpointHistory;
import cl.vampfern.tenpo_backend_challenge.service.EndpointHistoryService;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Component
public class RequestLoggingFilter implements WebFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final EndpointHistoryService endpointHistoryService;

    @Autowired
    public RequestLoggingFilter(EndpointHistoryService endpointHistoryService) {
        this.endpointHistoryService = endpointHistoryService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info("RequestLoggingFilter triggered for request: {}", exchange.getRequest().getPath());

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String requestPath = request.getPath().toString();

        StringBuilder stringBuilder = new StringBuilder();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        queryParams.forEach((key, values) -> {
            values.forEach(value -> {
                stringBuilder.append(key).append("=").append(value).append("&");
            });
        });

        String remoteAddress = request.getRemoteAddress() != null ? request.getRemoteAddress().toString() : "unknown";

        // Ex. https://medium.com/spring-boot/spring-cloud-api-gateway-filters-fca80f96fd26
        ServerHttpResponse decoratedResponse = new ServerHttpResponseDecorator(response) {
            private final StringBuilder responseBody = new StringBuilder();

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return super.writeWith(Flux.from(body).doOnNext(dataBuffer -> {
                    responseBody.append(Charset.defaultCharset().decode(dataBuffer.asByteBuffer()).toString());
                }));
            }

            @Override
            public Mono<Void> setComplete() {
                return super.setComplete();
            }

            @Override
            public String toString() {
                return responseBody.toString();
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .doOnSuccess(unused -> {

                    if ("/endpoint-history".equals(requestPath) || requestPath.contains("/v3/api-docs") || requestPath.contains("/swagger-ui")) {
                        logger.info("Skipping saving history for request path: {}", requestPath);
                        return;
                    }

                    String responseBody = decoratedResponse.toString(); // Get response data

                    EndpointHistory history = EndpointHistory.builder()
                            .endpoint(requestPath)
                            .parameters(stringBuilder.toString())
                            .response(responseBody) // Store captured response
                            .error(null) // Error information, if any
                            .ip(remoteAddress)
                            .created_at(LocalDateTime.now())
                            .update_at(LocalDateTime.now())
                            .build();

                    endpointHistoryService.save(history).subscribe();
                })
                .doOnError(errorParam -> {
                    logger.error("Saving error request {}", errorParam.getMessage());

                    String responseBody = decoratedResponse.toString();
                    String error = errorParam.getMessage();

                    EndpointHistory history = EndpointHistory.builder()
                            .endpoint(requestPath)
                            .parameters(stringBuilder.toString())
                            .response(responseBody)
                            .error(error)
                            .ip(remoteAddress)
                            .created_at(LocalDateTime.now())
                            .update_at(LocalDateTime.now())
                            .build();

                    endpointHistoryService.save(history).subscribe();
                });
    }
}