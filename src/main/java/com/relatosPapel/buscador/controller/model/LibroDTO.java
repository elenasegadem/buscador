package com.relatosPapel.buscador.controller.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LibroDTO {

    private String titulo;

    private String autor;

    private Date fechaPublicacion;

    private String isbn;

    private String categoria;

    private int valoracion;

    private boolean visibilidad;

    private int stock;
}
