package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detector API")
                        .version("1.0")
                        .description("API para detectar mutantes mediante análisis de ADN.\n\n" +
                                "**Endpoints disponibles:**\n" +
                                "- POST /mutant - Detecta si un ADN pertenece a un mutante\n" +
                                "- GET /stats - Obtiene estadísticas de verificaciones\n" +
                                "- GET /health - Verifica el estado de salud de la aplicación\n\n" +
                                "**Validaciones:**\n" +
                                "- Matriz NxN con caracteres ATCG únicamente\n" +
                                "- Tamaño máximo: 1000x1000\n" +
                                "- Tamaño mínimo: 4x4"));
    }
}
