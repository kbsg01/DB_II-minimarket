package com.minimarket.controller;

import com.minimarket.assembler.UsuarioModelAssembler;
import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Usuarios", description = "Gestión de usuarios y sus roles (cliente, cajero, administrador). " +
        "La contraseña nunca se retorna en las respuestas. Las respuestas incluyen enlaces HATEOAS " +
        "hacia los carritos y ventas del usuario.")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final String EJEMPLO_USUARIO = """
            {
              "username": "cliente2",
              "password": "clave-segura-123",
              "roles": [ { "id": 3 } ]
            }""";

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @Operation(summary = "Listar todos los usuarios",
            description = "Retorna los usuarios registrados con sus roles. El campo password se omite por seguridad.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        var modelos = usuarioService.findAll().stream()
                .map(usuarioModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener un usuario por ID",
            description = "Busca un usuario específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(u -> ResponseEntity.ok(usuarioModelAssembler.toModel(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo usuario",
            description = "Registra un usuario con username único y roles existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: username duplicado, contraseña faltante o roles inexistentes", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> guardarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario a crear (los roles referenciados deben existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(name = "nuevoUsuario", value = EJEMPLO_USUARIO)))
            @RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioModelAssembler.toModel(guardado));
    }

    @Operation(summary = "Actualizar un usuario existente",
            description = "Reemplaza los datos del usuario identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: username duplicado, contraseña faltante o roles inexistentes", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(
            @Parameter(description = "Identificador único del usuario a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del usuario", required = true,
                    content = @Content(schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(name = "usuarioActualizado", value = EJEMPLO_USUARIO)))
            @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioModelAssembler.toModel(usuarioService.save(usuario)));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un usuario",
            description = "Elimina el usuario identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "Identificador único del usuario a eliminar", example = "1")
            @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
