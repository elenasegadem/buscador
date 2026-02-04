package com.relatosPapel.buscador.service;


import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.model.Libro;

import java.time.LocalDate;
import java.util.List;

public interface LibroService {

    List<Libro> getLibros(String titulo, String autor, LocalDate fechaPublicacion, String categoria,
                          String isbn, Integer valoracion, Boolean visibilidad);

    Libro getLibro(String libroId);

    Libro createLibro(CreateLibroRequest libro);

    Boolean deleteLibro(String libroId);

    Libro updateLibro(String libroId, String updateRequest);

    Libro updateLibro(String libroId, LibroDTO updateRequest);
}
