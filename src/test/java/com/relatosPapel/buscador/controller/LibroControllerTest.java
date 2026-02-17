package com.relatosPapel.buscador.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relatosPapel.buscador.controller.model.CreateLibroRequest;
import com.relatosPapel.buscador.controller.model.LibroDTO;
import com.relatosPapel.buscador.data.model.Libro;
import com.relatosPapel.buscador.exception.DuplicateISBNException;
import com.relatosPapel.buscador.service.LibroService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(LibroController.class)
public class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibroService libroService;

    @Autowired
    private ObjectMapper objectMapper;

    private Libro libroTest;

    @BeforeEach
    public void setup() {
        libroTest = Libro.builder()
                .id(1L)
                .titulo("El Quijote")
                .autor("Miguel de Cervantes")
                .fechaPublicacion(null)
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();
    }

    //CREATE TESTS


    @Test
    public void creteLibroTestOk() throws Exception {
        CreateLibroRequest request = CreateLibroRequest.builder()
                .titulo("El Quijote")
                .autor("Miguel de Cervantes")
                .fechaPublicacion(null)
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        Mockito.when(libroService.createLibro(Mockito.any(CreateLibroRequest.class))).thenReturn(libroTest);

        mockMvc.perform(post("/libros")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("El Quijote"))
                .andExpect(jsonPath("$.autor").value("Miguel de Cervantes"))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.categoria").value("Novela"))
                .andExpect(jsonPath("$.visibilidad").value(true));

    }

    @Test
    public void createLibroMissingFieldsTest() throws Exception {
        CreateLibroRequest request = CreateLibroRequest.builder()
                .autor("Miguel de Cervantes")
                .isbn("1234567890")
                .build();

        mockMvc.perform(post("/libros")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createLibroDuplicateISBNTest() throws Exception {
        CreateLibroRequest request = CreateLibroRequest.builder()
                .titulo("El Quijote")
                .autor("Miguel de Cervantes")
                .fechaPublicacion(null)
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        doThrow(new DuplicateISBNException("El ISBN ya existe: " + request.getIsbn()))
            .when(libroService).createLibro(any(CreateLibroRequest.class));

        mockMvc.perform(post("/libros")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void createLibroInvalidJson() throws Exception {
        String invalidJson = "{ \"titulo\": \"El Quijote\", \"autor\": \"Miguel de Cervantes\" ";

        mockMvc.perform(post("/libros")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    //GET BY ID TESTS
    
    @Test
    public void getLibroByIdTestOk() throws Exception {
        Mockito.when(libroService.getLibro("1")).thenReturn(libroTest);

        mockMvc.perform(get("/libros/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("El Quijote"))
                .andExpect(jsonPath("$.autor").value("Miguel de Cervantes"))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.categoria").value("Novela"))
                .andExpect(jsonPath("$.visibilidad").value(true));
    }

    @Test
    public void getLibroByIdNotFoundTest() throws Exception {
        Mockito.when(libroService.getLibro("1")).thenReturn(null);

        mockMvc.perform(get("/libros/1")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //GET ALL TESTS

    @Test
    public void getLibrosTestOk() throws Exception {
        Mockito.when(libroService.getLibros(null, null, null, null, null, null, null)).thenReturn(List.of(libroTest));

        mockMvc.perform(get("/libros")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("El Quijote"))
                .andExpect(jsonPath("$[0].autor").value("Miguel de Cervantes"))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"))
                .andExpect(jsonPath("$[0].categoria").value("Novela"))
                .andExpect(jsonPath("$[0].visibilidad").value(true));
    }

    @Test
    public void getLibrosWithFiltersTest() throws Exception {
        Mockito.when(libroService.getLibros(eq("El Quijote"), eq("Cervantes"),
                    any(), eq("Clásica"), eq("1234567890123"), eq(5), eq(true)))
            .thenReturn(Arrays.asList(libroTest));

        mockMvc.perform(get("/libros")
                .param("titulo", "El Quijote")
                .param("autor", "Cervantes")
                .param("categoria", "Clásica")
                .param("isbn", "1234567890123")
                .param("valoracion", "5")
                .param("visibilidad", "true")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("El Quijote"))
                .andExpect(jsonPath("$[0].autor").value("Miguel de Cervantes"))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"))
                .andExpect(jsonPath("$[0].categoria").value("Novela"))
                .andExpect(jsonPath("$[0].visibilidad").value(true));
    }

    @Test
    public void getLibrosNoResultTest() throws Exception {
        Mockito.when(libroService.getLibros(any(), any(), any(), any(), any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/libros")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    //DELETE TESTS

     @Test
     public void deleteLibroTestOk() throws Exception {
         Mockito.when(libroService.deleteLibro("1")).thenReturn(true);

         mockMvc.perform(delete("/libros/1")
                 .contentType("application/json"))
                 .andExpect(status().isNoContent());
     }

    @Test
    public void deleteLibroNotFoundTest() throws Exception {
        Mockito.when(libroService.deleteLibro("1")).thenReturn(false);

        mockMvc.perform(delete("/libros/1")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    //UPDATE TESTS
    
    @Test
    public void updateLibroTestOk() throws Exception {
        LibroDTO updaDto = LibroDTO.builder()
                .titulo("El Quijote Actualizado")
                .autor("Miguel de Cervantes")
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();
        Libro libroUpdated = libroTest;
        libroUpdated.setTitulo("El Quijote Actualizado");

        Mockito.when(libroService.updateLibro(eq("1"), any(LibroDTO.class))).thenReturn(libroUpdated);
        mockMvc.perform(put("/libros/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("El Quijote Actualizado"));
    }

    @Test
    public void updateLibroNotFoundTest() throws Exception {
        LibroDTO updaDto = LibroDTO.builder()
                .titulo("El Quijote Actualizado")
                .autor("Miguel de Cervantes")
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        Mockito.when(libroService.updateLibro(eq("1"), any(LibroDTO.class))).thenReturn(null);
        mockMvc.perform(put("/libros/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updaDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateLibroInvalidJson() throws Exception {
        String invalidJson = "{ \"titulo\": \"El Quijote Actualizado\", \"autor\": \"Miguel de Cervantes\" ";

        mockMvc.perform(put("/libros/1")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateLibroDuplicateISBNTest() throws Exception {
        LibroDTO updaDto = LibroDTO.builder()
                .titulo("El Quijote Actualizado")
                .autor("Miguel de Cervantes")
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        doThrow(new DuplicateISBNException("El ISBN ya existe: " + updaDto.getIsbn()))
            .when(libroService).updateLibro(eq("1"), any(LibroDTO.class));
        
        mockMvc.perform(put("/libros/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updaDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateLibroInvalidIDTest() throws Exception {
        LibroDTO updaDto = LibroDTO.builder()
                .titulo("El Quijote Actualizado")
                .autor("Miguel de Cervantes")
                .isbn("1234567890")
                .categoria("Novela")
                .visibilidad(true)
                .build();

        Mockito.when(libroService.updateLibro(eq("invalid"), any(LibroDTO.class))).thenReturn(null);
        mockMvc.perform(put("/libros/invalid")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updaDto)))
                .andExpect(status().isNotFound());
    }

    // PATCH TESTS

    @Test
    public void patchLibroTestOk() throws Exception {
        String patchRequest = "{ \"titulo\": \"El Quijote Parcheado\" }";
        Libro libroUpdated = libroTest;
        libroUpdated.setTitulo("El Quijote Parcheado");

        Mockito.when(libroService.updateLibro(eq("1"), any(String.class))).thenReturn(libroUpdated);
        mockMvc.perform(patch("/libros/1")
                .contentType("application/json")
                .content(patchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("El Quijote Parcheado"));
    }

    @Test
    public void patchLibroInvalidJson() throws Exception {
        String invalidJson = "{ \"titulo\": \"El Quijote Parcheado\" ";

        mockMvc.perform(patch("/libros/1")
                .contentType("application/json")
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchLibroNotFoundTest() throws Exception {
        String patchRequest = "{ \"titulo\": \"El Quijote Parcheado\" }";

        Mockito.when(libroService.updateLibro(eq("1"), any(String.class))).thenReturn(null);
        mockMvc.perform(patch("/libros/1")
                .contentType("application/json")
                .content(patchRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchLibroInvalidIdTest() throws Exception {
        String patchRequest = "{ \"titulo\": \"El Quijote Parcheado\" }";

        Mockito.when(libroService.updateLibro(eq("invalid"), any(String.class))).thenReturn(null);
        mockMvc.perform(patch("/libros/invalid")
                .contentType("application/json")
                .content(patchRequest))
                .andExpect(status().isBadRequest());
    }

    
}
