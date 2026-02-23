package com.relatosPapel.buscador.data;

import com.relatosPapel.buscador.data.model.Libro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;


public interface LibroElasticsearchRepository extends ElasticsearchRepository<Libro, String> {

    Optional<Libro> findById(Long id);

    List<Libro> findAll();
}
