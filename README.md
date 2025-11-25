**Rairai API**

Este repositório implementa a API backend do projeto Rairai, construída com Spring Boot e focada em autenticação via JWT e gerenciamento básico de usuários.

**Visão Geral**
- **Descrição**: API REST para cadastro, autenticação e gerenciamento de usuários.
- **Stack**: Java 21, Spring Boot, Spring Security, JPA (MySQL), JWT, Maven.

**Requisitos**
- **Java**: 21
- **Maven**: wrapper incluso (`./mvnw`) ou Maven 3+
- **Docker** (opcional, recomendado para MySQL)

**Como Rodar (rápido)**
- **Subir o banco MySQL via Docker Compose**:

```
docker compose -f compose.yaml up -d mysql
```

- **Rodar a aplicação** (usar o wrapper recomendado):

```
./mvnw spring-boot:run
```

- **Rodar testes**:

```
./mvnw test
```

Se preferir usar o Maven instalado globalmente:

```
mvn -DskipTests=false spring-boot:run
```

**Configuração do Banco de Dados**
- Arquivo: `src/main/resources/application.properties`.
- Exemplo atual (padrão do projeto):
	- URL: `jdbc:mysql://localhost:3306/mydatabase`
	- Usuário: `root`
	- Senha: `verysecret`
- O `compose.yaml` também contém uma definição de `mysql` (variáveis de ambiente `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`). Certifique-se de alinhar as credenciais entre o `compose.yaml` e o `application.properties` antes de rodar.

**Endpoints Principais**
- Base: `/users`
- `GET /users` : lista paginada de usuários (parâmetros `page` e `size`).
- `GET /users/{id}` : recupera usuário por `id`.
- `GET /users/me` : retorna o usuário autenticado (requere `Authorization: Bearer <token>`).
- `POST /users/register` : registra um novo usuário.
	- Body (JSON) exemplo:

```
{
	"nome": "João Silva",
	"email": "joao@example.com",
	"password": "minhasenha",
	"dataNascimento": "1990-01-01",
	"ativo": true
}
```

- `POST /users/login` : autentica e retorna token JWT.
	- Body (JSON) exemplo:

```
{
	"email": "joao@example.com",
	"password": "minhasenha"
}
```

- `PUT /users/{id}` : atualiza dados do usuário (retorna novo token).
- `DELETE /users/{id}` : remove usuário (retorna HTTP 204).

Exemplo de uso do token em chamadas protegidas:

```
curl -H "Authorization: Bearer <TOKEN_AQUI>" http://localhost:8080/users/me
```

**Swagger / OpenAPI**
- A documentação interativa está disponível quando a aplicação está em execução: `http://localhost:8080/swagger-ui.html` ou `http://localhost:8080/swagger-ui/index.html`.

**Observações de Segurança e Operação**
- A implementação atual de `JWTUtil` gera uma chave secreta em memória a cada inicialização. Isso significa que tokens emitidos antes de reiniciar a aplicação deixarão de ser válidos após um restart. Para produção, altere a implementação para utilizar um segredo persistente (ex.: variável de ambiente) e HMAC fixo.
- Revise as regras de autorização em `SecurityConfiguration` — atualmente `POST /users/**` está liberado publicamente (útil para registro/login, mas tenha cuidado com endpoints sensíveis abertos por POST).
- Verifique e alinhe credenciais entre `application.properties` e `compose.yaml` para evitar problemas de conexão com o DB.

**Estrutura do Projeto (resumo)**
- `src/main/java/.../controller` : controladores REST (`AppUserController`).
- `src/main/java/.../service` : regras de negócio e autenticação.
- `src/main/java/.../repository` : interfaces JPA.
- `src/main/java/.../config` : configuração de JWT, filtros e segurança.
- `src/main/resources` : configurações (ex.: `application.properties`).

**Contribuição**
- Abra uma issue descrevendo a mudança desejada ou crie um pull request com testes quando aplicável.

**Licença**
- Atualize este README com a licença do projeto quando disponível.

---

Se quiser, eu posso:
- Gerar um `.env` de exemplo com variáveis sensíveis.
- Adicionar instruções para build em Docker (Dockerfile + docker-compose service para a app).
- Melhorar a configuração do JWT para usar variável de ambiente.
