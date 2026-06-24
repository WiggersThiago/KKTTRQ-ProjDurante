# 🐾 ONG Patinhas — Sistema de Adoção de Animais

Sistema web institucional e administrativo para uma ONG de adoção de animais.
Construído com **Spring Boot 3 + Java 21** seguindo arquitetura MVC tradicional,
com camadas bem separadas, autenticação por sessão, banco PostgreSQL e
templates Thymeleaf para o frontend base.

---

## 1. Tecnologias utilizadas

| Camada / Recurso  | Tecnologia                                  |
|-------------------|---------------------------------------------|
| Linguagem         | Java 21                                      |
| Framework         | Spring Boot 3.5                              |
| Web               | Spring MVC + Thymeleaf                       |
| Persistência      | Spring Data JPA + Hibernate                  |
| Banco de dados    | PostgreSQL 14+                               |
| Segurança         | Spring Security 6 (form login + BCrypt)      |
| Validação         | Bean Validation (Jakarta Validation)         |
| Build             | Maven (com `mvnw` incluído)                  |
| Boilerplate       | Lombok                                       |
| Logging           | SLF4J + Logback                              |
| DevTools          | Spring Boot DevTools (hot-reload em dev)     |

---

## 2. Pré-requisitos

Antes de rodar o projeto, instale:

### -> Obrigatórios

- **JDK 21** (qualquer distribuição: Temurin, Liberica, Microsoft, Zulu...)
  - Verifique: `java -version` deve mostrar 21.x
- **PostgreSQL 14 ou superior**

> 💡 **Não é preciso instalar o Maven manualmente**: o projeto já inclui o
> `mvnw` (Maven Wrapper). Use `./mvnw` no Linux/Mac ou `mvnw.cmd` no Windows.

---

## 3. Como rodar o projeto

### Passo 1 — Clonar o repositório

### Passo 2 — Subir o PostgreSQL e criar o banco
O nome do banco esperado é `patinhas`.

### Passo 3 — Configurar variáveis de ambiente

As credenciais sensíveis **não ficam no repositório**. Copie o template e defina os valores localmente:

```bash
cp .env.example .env
```

Edite o `.env` e preencha:

| Variável               | Descrição                                              |
|------------------------|--------------------------------------------------------|
| `DB_USERNAME`          | Usuário do PostgreSQL                                  |
| `DB_PASSWORD`          | Senha do PostgreSQL                                    |
| `ADMIN_SENHA_INICIAL`  | Senha do admin criado na primeira execução (`DataSeeder`) |

