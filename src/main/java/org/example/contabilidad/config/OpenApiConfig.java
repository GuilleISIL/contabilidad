package org.example.contabilidad.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SOFYA - ISIL",
                version = "v1",
                description = """
      API REST para sistema contable (facturas, pagos, asientos, plan de cuentas).
      Incluye endpoints para consulta y registro de asientos contables, facturación y conciliaciones.
    """,
                contact = @Contact(
                        name = "Equipo Sofya",
                        email = "support@sofya.local",
                        url = "https://sofya.local"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local"),
                @Server(url = "https://api.sofya.example.com", description = "Producción")
        }
)
public class OpenApiConfig {

  @Bean
  public GroupedOpenApi accountingGroup() {
    return GroupedOpenApi.builder()
      .group("Cuentas")
      .pathsToMatch("/api/v1/accounts/**", "/api/v1/journal/**")
      .build();
  }

  @Bean
  public GroupedOpenApi billingGroup() {
    return GroupedOpenApi.builder()
      .group("Pagos")
      .pathsToMatch("/api/v1/invoices/**", "/api/v1/payments/**")
      .build();
  }
}
