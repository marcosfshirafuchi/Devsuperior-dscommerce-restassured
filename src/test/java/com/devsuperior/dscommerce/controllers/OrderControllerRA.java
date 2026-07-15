package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingOrderId, nonExistingOrderId;

    @BeforeEach
    public void setUp(){
        //Endereço que vai estar hospedado o serviço
        baseURI = "http://localhost:8080";

        existingOrderId = 1L;
        nonExistingOrderId = 100L;

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";
        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto"; // Invalid Token
    }

    /*Problema 5: Consultar pedido por id

    Implemente os testes de API usando Rest Assured para consulta de pedidos por id (método GET do OrderController), considerando os seguintes cenários. Lembre-se de inserir o token no cabeçalho da requisição.
    1.	Busca de pedido por id retorna pedido existente quando logado como admin
    2.	Busca de pedido por id retorna pedido existente quando logado como cliente e o pedido pertence ao usuário
    3.	Busca de pedido por id retorna 403 quando pedido não pertence ao usuário
    4.	Busca de pedido por id retorna 404 para pedido inexistente quando logado como admin
    5.	Busca de pedido por id retorna 404 para pedido inexistente quando logado como cliente
    6.	Busca de pedido por id retorna 401 quando não logado como admin ou cliente

    * */

    //1.	Busca de pedido por id retorna pedido existente quando logado como admin
    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged(){
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
            .header("Content-type", "application/json")
            .header("Authorization", "Bearer " + adminToken)
            .accept(ContentType.JSON)
        .when()
            //Está passando o endpoint para testar
            .get("/orders/{id}", existingOrderId)
        .then()
            //Verificando a resposta da requisição
            .statusCode(200)
            .body("id", is(1))
            .body("moment", equalTo("2022-07-25T13:00:00Z"))
            .body("status", equalTo("PAID"))
            .body("client.name", equalTo("Maria Brown"))
            .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
            .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
            .body("total", is(1431.0F));
    }

    //2.	Busca de pedido por id retorna pedido existente quando logado como cliente e o pedido pertence ao usuário
    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged(){
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type", "application/json")
           .header("Authorization", "Bearer " + clientToken)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .get("/orders/{id}", existingOrderId)
        .then()
           //Verificando a resposta da requisição
           .statusCode(200)
           .body("id", is(1))
           .body("moment", equalTo("2022-07-25T13:00:00Z"))
           .body("status", equalTo("PAID"))
           .body("client.name", equalTo("Maria Brown"))
           .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
           .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
           .body("total", is(1431.0F));
    }

    //3.	Busca de pedido por id retorna 403 quando pedido não pertence ao usuário
    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser(){
        Long otherOrderId = 2L;
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type", "application/json")
           .header("Authorization", "Bearer " + clientToken)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .get("/orders/{id}", otherOrderId)
        .then()
           //Verificando a resposta da requisição
           .statusCode(403);
    }

    //4.	Busca de pedido por id retorna 404 para pedido inexistente quando logado como admin
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged(){
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type", "application/json")
           .header("Authorization", "Bearer " + adminToken)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .get("/orders/{id}", nonExistingOrderId)
        .then()
           //Verificando a resposta da requisição
           .statusCode(404);
    }

    //5.	Busca de pedido por id retorna 404 para pedido inexistente quando logado como cliente
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged(){
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type", "application/json")
           .header("Authorization", "Bearer " + clientToken)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .get("/orders/{id}", nonExistingOrderId)
        .then()
           //Verificando a resposta da requisição
           .statusCode(404);
    }

    //6.	Busca de pedido por id retorna 401 quando não logado como admin ou cliente
    @Test
    public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken(){
        given()
           //Definindo o cabeçalho da requisição
           //Tipo da informação
           .header("Content-type", "application/json")
           .header("Authorization", "Bearer " + invalidToken)
           .accept(ContentType.JSON)
        .when()
           //Está passando o endpoint para testar
           .get("/orders/{id}", existingOrderId)
        .then()
           //Verificando a resposta da requisição
           .statusCode(401);
    }
}
