package br.com.fiap.mototrack;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(info = @Info(
        title = "Mototrack API",
        version = "v1",
        description = "API do sistema Mototrack"
))
public class MototrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MototrackApplication.class, args);
    }

}
