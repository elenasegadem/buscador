package com.relatosPapel.buscador.data;

import com.relatosPapel.buscador.controller.model.AggregationDetails;
import com.relatosPapel.buscador.data.model.Libro;
import com.relatosPapel.buscador.controller.model.LibroQueryResponse;
import com.relatosPapel.buscador.data.utils.Consts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchAggregations;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.filter.ParsedFilters;
import org.opensearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class LibroRepository {

    private final LibroElasticsearchRepository libroElasticsearchRepository;

    private final ElasticsearchOperations openSearchClient;

    public List<Libro> getLibros() {
        return libroElasticsearchRepository.findAll();
    }

    public Libro getById(Long id) {
        return libroElasticsearchRepository.findById(id).orElse(null);
    }

    public Libro save(Libro libro) {
        return libroElasticsearchRepository.save(libro);
    }

    public void delete(Libro libro) {
        libroElasticsearchRepository.delete(libro);
    }

    @SneakyThrows
    public List<Libro> search(String titulo, String autor, LocalDate fechaPublicacion, List<String> categoria,
                              String isbn, List<Integer> valoracion, Boolean visibilidad, String page) {

        BoolQueryBuilder querySpecification = QueryBuilders.boolQuery();

        if (StringUtils.isEmpty(titulo)) {
            querySpecification.must(QueryBuilders.matchQuery(Consts.TITULO, titulo));
        }

        if (StringUtils.isEmpty(autor)) {
            querySpecification.must(QueryBuilders.matchQuery(Consts.AUTOR, autor));
        }

        if (fechaPublicacion != null) {
            querySpecification.must(QueryBuilders.termQuery(Consts.FECHA_PUBLICACION, fechaPublicacion));
        }

        if (categoria != null && !categoria.isEmpty()) {
            querySpecification.must(QueryBuilders.termsQuery(Consts.CATEGORIA, categoria));
        }

        if (StringUtils.isEmpty(isbn)) {
            querySpecification.must(QueryBuilders.termQuery(Consts.ISBN, isbn));
        }

        if (valoracion != null && !valoracion.isEmpty()) {
            querySpecification.must(QueryBuilders.termsQuery(Consts.VALORACION, valoracion));
        }

        if (visibilidad != null) {
            querySpecification.must(QueryBuilders.termQuery(Consts.VISIBILIDAD, visibilidad));
        }

        if (!querySpecification.hasClauses()) {
            querySpecification.must(QueryBuilders.matchAllQuery());
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpecification);
        nativeSearchQueryBuilder.withAggregations(
                AggregationBuilders.terms(Consts.AGG_KEY_TERM_CATEGORIA).field(Consts.CATEGORIA).size(1000),
                AggregationBuilders.terms(Consts.AGG_KEY_TERM_VALORACION).field(Consts.VALORACION).size(1000)
        );

        nativeSearchQueryBuilder.withMaxResults(5);

        int pageInt = Integer.parseInt(page);

        if (pageInt !=0) {
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageInt,5));
        }

        Query query = nativeSearchQueryBuilder.build();

        SearchHits<Libro> results = openSearchClient.search(query, Libro.class);

        return new LibroQueryResponse(getResponseLibro(results), getResponseAggregations(results)).getLibros();
    }

    private List<Libro> getResponseLibro(SearchHits<Libro> result) {
        return result.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    private Map<String, List<AggregationDetails>> getResponseAggregations(SearchHits<Libro> result) {
        Map<String, List<AggregationDetails>> responseAggregations = new HashMap<>();

        if (result.hasAggregations()) {
            OpenSearchAggregations aggregations = (OpenSearchAggregations) result.getAggregations();
            Map<String, Aggregation> aggs = Objects.requireNonNull(aggregations).aggregations().asMap();

            aggs.forEach((key, value) -> {

                if(!responseAggregations.containsKey(key)) {
                    responseAggregations.put(key, new LinkedList<>());
                }

                if (value instanceof ParsedStringTerms parsedStringTerms) {
                    parsedStringTerms.getBuckets().forEach(bucket ->
                            responseAggregations.get(key).add(new AggregationDetails(bucket.getKey().toString(), (int) bucket.getDocCount()))
                    );
                }

                if (value instanceof ParsedFilters parsedFilters) {
                    parsedFilters.getBuckets().forEach(bucket ->
                            responseAggregations.get(key).add(new AggregationDetails(bucket.getKeyAsString(), (int) bucket.getDocCount()))
                    );
                }
            });
        }
        return responseAggregations;
    }
}
