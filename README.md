# 🔧 TURBO GARAGE

**Gestão de clientes, veículos e ordens de serviço para oficina mecânica**

Projeto Integrador · Curso Técnico SENAI · Web & Backend (Partes 3 e 4)

**Alunos:** Guilherme Felberque e Danilo Dorella

---

## Sumário
1. [Sobre o projeto](#1-sobre-o-projeto)
2. [Tecnologias](#2-tecnologias)
3. [Como rodar](#3-como-rodar)
4. [Telas do sistema](#4-telas-do-sistema)
5. [Perfis de acesso](#5-perfis-de-acesso)
6. [Arquitetura em camadas](#6-arquitetura-em-camadas)
7. [Banco de dados](#7-banco-de-dados)
8. [Endpoints da API](#8-endpoints-da-api)
9. [Segurança](#9-segurança)
10. [Requisitos atendidos](#10-requisitos-atendidos)
11. [Roteiro de apresentação](#11-roteiro-de-apresentação)
12. [Observações técnicas](#12-observações-técnicas)

---

## 1. Sobre o projeto

A **Turbo Garage** é uma aplicação web para uma oficina mecânica. Ela permite:

- que **clientes** se cadastrem no site, façam login e cadastrem os próprios veículos;
- que o **administrador** da oficina consulte os clientes e gerencie os veículos e as ordens de serviço.

**Identidade visual:** flat design de alto contraste, em preto, amarelo e branco, inspirado em faixas de segurança e no universo automotivo.

---

## 2. Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.1.1 (Spring Web MVC) |
| Acesso ao banco | Spring JDBC (`JdbcTemplate`) |
| Banco de dados | MySQL (driver `mysql-connector-java` 5.1.49) |
| Criptografia | BCrypt (`jbcrypt` 0.4) |
| Validação | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| Front-end | HTML5, CSS3 e JavaScript puro (`fetch()`) |
| API pública | [ViaCEP](https://viacep.com.br) |
| Build | Maven |

---

## 3. Como rodar

### Pré-requisitos
- Java 25 (JDK)
- MySQL instalado e ligado
- IntelliJ IDEA ou VS Code (com o *Extension Pack for Java*)

### Passo a passo
1. **Abra o projeto** pela pasta `turbogarage`, a que tem o `pom.xml`, e deixe o Maven baixar as dependências.
2. **Configure o banco** em `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=        # coloque a senha do seu MySQL
   ```
3. **Defina o administrador** no mesmo arquivo:
   ```properties
   app.admin.email=seuemail@gmail.com
   app.admin.senha=suaSenha
   ```
4. **Rode** a classe `DemoApplication.java`.
5. **Acesse** http://localhost:8080

> Não é preciso criar o banco nem as tabelas à mão. Quando o servidor liga, ele faz três coisas sozinho:
> - cria o banco `login_db`;
> - executa o `schema.sql`, que cria as tabelas se elas ainda não existirem;
> - cria a conta do administrador, com a senha criptografada em BCrypt.

---

## 4. Telas do sistema

| Tela | Arquivo | Quem acessa | O que faz |
|---|---|---|---|
| Login | `index.html` | Todos | Entrada com email e senha. Mostra uma mensagem de erro se as credenciais estiverem erradas |
| Cadastro | `cadastro.html` | Todos | Cadastro do cliente: 6 campos obrigatórios, mais email e senha de acesso |
| Boas-vindas | `home.html` | Logados | Imagem em tela cheia com saudação e atalhos de acordo com o perfil |
| Clientes | `cadastros.html` | Admin | Lista dos clientes, busca por ID, ficha completa e exclusão |
| Veículos | `veiculos.html` | Admin e cliente | **Admin:** CRUD completo. **Cliente:** só o formulário para cadastrar o próprio veículo |
| Ordens de Serviço | `ordens.html` | Admin | CRUD completo, com filtro por status e total em R$ |

**Menu de navegação:** Início · Clientes · Veículos · Ordens de Serviço. Cada pessoa vê só os itens que pode acessar.

---

## 5. Perfis de acesso

| Ação | Visitante | Cliente | Admin |
|---|:---:|:---:|:---:|
| Criar conta (cadastro) | ✅ | ✅ | ✅ |
| Fazer login / ver boas-vindas | | ✅ | ✅ |
| Cadastrar o **próprio** veículo | | ✅ | ✅ |
| Ver a lista "Veículos cadastrados" | | | ✅ |
| Editar / excluir veículos | | | ✅ |
| Ver clientes cadastrados | | | ✅ |
| CRUD de ordens de serviço | | | ✅ |

O administrador é o email definido em `app.admin.email`. Só pode existir **um** administrador: se você trocar o email no arquivo, o antigo perde o acesso.

---

## 6. Arquitetura em camadas

```
turbogarage/
├── pom.xml
├── banco.sql                        → script do banco + consultas para a auditoria
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java     → inicia o servidor
    │   ├── model/                   → MODEL: Usuario, Cadastro, Veiculo, OrdemServico
    │   ├── dto/                     → dados que chegam das telas (com validação)
    │   │                              LoginRequest, CadastroRequest, VeiculoRequest, OrdemServicoRequest
    │   ├── repository/              → REPOSITORY: SQL com JdbcTemplate
    │   ├── service/                 → SERVICE: regras de negócio
    │   ├── controller/              → CONTROLLER: endpoints REST + ErroHandler
    │   ├── config/                  → AdminInterceptor (permissões), WebConfig, DadosIniciais
    │   └── util/SenhaUtil.java      → criptografia BCrypt
    └── resources/
        ├── application.properties   → configurações (banco, admin)
        ├── schema.sql               → criação automática das tabelas
        └── static/                  → telas (HTML, CSS, JS, imagem)
```

**Fluxo de uma requisição:**
```
Tela (fetch) → Controller → Service (regras) → Repository (SQL) → MySQL
```

---

## 7. Banco de dados

### Tabelas

**`usuarios`**: contas de login
| Coluna | Tipo | Observação |
|---|---|---|
| id | INT | PK, auto incremento |
| email | VARCHAR(150) | único |
| senha_hash | VARCHAR(255) | **hash BCrypt**, nunca a senha pura |
| perfil | VARCHAR(20) | `ADMIN` ou `CLIENTE` |

**`cadastros`**: dados dos clientes
| Coluna | Tipo | Observação |
|---|---|---|
| id | INT | PK |
| nome | VARCHAR(150) | obrigatório |
| email | VARCHAR(150) | liga ao login |
| endereco | VARCHAR(200) | obrigatório |
| cep | VARCHAR(9) | formato 00000-000 |
| numero | VARCHAR(10) | obrigatório |
| telefone | VARCHAR(20) | obrigatório |
| observacao | TEXT | obrigatório |
| data_criacao | DATETIME | preenchida automaticamente |
| ativo | TINYINT(1) | exclusão lógica (soft delete) |

**`veiculos`**: Parte 4
| Coluna | Tipo | Observação |
|---|---|---|
| id | INT | PK |
| placa | VARCHAR(8) | única (ABC-1234 ou ABC1D23) |
| modelo | VARCHAR(100) | obrigatório |
| ano | INT | 1950 a 2100 |
| cadastro_id | INT | **FK → cadastros.id** |

**`ordens_servico`**: Parte 4
| Coluna | Tipo | Observação |
|---|---|---|
| id | INT | PK |
| veiculo_id | INT | **FK → veiculos.id** |
| defeito | VARCHAR(500) | obrigatório |
| valor | DECIMAL(10,2) | em R$ |
| status | VARCHAR(20) | ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA |
| data_abertura | DATETIME | preenchida automaticamente |

### Relacionamentos
```
cadastros (1) ───< (N) veiculos (1) ───< (N) ordens_servico
```
Um cliente pode ter vários veículos. Um veículo pode ter várias ordens de serviço.

---

## 8. Endpoints da API

### Autenticação
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/login` | Todos | Login (JSON `{ email, senha }`) |
| GET | `/api/usuario` | Logado | Quem está logado e se é admin |
| POST | `/api/logout` | Logado | Encerra a sessão |

### Cadastros (clientes)
| Método | Rota | Acesso | Resposta |
|---|---|---|---|
| POST | `/api/cadastros` | Todos | 201: "Cadastro realizado com sucesso!" |
| GET | `/api/cadastros` | Admin | 200: lista |
| GET | `/api/cadastros/{id}` | Admin | 200 ou 404 |
| DELETE | `/api/cadastros/{id}` | Admin | 200 (soft delete; também remove o login do cliente) |

### Veículos
| Método | Rota | Acesso | Resposta |
|---|---|---|---|
| GET | `/api/veiculos` | Admin | 200: lista |
| GET | `/api/veiculos/{id}` | Admin | 200 ou 404 |
| POST | `/api/veiculos` | **Admin e cliente** | 201: "Registro salvo com sucesso!" |
| PUT | `/api/veiculos/{id}` | Admin | 200: "Registro atualizado" |
| DELETE | `/api/veiculos/{id}` | Admin | 200: "Registro excluído" |

### Ordens de serviço
| Método | Rota | Acesso | Resposta |
|---|---|---|---|
| GET | `/api/ordens` | Admin | 200: lista |
| GET | `/api/ordens/{id}` | Admin | 200 ou 404 |
| POST | `/api/ordens` | Admin | 201: "Registro salvo com sucesso!" |
| PUT | `/api/ordens/{id}` | Admin | 200: "Registro atualizado" |
| DELETE | `/api/ordens/{id}` | Admin | 200: "Registro excluído" |

### Códigos de erro (tratamento global: `ErroHandler`)
| Código | Quando |
|---|---|
| 400 | Campo obrigatório vazio ou inválido, ou JSON mal formado |
| 401 | Não está logado |
| 403 | Logado, mas sem permissão (não é admin) |
| 404 | Registro não encontrado |
| 409 | Email ou placa repetidos, ou exclusão de veículo que tem ordens |
| 500 | Erro inesperado (a mensagem mostra o motivo) |

Todos os erros voltam como JSON `{ "mensagem": "..." }` e aparecem na tela.

---

## 9. Segurança

- **Senhas com BCrypt:** a senha é transformada em hash com *salt* aleatório antes de ser salva. No banco aparece algo como `$2a$10$...`, nunca a senha real.
- **Mensagem de login genérica:** "Email ou senha inválidos" não revela qual dos dois está errado.
- **Sessão no servidor:** o login fica guardado na sessão (`HttpSession`), que expira em 30 minutos.
- **Permissões no back-end:** o `AdminInterceptor` bloqueia as rotas protegidas no Java, não só escondendo botões na tela.
- **Veículo do cliente:** ao cadastrar um veículo, o Java ignora o `clienteId` enviado pelo cliente e usa o cadastro de quem está logado.
- **SQL Injection:** todas as consultas usam parâmetros `?` (PreparedStatement).
- **XSS:** os dados aparecem nas telas com `textContent`, nunca com `innerHTML`.

---

## 10. Requisitos atendidos

### Parte 3: Integração end-to-end
- [x] Nome do produto e dos alunos no cabeçalho
- [x] Estilo visual de alto contraste (flat design)
- [x] Tela de login com mensagem de erro visual
- [x] Tela de boas-vindas com imagem em tela cheia
- [x] Cadastro com os 6 campos obrigatórios e consulta dos registros
- [x] Mensagem "Cadastro realizado com sucesso!"
- [x] Arquitetura Controller / Service / Repository / Model
- [x] Senha criptografada (BCrypt)
- [x] Conexão com MySQL via Spring JDBC
- [x] Endpoints `/api/login`, `POST /api/cadastros`, `GET /api/cadastros`, `GET /api/cadastros/{id}`
- [x] ⭐ Desafio 1: máscaras de CEP e telefone
- [x] ⭐ Desafio 2: ViaCEP preenche o endereço
- [x] ⭐ Desafio 3: `data_criacao` automática e exclusão (soft delete)

### Parte 4: APIs públicas e Multi-CRUD
- [x] API pública integrada (ViaCEP)
- [x] Nova tela 1 com CRUD completo: **Veículos**
- [x] Nova tela 2 com CRUD completo: **Ordens de Serviço**
- [x] Busca e filtro nas listagens
- [x] Botão **Editar**, que carrega os dados no formulário
- [x] Botão **Excluir** com janela de confirmação
- [x] Avisos "Registro salvo", "Registro atualizado" e "Registro excluído"
- [x] Menu de navegação entre todas as telas
- [x] Endpoints `GET`, `POST`, `PUT` e `DELETE`
- [x] ⭐ Desafio 1: relacionamento entre entidades (chaves estrangeiras)
- [x] ⭐ Desafio 2: exportação para Excel (.csv)
- [x] ⭐ Desafio 3: tratamento global de exceções (`@RestControllerAdvice`)

---

## 11. Roteiro de apresentação

### Parte 3
1. **Senha incorreta:** tente entrar com uma senha errada e mostre a mensagem vermelha.
2. **Inserção:** clique em "Cadastre-se" e cadastre 3 clientes, cada um com um email diferente.
3. **Feedback:** mostre a mensagem "Cadastro realizado com sucesso!".
4. **Transição de sessão:** entre como cliente e mostre a tela com a imagem em tela cheia.
5. **Recuperação individual:** entre como admin → **Clientes** → clique em "Ver" em cada um.
6. **Auditoria:** no MySQL Workbench, rode o comando abaixo e mostre que as senhas estão em hash.
   ```sql
   SELECT email, perfil, senha_hash FROM usuarios;
   ```

### Parte 4
1. **API pública:** no cadastro, digite um CEP (ex.: `01001000`) e mostre o endereço sendo preenchido sozinho.
2. **Navegação:** como admin, abra **Veículos** e **Ordens de Serviço** pelo menu.
3. **CRUD de Veículos:** cadastre, busque, edite (aparece "Registro atualizado") e exclua com confirmação (aparece "Registro excluído").
4. **CRUD de Ordens:** cadastre, filtre por status, edite e exclua.
5. **Persistência:** no Workbench, rode:
   ```sql
   SELECT * FROM veiculos;
   SELECT * FROM ordens_servico;
   ```
6. **Feedback:** mostre os avisos verdes e a janela de confirmação.
7. **Extra:** clique em **Exportar Excel** e abra o arquivo.
8. **Cliente cadastrando veículo:** entre como cliente → "Cadastrar meu veículo". Mostre que ele não vê a lista nem as outras telas. Depois, como admin, mostre o veículo na lista com o nome do cliente.

> Outras consultas prontas para a apresentação estão no fim do arquivo `banco.sql`.

---

## 12. Observações técnicas

- **Por que Spring JDBC e não JPA?** O JPA/Hibernate do Spring Boot 4 exige MySQL 8 ou mais novo. O MySQL usado no projeto é uma versão antiga (driver 5.1.49). O enunciado da Parte 3 aceita "Spring Data JPA **ou** Spring JDBC". A arquitetura em camadas é a mesma.
- **Tabelas antigas:** se o banco já tinha tabelas de uma versão anterior, a classe `DadosIniciais` adiciona sozinha as colunas novas (`perfil` e `email`).
- **ViaCEP precisa de internet:** sem conexão, o cadastro funciona normalmente, mas o endereço precisa ser digitado à mão.
- **Regras de negócio:**
  - a placa não pode se repetir;
  - um veículo com ordens de serviço não pode ser excluído; exclua as ordens antes;
  - um cliente excluído perde o acesso ao login.
