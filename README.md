# Slim Garage

Esta é uma aplicação Kotlin Spring Boot para gerenciamento de estacionamento, permitindo registrar entradas e saídas de veículos, calcular preços e aplicar descontos.
Tem como premissa as especificações do teste técnico backend java da Estapar. 

A aplicação utiliza banco de dados MySQL para persistência de dados.

## Endpoints da API

- **GET /garage**: Retorna informações sobre o estacionamento.
- **GET /revenue**: Retorna dados de receita (aceita parâmetros de consulta).
- **POST /webhook**: Registra entrada ou saída de veículo (requer corpo JSON com dados do veículo).

## Como executar localmente

1. Navegue até a pasta `docker`:
   ```
   cd docker
   ```

2. Execute o comando:
   ```
   docker-compose up
   ```

A aplicação estará disponível em `http://localhost:8080`.
