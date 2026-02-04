package com.relatosPapel.operador.data;

import com.relatosPapel.operador.data.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LibroJpaRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    List<Libro> findByTitulo(String titulo);

    List<Libro> findByAutor(String autor);

    List<Libro> findByVisibilidad(boolean visibilidad);
}
