package com.minimarket.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración central del contrato OpenAPI (elementos info, servers,
 * security y components exigidos por la guía de la semana 7).
 */
@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI minimarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .description("""
                                API REST del sistema backend Minimarket Plus. \
                                Gestiona productos, categorías, carritos, inventario, \
                                ventas y usuarios con roles (cliente, cajero, administrador). \
                                Todos los endpoints de negocio requieren autenticación HTTP Basic; \
                                use el botón Authorize (credenciales de demostración en el README). \
                                Las respuestas de recurso individual y colección incluyen enlaces \
                                de hipermedia (HATEOAS, bloque "_links") hacia el propio recurso, \
                                su colección y los recursos relacionados, facilitando la navegación \
                                sin necesidad de construir URLs manualmente.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Minimarket Plus - PBY2202")
                                .email("karla.santigut@gmail.com"))
                        .license(new License()
                                .name("Uso académico - Duoc UC")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Entorno local de desarrollo")))
                // Esquema de seguridad reutilizable: habilita el botón Authorize
                // y marca cada operación protegida con el candado correspondiente.
                .components(new Components().addSecuritySchemes(BASIC_AUTH,
                        new SecurityScheme()
                                .name(BASIC_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Autenticación HTTP Basic contra los usuarios registrados en H2")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }
}
