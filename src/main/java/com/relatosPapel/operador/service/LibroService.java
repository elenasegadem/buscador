package com.relatosPapel.operador.service;


import com.relatosPapel.operador.controller.model.CreateLibroRequest;
import com.relatosPapel.operador.controller.model.LibroDTO;
import com.relatosPapel.operador.data.model.Libro;

import java.util.Date;
import java.util.List;

public interface LibroService {

    List<Libro> getLibros(String titulo, String autor, Date fechaPublicacion, String categoria,
                          String isbn, Integer valoracion, Boolean visibilidad);

    Libro getLibro(String libroId);

    Libro createLibro(CreateLibroRequest libro);

    Boolean deleteLibro(String libroId);

    Libro updateLibro(String libroId, String updateRequest);

    Libro updateLibro(String libroId, LibroDTO updateRequest);
}
