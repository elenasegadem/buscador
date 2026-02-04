package com.relatosPapel.buscador.data.model;

import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.utils.Consts;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "libros")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Consts.TITULO)
    private String titulo;

    @Column(name = Consts.AUTOR)
    private String autor;

    @Column(name = Consts.ISBN, unique = true)
    private String isbn;

    @Column(name = Consts.FECHA_PUBLICACION)
    private Date fechaPublicacion;

    @Column(name = Consts.CATEGORIA, nullable = false)
    private String categoria;

    @Column(name = Consts.VALORACION)
    private int valoracion;

    @Column(name = Consts.VISIBILIDAD)
    private boolean visibilidad;

    @Column(name = Consts.STOCK)
    private int stock;

    public void update(LibroDTO libroDTO) {
        this.titulo = libroDTO.getTitulo();
        this.autor = libroDTO.getAutor();
        this.fechaPublicacion = libroDTO.getFechaPublicacion();
        this.isbn = libroDTO.getIsbn();
        this.categoria = libroDTO.getCategoria();
        this.valoracion = libroDTO.getValoracion();
        this.visibilidad = libroDTO.isVisibilidad();
        this.stock = libroDTO.getStock();
    }
}
