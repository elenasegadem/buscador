package com.relatosPapel.buscador.controller.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDTO {

    private String titulo;

    private String autor;

    private String isbn;

    private String categoria;

    private int valoracion;

    private boolean visibilidad;

    private int stock;

    private String formato;

    private String imageUrl;

    private String descripcion;

    private Float precio;
}
