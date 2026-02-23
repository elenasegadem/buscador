package com.relatosPapel.buscador.controller.model;

import com.relatosPapel.buscador.data.model.Libro;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LibroQueryResponse {

    private List<Libro> libros;
    private Map<String, List<AggregationDetails>> aggregations;
}
