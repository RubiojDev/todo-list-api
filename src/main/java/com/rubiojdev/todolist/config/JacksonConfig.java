package com.rubiojdev.todolist.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración personalizada para Jackson.
 * <p>
 * Esta clase define un {@link ObjectMapper} como bean dentro del contexto
 * de Spring. Fue creada para resolver un problema de detección de la
 * dependencia {@code com.fasterxml.jackson.databind.ObjectMapper} durante
 * la inyección de dependencias en la aplicación.
 * </p>
 * <p>
 * El método {@link ObjectMapper#findAndRegisterModules()} permite que
 * Jackson registre automáticamente módulos adicionales disponibles
 * en el classpath (por ejemplo, soporte para Java Time).
 * </p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Proporciona una instancia configurada de {@link ObjectMapper}
     * disponible como bean en el contexto de Spring.
     *
     * @return instancia de {@link ObjectMapper} con módulos registrados automáticamente
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules();
    }
}
