package com.relatosPapel.buscador.controller;

import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.model.Libro;
import com.relatosPapel.buscador.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "Libro Controller", description = "Endpoints para gestionar libros")
public class LibroController {

    private final LibroService libroService;

    @GetMapping("/libros")
    @Operation(
            operationId = "Obtener los libros",
            description = "Operación de lectura",
            summary = "Obtiene una lista de libros"
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))
    )
    public ResponseEntity<List<Libro>> getLibros(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "titulo", description = "Título del libro. No tiene por que ser exacto",
                    example = "Lazarillo de Tormes", required = false)
            @RequestParam(required = false) String titulo,
            @Parameter(name = "autor", description = "Autor del libro. No tiene por que ser exacto",
                    example = "Arturo Pérez", required = false)
            @RequestParam(required = false) String autor,
            @Parameter(name = "fechaPublicacion", description = "Fecha de publicación del libro", example = "2026-01-01", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacion,
            @Parameter(name = "categoria", description = "Categoría del libro. Debe ser exacta",
                    example = "Romántica", required = false)
            @RequestParam(required = false) String categoria,
            @Parameter(name = "isbn", description = "ISBN del libro. Debe ser exacto",
                    example = "1234567890123", required = false)
            @RequestParam(required = false) String isbn,
            @Parameter(name = "valoracion", description = "Valoración del libro",
                    example = "2", required = false)
            @RequestParam(required = false) Integer valoracion,
            @Parameter(name = "visibilidad", description = "Visibilidad del libro", example = "true", required = false)
            @RequestParam(required = false) Boolean visibilidad
            ) {
        log.info("Headers recibidos: {}", headers);
        List<Libro> libros = libroService.getLibros(titulo, autor, fechaPublicacion, categoria, isbn, valoracion, visibilidad);

        return ResponseEntity.ok(Objects.requireNonNullElse(libros, Collections.emptyList()));
    }

    @GetMapping("/libros/{libroId}")
    @Operation(
            operationId = "Obtener un libro",
            description = "Operación de lectura",
            summary = "Se devuelve un libro específico a partir de su identificador")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))
    )
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador proporcionado."
    )
    public ResponseEntity<Libro> getLibro(@PathVariable String libroId) {
        Libro libro = libroService.getLibro(libroId);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/libros/{libroId}")
    @Operation(
            operationId = "Eliminar un libro",
            description = "Operación de escritura",
            summary = "Elimina un libro específico a partir de su identificador")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado ningún libro con el identificador proporcionado."
    )
    public ResponseEntity<Void> deleteLibro(@PathVariable String libroId) {
        Boolean deleted = libroService.deleteLibro(libroId);
        if (Boolean.TRUE.equals(deleted)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/libros")
    @Operation(
            operationId = "Crear un libro",
            description = "Operación de escritura",
            summary = "Crea un nuevo libro",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateLibroRequest.class))
            )
    )
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))
    )
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos de entrada inválidos."
    )
    @ApiResponse(
            responseCode = "409",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "ISBN ya existe."
    )
    public ResponseEntity<Libro> createLibro(@RequestBody CreateLibroRequest createLibroRequest) {
        Libro createdLibro = libroService.createLibro(createLibroRequest);

        if (createdLibro != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLibro);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/libros/{libroId}")
    @Operation(
            operationId = "Actualizar parcialmente un libro",
            description = "Operación de escritura",
            summary = "Actualiza parcialemente un libro específico a partir de su identificador",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a actualizar",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            )
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))
    )
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos de entrada inválidos."
    )
    public ResponseEntity<Libro> updateLibro(@PathVariable String libroId,
                                             @RequestBody String updateRequest) {
        Libro updatedLibro = libroService.updateLibro(libroId, updateRequest);

        if (updatedLibro != null) {
            return ResponseEntity.ok(updatedLibro);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/libros/{libroId}")
    @Operation(
            operationId = "Actualizar un libro",
            description = "Operación de escritura",
            summary = "Actualiza un libro específico a partir de su identificador",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a actualizar",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LibroDTO.class))
            )
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Libro.class))
    )
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Libro no encontrado."
    )
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos de entrada inválidos."
    )
    public ResponseEntity<Libro> updateLibro(@PathVariable String libroId,
                                             @RequestBody LibroDTO updateRequest) {
        Libro updatedLibro = libroService.updateLibro(libroId, updateRequest);

        if (updatedLibro != null) {
            return ResponseEntity.ok(updatedLibro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
