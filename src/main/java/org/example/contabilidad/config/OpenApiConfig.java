
package org.example.contabilidad.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi accountingGroup() {
    return GroupedOpenApi.builder()
            .group("Cuentas")
            .pathsToMatch("/api/v1/accounts/**", "/api/v1/journal/**")
            .addOpenApiCustomizer(openApi -> openApi.info(
                    new Info()
                            .title("SOFYA - ISIL • Cuentas")
                            .version("v1")
                            .description("""
                  API REST para sistema contable: cuentas y asientos (journal).
                  Incluye consulta y registro de asientos contables.
                """)
                            .contact(new Contact()
                                    .name("Equipo TI")
                                    .email("support@isil.pe")
                                    .url("https://isil.pe"))
                            .license(new License()
                                    .name("Apache 2.0")
                                    .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
            ))
            .build();
  }

  @Bean
  public GroupedOpenApi billingGroup() {
    return GroupedOpenApi.builder()
            .group("Pagos")
            .pathsToMatch("/api/v1/invoices/**", "/api/v1/payments/**")
            .addOpenApiCustomizer(openApi -> openApi.info(
                    new Info()
                            .title("SOFYA - ISIL • Facturación y Pagos")
                            .version("v1")
                            .description("""
                  API REST para facturas y pagos.
                  Operaciones de emisión, cobro y conciliación.
                """)
                            .contact(new Contact()
                                    .name("Equipo TI")
                                    .email("support@isil.pe")
                                    .url("https://isil.pe"))
                            .license(new License()
                                    .name("Apache 2.0")
                                    .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
            ))
            .build();
  }
}