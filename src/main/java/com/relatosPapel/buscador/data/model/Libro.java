package com.relatosPapel.buscador.data.model;

import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.utils.Consts;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "libros", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Libro {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = Consts.TITULO)
    private String titulo;

    @Field(type = FieldType.Text, name = Consts.AUTOR)
    private String autor;

    @Field(type = FieldType.Keyword, name = Consts.ISBN)
    private String isbn;

    @Field(type = FieldType.Date, name = Consts.FECHA_PUBLICACION, pattern = "yyyy-MM-dd")
    private LocalDate fechaPublicacion;

    @Field(type = FieldType.Keyword, name = Consts.CATEGORIA)
    private String categoria;

    @Field(type = FieldType.Integer, name = Consts.VALORACION)
    private int valoracion;

    @Field(type = FieldType.Boolean, name = Consts.VISIBILIDAD)
    private boolean visibilidad;

    @Field(type = FieldType.Integer, name = Consts.STOCK)
    private int stock;

    @Field(type = FieldType.Keyword, name = Consts.FORMATO)
    private String formato;

    @Field(type = FieldType.Float, name = Consts.PRECIO)
    private Float precio;

    @Field(type = FieldType.Text, name = Consts.IMAGE_URL)
    private String imageUrl;

    @Field(type = FieldType.Text, name = Consts.DESCRIPCION)
    private String descripcion;

    public void update(LibroDTO libroDTO) {
        this.titulo = libroDTO.getTitulo();
        this.autor = libroDTO.getAutor();
        this.isbn = libroDTO.getIsbn();
        this.categoria = libroDTO.getCategoria();
        this.valoracion = libroDTO.getValoracion();
        this.visibilidad = libroDTO.isVisibilidad();
        this.stock = libroDTO.getStock();
        this.formato = libroDTO.getFormato();
        this.precio = libroDTO.getPrecio();
        this.imageUrl = libroDTO.getImageUrl();
        this.descripcion = libroDTO.getDescripcion();

    }
}
