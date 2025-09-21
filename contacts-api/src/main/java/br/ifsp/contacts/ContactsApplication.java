package br.ifsp.contacts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Classe principal da nossa aplicação Spring Boot.
 * 
 * A anotação @SpringBootApplication habilita as configurações
 * automáticas do Spring (auto-configuration) e também indica 
 * que esta é a classe que deve ser executada para iniciar 
 * a aplicação.
 */
@OpenAPIDefinition(info = @Info(title = "contacts-api", description = "API para gerenciamento de contatos"))
@SpringBootApplication
public class ContactsApplication {

    public static void main(String[] args) {
        // Método main: ponto de entrada de uma aplicação Java.
        // SpringApplication.run() inicia a aplicação Spring Boot.
        SpringApplication.run(ContactsApplication.class, args);
    }

}