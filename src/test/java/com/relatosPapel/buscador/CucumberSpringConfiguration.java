package com.relatosPapel.buscador;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = BuscadorApplication.class)
public class CucumberSpringConfiguration {
}
