package org.example.contabilidad;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(
        info = @Info(
                title = "Accounting API",
                version = "v1",
                description = "API REST para sistema contable (facturas, pagos, asientos, plan de cuentas)"
        )
)
@SpringBootApplication
public class SofyaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SofyaApplication.class, args);
    }

}
