package org.example.contabilidad.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi accountingGroup() {
    return GroupedOpenApi.builder()
      .group("accounting")
      .pathsToMatch("/api/v1/accounts/**", "/api/v1/journal/**")
      .build();
  }

  @Bean
  public GroupedOpenApi billingGroup() {
    return GroupedOpenApi.builder()
      .group("billing")
      .pathsToMatch("/api/v1/invoices/**", "/api/v1/payments/**")
      .build();
  }
}
