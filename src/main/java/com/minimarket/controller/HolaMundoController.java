package com.minimarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Público", description = "Endpoints de acceso público, sin autenticación")
@RestController
public class HolaMundoController {

    @Operation(summary = "Saludo de verificación",
            description = "Endpoint público para comprobar que la API está en línea. No requiere autenticación.")
    @ApiResponse(responseCode = "200", description = "La API responde correctamente",
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(value = "¡Hola Mundo!")))
    @SecurityRequirements // Anula el requisito global de basicAuth: ruta pública
    @GetMapping("/public/hola")
    public String holaMundo() {
        return "¡Hola Mundo!";
    }
}
