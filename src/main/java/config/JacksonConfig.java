package com.vehicle.spareparts.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder builder) {
                builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                builder.mixIn(Object.class, HibernateProxyMixin.class);
            }
        };
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private static class HibernateProxyMixin {
    }
}
