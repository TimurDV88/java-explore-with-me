package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class StatClientConfig {

    @Value("${stat-server.url}")
    private String statServiceUrl;

    private RestTemplate buildRestTemplate(RestTemplateBuilder builder) {

        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(statServiceUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Bean
    public StatClient statClient(RestTemplateBuilder builder) {

        return new StatClient(buildRestTemplate(builder));
    }
}
