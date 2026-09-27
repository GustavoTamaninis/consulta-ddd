# Consulta DDD

Uma aplicação web para consulta de DDDs, desenvolvida como resposta ao desafio técnico da HSP Software, na 3.ª opção de projeto **"Painel de Consulta"**.

A aplicação consome a **[BrasilAPI](https://brasilapi.com.br/)** para retornar, a partir de um DDD informado pelo usuário, o estado correspondente e a lista de municípios atendidos por aquele código.

## Funcionalidades

- Consulta de DDD via API própria (`/api/ddd/v1/{ddd}`), que atua como *proxy* para a BrasilAPI.
- Interface gráfica simples (HTML + Bootstrap + JavaScript puro) para digitar o DDD e visualizar o resultado.
- Validação de entrada:
  - DDD vazio → erro `400 (Bad Request)`.
  - DDD fora do padrão válido (não numérico ou fora da faixa 11–99) → erro `422 (Unprocessable Content)`.
  - Falhas inesperadas na consulta (ex.: instabilidade da API externa) → erro `500 (Internal Server Error)` com mensagem amigável.
- Tratamento de tela vazia/erro no front-end: mensagens de erro são exibidas ao usuário sem quebrar a interface, tanto para entradas inválidas quanto para falhas de comunicação com a API.

## Como rodar o projeto

### Pré-requisitos

- JDK 25 instalado (ou superior, compatível com a versão configurada no `pom.xml`).
- Não é necessário ter o Maven instalado localmente, pois o projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`).

### Passo a passo

```bash
# Clone o repositório
git clone https://github.com/GustavoTamaninis/consulta-ddd.git
cd consulta-ddd

# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicação sobe por padrão em `http://localhost:8080`.

- Interface web: `http://localhost:8080/`
- Endpoint da API: `GET http://localhost:8080/api/ddd/v1/{ddd}` (ex.: `/api/ddd/v1/47`)

### Rodando os testes

```bash
./mvnw test
```

## Exemplo de uso da API

**Requisição:**
```
GET /api/ddd/v1/47
```

**Resposta (200):**
```json
{
  "state": "SC",
  "cities": ["Joinville", "Blumenau", "Balneário Camboriú", "..."]
}
```

**Resposta de erro (DDD vazio, 400):**
```json
{
  "mensagem": "Erro! O campo de DDD está vazio.",
  "status": 400
}
```

**Resposta de erro (DDD inválido, 422):**
```json
{
  "mensagem": "Erro! O DDD deve ser um número inteiro entre 11 e 99.",
  "status": 422
}
```

## O que ficou de fora e por quê

Minha prioridade neste desafio foi a garantia do **funcionamento completo do fluxo principal**: receber o DDD, validar a entrada, consultar a BrasilAPI, tratar os erros mais relevantes e exibir o resultado de forma clara na interface.
Não implementei o sistema de paginação, pois, admito, não tive o conhecimento suficiente para fazer isso neste projeto.


## Histórico de conversas com IA

O histórico de interações com ferramentas de Inteligência Artificial utilizadas durante o desenvolvimento está disponível no arquivo [`historico-conversas-com-ia.md`](./historico-conversas-com-ia.md), na raíz deste repositório.
