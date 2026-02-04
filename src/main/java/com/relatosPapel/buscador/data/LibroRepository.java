package com.relatosPapel.buscador.data;

import com.relatosPapel.buscador.data.model.Libro;
import com.relatosPapel.buscador.data.utils.Consts;
import com.relatosPapel.buscador.data.utils.SearchCriteria;
import com.relatosPapel.buscador.data.utils.SearchOperation;
import com.relatosPapel.buscador.data.utils.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LibroRepository {

    private final LibroJpaRepository libroJpaRepository;

    public List<Libro> getLibros() {
        return libroJpaRepository.findAll();
    }

    public Libro getById(Long id) {
        return libroJpaRepository.findById(id).orElse(null);
    }

    public Libro save(Libro libro) {
        return libroJpaRepository.save(libro);
    }

    public void delete(Libro libro) {
        libroJpaRepository.delete(libro);
    }

    public List<Libro> search(String titulo, String autor, LocalDate fechaPublicacion, String categoria,
                              String isbn, Integer valoracion, Boolean visibilidad) {
        SearchCriteria criteria = new SearchCriteria();

        if (StringUtils.isNotBlank(titulo)) {
            criteria.add(new SearchStatement(Consts.TITULO, titulo, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(autor)) {
            criteria.add(new SearchStatement(Consts.AUTOR, autor, SearchOperation.MATCH));
        }

        if (fechaPublicacion != null) {
            criteria.add(new SearchStatement(Consts.FECHA_PUBLICACION, fechaPublicacion, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(categoria)) {
            criteria.add(new SearchStatement(Consts.CATEGORIA, categoria, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(isbn)) {
            criteria.add(new SearchStatement(Consts.ISBN, isbn, SearchOperation.EQUAL));
        }

        if (valoracion != null) {
            criteria.add(new SearchStatement(Consts.VALORACION, valoracion, SearchOperation.EQUAL));
        }

        if (visibilidad != null) {
            criteria.add(new SearchStatement(Consts.VISIBILIDAD, visibilidad, SearchOperation.EQUAL));
        }

        return libroJpaRepository.findAll(criteria);
    }
}
