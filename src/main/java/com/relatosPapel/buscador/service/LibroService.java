package com.relatosPapel.buscador.service;


import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.controller.model.LibroQueryResponse;
import com.relatosPapel.buscador.data.model.Libro;

import java.time.LocalDate;
import java.util.List;

public interface LibroService {

    LibroQueryResponse getLibros(String tituloAutor, LocalDate fechaPublicacion, List<String> categoria, String isbn,
                                       List<Integer> valoracion, List<Float> precio, String formato, Boolean visibilidad, String page);

    Libro getLibro(String libroId);

    Libro createLibro(CreateLibroRequest libro);

    Boolean deleteLibro(String libroId);

    Libro updateLibro(String libroId, String updateRequest);

    Libro updateLibro(String libroId, LibroDTO updateRequest);
}
