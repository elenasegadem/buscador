package com.relatosPapel.buscador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.LibroRepository;
import com.relatosPapel.buscador.data.model.Libro;


@ExtendWith(MockitoExtension.class)
public class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroSereviceImpl libroSerevice;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    private Libro libroTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        libroTest = Libro.builder()
                .titulo("El Quijote")
                .autor("Miguel de Cervantes")
                .fechaPublicacion(java.time.LocalDate.of(1605, 1, 16))
                .isbn("978-3-16-148410-0")
                .categoria("Novela")
                .visibilidad(true)
                .stock(10)
                .valoracion(1)
                .build();
    }

    //GET TESTS

    @Test
    public void getLibrosTestNoFilters() {
        when(libroRepository.getLibros()).thenReturn(Arrays.asList(libroTest));

        List<Libro> result = libroSerevice.getLibros(null, null, null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("El Quijote", result.get(0).getTitulo());
    }

    @Test
    public void getLibrosTestWithFilters() {
        when(libroRepository.search(anyString(), anyString(), any(), anyString(), anyString(), anyInt(), anyBoolean()))
            .thenReturn(Arrays.asList(libroTest));

        List<Libro> result = libroSerevice.getLibros("El Quijote", "Cervantes", null, "Clásica", "1234567890123", 5, true);

        assertEquals(1, result.size());
        assertEquals("El Quijote", result.get(0).getTitulo());
    }

    @Test
    public void testGerLibroNoResults() {
        when(libroRepository.getLibros()).thenReturn(Arrays.asList());

        List<Libro> result = libroSerevice.getLibros(null, null, null, null, null, null, null);

        assertEquals(null, result);
    }

    @Test
    public void testGetLibroById() {
        when(libroRepository.getById(anyLong())).thenReturn(libroTest);

        Libro result = libroSerevice.getLibro("1");

        assertEquals("El Quijote", result.getTitulo());
    }

    @Test
    public void testGetLibroByIdNotFound() {
        when(libroRepository.getById(anyLong())).thenReturn(null);

        Libro result = libroSerevice.getLibro("1");

        assertEquals(null, result);
    }

    //Create tests

     @Test
     public void testCreateLibroSuccess() {
         when(libroRepository.save(any(Libro.class))).thenReturn(libroTest);

         CreateLibroRequest request = new CreateLibroRequest();
         request.setTitulo("El Quijote");
         request.setAutor("Miguel de Cervantes");
         request.setFechaPublicacion(java.time.LocalDate.of(1605, 1, 16));
         request.setIsbn("978-3-16-148410-0");
         request.setCategoria("Novela");
         request.setVisibilidad(true);

         Libro result = libroSerevice.createLibro(request);

         assertEquals("El Quijote", result.getTitulo());
     }

     @Test
     public void testCreateLibroDuplicateISBN() {
            when(libroRepository.save(any(Libro.class))).thenThrow(new RuntimeException("Duplicate ISBN"));
    
            CreateLibroRequest request = new CreateLibroRequest();
            request.setTitulo("El Quijote");
            request.setAutor("Miguel de Cervantes");
            request.setFechaPublicacion(java.time.LocalDate.of(1605, 1, 16));
            request.setIsbn("978-3-16-148410-0");
            request.setCategoria("Novela");
            request.setVisibilidad(true);
    
            try {
                libroSerevice.createLibro(request);
            } catch (Exception e) {
                assertEquals("El ISBN ya existe: 978-3-16-148410-0", e.getMessage());
            }
    }

    //DELETE TESTS

    @Test
    public void testDeleteLibroSuccess() {
        when(libroRepository.getById(anyLong())).thenReturn(libroTest);

        Boolean result = libroSerevice.deleteLibro("1");

        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testDeleteLibroNotFound() {
        when(libroRepository.getById(anyLong())).thenReturn(null);

        Boolean result = libroSerevice.deleteLibro("1");

        assertEquals(Boolean.FALSE, result);
    }

    //PUT TESTS

    @Test
    public void testUpdateLibroSuccess() {
        LibroDTO libroDTO = LibroDTO.builder()
                .titulo("El Quijote de la mancha")
                .autor("Miguel de Cervantes")
                .isbn("978-3-16-148410-0")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        when(libroRepository.getById(anyLong())).thenReturn(libroTest);
        when(libroRepository.save(any(Libro.class))).thenReturn(libroTest);

        Libro result = libroSerevice.updateLibro("1", libroDTO);
        assertEquals("El Quijote de la mancha", result.getTitulo());
    }

    //PATCH TESTS

    @Test
    public void testPatchLibroSuccess() {
        String updateRequest = "{\"titulo\": \"El Quijote de la mancha\"}";

        when(libroRepository.getById(1L)).thenReturn(libroTest);
        when(libroRepository.save(any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);

        Libro result = libroSerevice.updateLibro("1", updateRequest);
        
        assertNotNull(result);
        assertEquals("El Quijote de la mancha", result.getTitulo());
        assertEquals("Miguel de Cervantes", result.getAutor());
    }






}
