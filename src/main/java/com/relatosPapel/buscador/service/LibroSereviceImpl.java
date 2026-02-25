package com.relatosPapel.buscador.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.controller.model.LibroQueryResponse;
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
    public LibroQueryResponse getLibros(String tituloAutor, LocalDate fechaPublicacion, List<String> categoria, String isbn,
                                              List<Integer> valoracion, List<Float> precio, String formato, Boolean visibilidad, String page) {

        if (StringUtils.hasLength(tituloAutor) || StringUtils.hasLength(formato) || fechaPublicacion !=null || (categoria!=null && !categoria.isEmpty())
                || StringUtils.hasLength(isbn) || (valoracion!=null && !valoracion.isEmpty())|| visibilidad!=null
                || precio!=null && !precio.isEmpty()) {
            return libroRepository.search(tituloAutor, fechaPublicacion, categoria, isbn, valoracion, precio, formato, visibilidad, page);
        }

        LibroQueryResponse libroQueryResponse = new LibroQueryResponse();
        libroQueryResponse.setLibros(libroRepository.getLibros());
        return libroQueryResponse;
    }

    @Override
    public Libro getLibro(String libroId) {
        return libroRepository.getById(libroId);
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

        if (libro != null) {
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