**Windows (PowerShell):**

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "sua_senha_postgres"
$env:ADMIN_SENHA_INICIAL = "sua_senha_admin"
```

**Linux / Mac:**

```bash
export DB_USERNAME="postgres"
export DB_PASSWORD="sua_senha_postgres"
export ADMIN_SENHA_INICIAL="sua_senha_admin"
```

### Passo 4 — Rodar a aplicação
**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / Mac:**

```bash
./mvnw spring-boot:run
```

Ou, alternativamente, gerar o `.jar`:

```bash
./mvnw clean package -DskipTests
java -jar target/patinhas-0.0.1-SNAPSHOT.jar
```

A aplicação sobe em **http://localhost:8080**.

### Passo 5 — Login administrativo
Na primeira execução, o `DataSeeder` cria:
- Um administrador padrão.
- Um registro de informações institucionais default.
| Campo  | Valor padrão               |
|--------|----------------------------|
| E-mail | `admin@patinhas.org`        |
| Senha  | valor de `ADMIN_SENHA_INICIAL` |

> 🔐 **Use uma senha forte em `ADMIN_SENHA_INICIAL`** e troque-a após o primeiro acesso, se necessário.

---

## 4. Como configurar o PostgreSQL

### PostgreSQL nativo

1. Instale o PostgreSQL ([downloads oficiais](https://www.postgresql.org/download/)).
2. Acesse o `psql` como superusuário:
   ```bash
   psql -U postgres
   ```
3. Crie o banco:
   ```sql
   CREATE DATABASE patinhas;
   ```
4. (Opcional) Crie um usuário dedicado:
   ```sql
   CREATE USER patinhas_user WITH ENCRYPTED PASSWORD 'umaSenhaSegura';
   GRANT ALL PRIVILEGES ON DATABASE patinhas TO patinhas_user;
   ```
5. Defina `DB_USERNAME` e `DB_PASSWORD` com as credenciais do usuário criado (via `.env` ou variáveis de ambiente).

### Schema

Não é preciso rodar nenhum SQL manualmente: a propriedade
`spring.jpa.hibernate.ddl-auto=update` faz o Hibernate criar/atualizar as
tabelas automaticamente na primeira execução.

> Para produção, recomenda-se trocar `update` por `validate` e versionar o
> schema com **Flyway** ou **Liquibase**.

---

## 5. Arquitetura

O projeto segue **arquitetura em camadas** (Layered Architecture) inspirada
em Clean Architecture, com responsabilidades bem definidas:

```
┌─────────────────────────────────────────────────────────┐
│  controller/  (Web MVC + REST API)                       │
│      ↓                                                   │
│  service/     (Regras de negócio + transações)           │
│      ↓                                                   │
│  repository/  (Acesso a dados — Spring Data JPA)         │
│      ↓                                                   │
│  entity/      (Modelo de domínio JPA)                    │
└─────────────────────────────────────────────────────────┘

  Suporte:
   - dto/        → Request/Response (entrada e saída)
   - security/   → Configuração e UserDetailsService
   - exception/  → Tratamento global de erros + ApiError
   - config/     → Beans de configuração + DataSeeder
   - util/       → Utilitários comuns
```

### Princípios aplicados

- **Separation of Concerns**: cada camada tem uma única responsabilidade.
- **DTOs em todas as fronteiras**: `Entity` nunca trafega entre camadas
  externas (controller ↔ cliente).
- **Validações com Bean Validation**: anotações em DTOs + `@Valid` nos
  controllers.
- **Tratamento global de erros** via `@RestControllerAdvice`.
- **Soft delete** para Animais e Eventos (campo `ativo`), preservando o
  histórico institucional.
- **Senhas com BCrypt** — nunca armazenadas em texto plano.
- **Anonimato real nas denúncias**: nenhuma informação do denunciante é
  capturada nem armazenada.

---

## 6. Estrutura de pastas

```
patinhas/
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/
├── README.md
└── src/
    ├── main/
    │   ├── java/br/com/patinhas/
    │   │   ├── PatinhasApplication.java        ← entrada da aplicação
    │   │   │
    │   │   ├── config/
    │   │   │   ├── DataSeeder.java             ← cria admin + info inicial
    │   │   │   └── WebConfig.java
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── web/                        ← controllers públicos (Thymeleaf)
    │   │   │   │   ├── HomeController.java
    │   │   │   │   ├── AnimalWebController.java
    │   │   │   │   └── DenunciaWebController.java
    │   │   │   ├── admin/                      ← controllers do painel
    │   │   │   │   ├── AdminDashboardController.java
    │   │   │   │   ├── AdminAnimalController.java
    │   │   │   │   ├── AdminEventoController.java
    │   │   │   │   ├── AdminDenunciaController.java
    │   │   │   │   └── AdminInformacaoONGController.java
    │   │   │   └── api/                        ← endpoints REST (JSON)
    │   │   │       ├── AnimalApiController.java
    │   │   │       ├── DenunciaApiController.java
    │   │   │       ├── EventoApiController.java
    │   │   │       ├── InformacaoONGApiController.java
    │   │   │       └── AuthApiController.java
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── request/                    ← AnimalRequestDTO, etc.
    │   │   │   └── response/                   ← AnimalResponseDTO, ApiResponse, etc.
    │   │   │
    │   │   ├── entity/
    │   │   │   ├── Animal.java
    │   │   │   ├── Usuario.java
    │   │   │   ├── Denuncia.java
    │   │   │   ├── Evento.java
    │   │   │   ├── InformacaoONG.java
    │   │   │   └── enums/                      ← PorteAnimal, SexoAnimal,
    │   │   │                                     StatusAdocao, RoleUsuario,
    │   │   │                                     StatusDenuncia
    │   │   ├── repository/                     ← interfaces JpaRepository
    │   │   ├── service/                        ← regras de negócio
    │   │   │
    │   │   ├── security/
    │   │   │   ├── SecurityConfig.java         ← Spring Security 6
    │   │   │   └── CustomUserDetailsService.java
    │   │   │
    │   │   ├── exception/
    │   │   │   ├── ApiError.java
    │   │   │   ├── BusinessException.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   │
    │   │   └── util/
    │   │       └── DateUtil.java
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── templates/                      ← Thymeleaf
    │       │   ├── layout/base.html
    │       │   ├── fragments/{header,footer}.html
    │       │   ├── index.html
    │       │   ├── animais.html
    │       │   ├── animal-detalhe.html
    │       │   ├── denuncia.html
    │       │   ├── login.html
    │       │   ├── sobre.html
    │       │   ├── erro.html
    │       │   └── admin/
    │       │       ├── dashboard.html
    │       │       ├── animais.html
    │       │       ├── animal-form.html
    │       │       ├── eventos.html
    │       │       ├── evento-form.html
    │       │       ├── denuncias.html
    │       │       ├── denuncia-detalhe.html
    │       │       └── informacoes.html
    │       └── static/
    │           ├── css/{style.css, dashboard.css}
    │           ├── js/app.js
    │           └── img/
    └── test/                                   ← testes 
