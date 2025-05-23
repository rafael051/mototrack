package br.com.fiap.mototrack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Home", description = "Endpoint inicial de status da API")
public class HomeController {

    @GetMapping("/")
    @Operation(
            summary = "Página inicial da API",
            description = "Exibe uma mensagem de status e link para a documentação Swagger"
    )
    public String home() {
        return """
                 <b>Sistema MotoTrack</b><br>
                ✅ API rodando com sucesso!<br>
                🔗 <a href='/swagger-ui.html'>Acesse a documentação Swagger</a>
               """;
    }
}
