package com.minimarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Public", description = "Endpoints públicos accesibles sin autenticación.")
@RestController
public class HolaMundoController {

    @Operation(summary = "Health check",
               description = "Verifica que el servidor esté en línea. No requiere autenticación.",
               security = {})
    @ApiResponse(responseCode = "200", description = "Servidor disponible")
    @GetMapping("/public/hola")
    public String holaMundo() {
        return "¡Hola Mundo!";
    }
}