```

---

## 7. Entidades do domínio

### 7.1 `Animal`

Representa um animal cadastrado pela ONG.

| Campo          | Tipo            | Observações                                         |
|----------------|-----------------|------------------------------------------------------|
| id             | Long            | PK auto-gerada                                       |
| nome           | String(100)     | obrigatório                                          |
| idade          | Integer         | em anos                                              |
| descricao      | String(1000)    | livre                                                |
| porte          | `PorteAnimal`   | enum: PEQUENO / MEDIO / GRANDE                       |
| sexo           | `SexoAnimal`    | enum: MACHO / FEMEA                                  |
| statusAdocao   | `StatusAdocao`  | enum: DISPONIVEL / EM_PROCESSO / ADOTADO / INDISPONIVEL |
| castrado       | Boolean         |                                                      |
| vacinado       | Boolean         |                                                      |
| fotoUrl        | String(500)     | URL externa (S3, Cloudinary, etc.)                   |
| dataCadastro   | LocalDateTime   | preenchida automaticamente                           |
| ativo          | Boolean         | soft delete                                          |

### 7.2 `Usuario`

Apenas administradores. Senha sempre criptografada com BCrypt.

| Campo        | Tipo          | Observações                                |
|--------------|---------------|---------------------------------------------|
| id           | Long          | PK                                          |
| nome         | String(150)   |                                             |
| email        | String(150)   | único, usado como login                     |
| senha        | String(255)   | BCrypt hash                                 |
| role         | `RoleUsuario` | ADMIN ou USUARIO                            |
| ativo        | Boolean       | desativados não conseguem logar             |
| dataCriacao  | LocalDateTime |                                             |

### 7.3 `Denuncia`

100% anônima — nenhum dado do denunciante é guardado.

| Campo                | Tipo              | Observações                              |
|----------------------|-------------------|-------------------------------------------|
| id                   | Long              | PK                                        |
| descricao            | String(2000)      | obrigatória                               |
| local                | String(255)       | obrigatório                               |
| dataOcorrido         | LocalDate         | opcional                                  |
| dataEnvio            | LocalDateTime     | preenchida automaticamente                |
| status               | `StatusDenuncia`  | PENDENTE / EM_ANALISE / EM_ATENDIMENTO / RESOLVIDA / ARQUIVADA |
| observacoesInternas  | String(2000)      | uso exclusivo da administração            |

### 7.4 `Evento`

| Campo       | Tipo           | Observações |
|-------------|----------------|-------------|
| id          | Long           | PK          |
| titulo      | String(150)    |             |
| descricao   | String(2000)   |             |
| local       | String(255)    |             |
| dataEvento  | LocalDateTime  |             |
| ativo       | Boolean        | soft delete |

### 7.5 `InformacaoONG`

Registro **único** com os dados institucionais exibidos na página "Sobre".

| Campo           | Tipo         |
|-----------------|--------------|
| nomeONG         | String(150)  |
| quemSomos       | String(4000) |
| proposito       | String(4000) |
| pixDoacao       | String(255)  |
| enderecoDoacao  | String(500)  |
| telefoneContato | String(50)   |
| emailContato    | String(150)  |
| instagram       | String(255)  |
| facebook        | String(255)  |

---

## 8. Rotas (Web e API)

### 8.1 Rotas Web públicas

| Método | URL              | Descrição                                |
|--------|------------------|-------------------------------------------|
| GET    | `/`              | Página inicial com destaques              |
| GET    | `/home`          | Idem                                      |
| GET    | `/animais`       | Lista pública de animais (com filtros)    |
| GET    | `/animais/{id}`  | Detalhe do animal                         |
| GET    | `/sobre`         | Informações institucionais                |
| GET    | `/denuncia`      | Formulário de denúncia anônima            |
| POST   | `/denuncia`      | Submete denúncia anônima                  |
| GET    | `/login`         | Tela de login                             |
| POST   | `/login`         | Autentica (form login do Spring Security) |
| POST   | `/logout`        | Encerra a sessão                          |
| GET    | `/erro`          | Página de erro                            |

### 8.2 Rotas Web administrativas (exigem ROLE_ADMIN)

| Método | URL                                    | Descrição                              |
|--------|----------------------------------------|-----------------------------------------|
| GET    | `/admin/dashboard`                     | Painel com indicadores                  |
| GET    | `/admin/animais`                       | Lista todos os animais                  |
| GET    | `/admin/animais/novo`                  | Formulário de novo animal               |
| POST   | `/admin/animais`                       | Cria animal                             |
| GET    | `/admin/animais/{id}/editar`           | Formulário de edição                    |
| POST   | `/admin/animais/{id}`                  | Atualiza animal                         |
| POST   | `/admin/animais/{id}/remover`          | Soft delete                             |
| GET    | `/admin/eventos`                       | Lista eventos                           |
| GET    | `/admin/eventos/novo`                  | Formulário                              |
| POST   | `/admin/eventos`                       | Cria evento                             |
| GET    | `/admin/eventos/{id}/editar`           | Formulário de edição                    |
| POST   | `/admin/eventos/{id}`                  | Atualiza evento                         |
| POST   | `/admin/eventos/{id}/remover`          | Soft delete                             |
| GET    | `/admin/denuncias`                     | Lista denúncias (filtro por status)     |
| GET    | `/admin/denuncias/{id}`                | Detalhe                                 |
| POST   | `/admin/denuncias/{id}`                | Atualiza status / observações internas  |
| GET    | `/admin/informacoes`                   | Edita informações institucionais        |
| POST   | `/admin/informacoes`                   | Salva informações institucionais        |

### 8.3 API REST

Todas as respostas seguem o envelope `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso.",
  "data": { },
  "timestamp": "2026-05-08T12:00:00"
}
```

#### Públicas (`/api/v1/public/**`)

| Método | URL                                | Descrição                                    |
|--------|------------------------------------|-----------------------------------------------|
| GET    | `/api/v1/public/animais`           | Lista animais (filtros: `nome`, `status`)     |
| GET    | `/api/v1/public/animais/{id}`      | Detalhe                                       |
| GET    | `/api/v1/public/eventos`           | Eventos ativos                                |
| GET    | `/api/v1/public/eventos/proximos`  | Eventos futuros                               |
| GET    | `/api/v1/public/informacoes`       | Informações institucionais                    |
| POST   | `/api/v1/public/denuncias`         | Cria denúncia anônima (CSRF dispensado)       |

#### Administrativas (`/api/v1/admin/**` — exigem ROLE_ADMIN)

| Método | URL                                   | Descrição                       |
|--------|---------------------------------------|----------------------------------|
| GET    | `/api/v1/admin/animais`               | Listar todos                     |
| POST   | `/api/v1/admin/animais`               | Cadastrar                        |
| PUT    | `/api/v1/admin/animais/{id}`          | Atualizar                        |
| DELETE | `/api/v1/admin/animais/{id}`          | Soft delete                      |
| GET    | `/api/v1/admin/eventos`               | Listar todos                     |
| POST   | `/api/v1/admin/eventos`               | Cadastrar                        |
| PUT    | `/api/v1/admin/eventos/{id}`          | Atualizar                        |
| DELETE | `/api/v1/admin/eventos/{id}`          | Soft delete                      |
| GET    | `/api/v1/admin/denuncias`             | Listar (filtro `status`)         |
| GET    | `/api/v1/admin/denuncias/{id}`        | Detalhar                         |
| PUT    | `/api/v1/admin/denuncias/{id}/status` | Atualizar status / observações   |
| PUT    | `/api/v1/admin/informacoes`           | Atualizar dados institucionais   |
| GET    | `/api/v1/me`                          | Verifica usuário logado          |

---

## 9. Autenticação e segurança

### 9.1 Modelo

- **Form login** com sessão (cookie `JSESSIONID`).
- Login via **e-mail + senha**.
- Senhas armazenadas com **BCrypt** (`BCryptPasswordEncoder`).
- `CustomUserDetailsService` busca o usuário pela coluna `email` e
  associa as authorities baseadas em `RoleUsuario`.
- Apenas usuários **com role `ADMIN` e `ativo=true`** conseguem entrar.

### 9.2 Política de rotas (`SecurityConfig`)

| Padrão                                         | Acesso        |
|------------------------------------------------|---------------|
| `/css/**`, `/js/**`, `/img/**`, `/webjars/**`  | público       |
| `/`, `/home`, `/animais/**`, `/denuncia/**`,   |               |
| `/sobre`, `/login/**`, `/erro`, `/error`       | público       |
| `/api/v1/public/**`                            | público       |
| `/admin/**`                                    | ROLE_ADMIN    |
| `/api/v1/admin/**`                             | ROLE_ADMIN    |
| Demais                                         | autenticado   |

### 9.3 Outros ajustes

- **CSRF habilitado** no fluxo Web (forms com `_csrf` automático via Thymeleaf).
- **CSRF desabilitado apenas** para `/api/v1/public/**` (denúncias anônimas).
- Sessão única por usuário (`maximumSessions(1)`).
- Página customizada de **403** em `/erro?codigo=403`.
- Logout via `POST /logout` redireciona para `/?logout`.

### 9.4 Boas práticas em produção

- Trocar a senha padrão imediatamente.
- Configurar HTTPS (TLS) na frente da aplicação.
- Usar variáveis de ambiente para credenciais.
- Habilitar headers de segurança adicionais (HSTS, CSP).
- Trocar `ddl-auto=update` por `validate` + Flyway.

---

## 10. Fluxo do sistema

### 10.1 Visitante

1. Acessa `/` e vê os destaques (animais e eventos).
2. Pode navegar até `/animais` para ver a lista completa, com filtros.
3. Pode acessar `/sobre` para conhecer a ONG.
4. Pode enviar uma denúncia em `/denuncia` (totalmente anônima).

### 10.2 Administrador

1. Acessa `/login` e autentica.
2. É redirecionado para `/admin/dashboard`.
3. Gerencia:
   - **Animais** — cria, edita, atualiza status (DISPONIVEL → ADOTADO etc.), remove.
   - **Eventos** — cria, edita, ativa/desativa.
   - **Denúncias** — visualiza, atualiza status, registra observações internas.
   - **Informações da ONG** — atualiza textos institucionais.
4. Sai pelo botão "Sair".

### 10.3 Inicialização

Na primeira execução, o `DataSeeder`:

- Cria o admin padrão (credenciais via variável `ADMIN_SENHA_INICIAL`).
- Cria um registro inicial em `informacao_ong` com placeholders.

---

## Licença

Projeto educacional — sinta-se livre para usar como base de estudos ou para
o seu próprio projeto de ONG. 🐶🐱❤️
