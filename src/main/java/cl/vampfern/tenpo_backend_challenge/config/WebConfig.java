package cl.vampfern.tenpo_backend_challenge.config;

import cl.vampfern.tenpo_backend_challenge.pageable.PageableHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

// https://medium.com/@davidpetro/spring-webflux-and-pageable-be55104c234f
@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new PageableHandlerMethodArgumentResolver());
    }

}