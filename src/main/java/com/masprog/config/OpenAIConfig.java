package com.masprog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Controle Financeiro Pessoal")
                        .version("v1")
                        .description("API RESTful que permita o gerenciamento de receitas, despesas e " +
                                "investimentos pessoais com base em categorias predefinidas. O sistema oferecerá funcionalidades para cadastrar, " +
                                "consultar, atualizar e excluir registros financeiros, além de gerar resumos mensais e anuais.")
                        .termsOfService("https://www.linkedin.com/company/106536069/admin/dashboard/")
                        .license(new License().name("Apache 2.0")
                                .url("https://www.linkedin.com/in/mauro-manuel-8606312b5/")));
    }
}
