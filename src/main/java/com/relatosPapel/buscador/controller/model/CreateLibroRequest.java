package com.relatosPapel.buscador.controller.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLibroRequest {

    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    @NotBlank
    private String isbn;

    @NotBlank
    private String categoria;

    @NotNull
    private Boolean visibilidad;

    @NotNull
    private int stock;

    @NotNull
    private LocalDate fechaPublicacion;

    @NotNull
    private String formato;

    @NotNull
    private String imageUrl;

    @NotNull
    private String descripcion;

    @NotNull
    private Float precio;
}
