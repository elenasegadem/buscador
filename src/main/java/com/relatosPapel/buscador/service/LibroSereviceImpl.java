package com.relatosPapel.buscador.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.LibroRepository;
import com.relatosPapel.buscador.data.model.Libro;
import com.relatosPapel.buscador.exception.DuplicateISBNException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class LibroSereviceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public List<Libro> getLibros(String titulo, String autor, LocalDate fechaPublicacion, String categoria, String isbn, Integer valoracion, Boolean visibilidad) {

        if (StringUtils.hasLength(titulo) || StringUtils.hasLength(autor) || fechaPublicacion !=null || StringUtils.hasLength(categoria)
                || StringUtils.hasLength(isbn) || valoracion!=null || visibilidad!=null) {
            return libroRepository.search(titulo, autor, fechaPublicacion, categoria, isbn, valoracion, visibilidad);
        }

        List<Libro> libros = libroRepository.getLibros();
        return libros.isEmpty() ? null : libros;
    }

    @Override
    public Libro getLibro(String libroId) {
        return libroRepository.getById(Long.valueOf(libroId));
    }

    @Override
    public Libro createLibro(@Valid CreateLibroRequest libroRequest) {
        try {
            Libro libro = Libro.builder()
                    .titulo(libroRequest.getTitulo())
                    .autor(libroRequest.getAutor())
                    .fechaPublicacion(libroRequest.getFechaPublicacion())
                    .isbn(libroRequest.getIsbn())
                    .categoria(libroRequest.getCategoria())
                    .visibilidad(libroRequest.getVisibilidad())
                    .build();

            return libroRepository.save(libro);

        } catch (Exception e) {
            throw new DuplicateISBNException("El ISBN ya existe: " + libroRequest.getIsbn());        }
    }

    @Override
    public Boolean deleteLibro(String libroId) {
        Libro libro = getLibro(libroId);

        if (libroId != null) {
            libroRepository.delete(libro);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Libro updateLibro(String libroId, String updateRequest) {
        Libro libro = getLibro(libroId);

        if (libro != null) {
            try {
                JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(updateRequest));
                JsonNode libroNode = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(libro)));
                Libro patchedLibro = objectMapper.treeToValue(libroNode, Libro.class);
                return libroRepository.save(patchedLibro);
            } catch (Exception e) {
                log.error("Error updating libro with id {}: {}", libroId, e.getMessage());
            }
        }
        return null;
    }

    @Override
    public Libro updateLibro(String libroId, LibroDTO updateRequest) {
        Libro libro = getLibro(libroId);
        if (libro != null) {
            libro.update(updateRequest);
            return libroRepository.save(libro);
        }

        return null;
    }
}
