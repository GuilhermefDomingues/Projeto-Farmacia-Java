# Sistema de Gerenciamento de Farmácia

Projeto acadêmico desenvolvido para praticar Java, Spring Boot e APIs REST. Permite gerenciar medicamentos e clientes, registrar vendas e acompanhar o estoque por uma interface web.

## Tecnologias

- Java 17 e Spring Boot 4
- Maven
- Spring Web MVC e Spring Data JPA / Hibernate
- MySQL e Flyway
- Bean Validation, Lombok e ModelMapper
- HTML, CSS e JavaScript

## Funcionalidades

- Cadastro, listagem, consulta, atualização e exclusão de medicamentos.
- Cadastro, listagem, consulta e atualização de clientes.
- Registro e consulta de vendas.
- Cálculo do total da venda com base no preço cadastrado.
- Baixa automática do estoque ao registrar uma venda.
- Verificação de validade e disponibilidade do medicamento.
- Validação dos dados recebidos pela API.
- Interface web com busca e formulários.

Cada venda contém um cliente, um medicamento e uma quantidade. O preço unitário e o total ficam registrados no momento da venda.

## Organização

O código Java está em `src/main/java/com/gui/projetofarmacia`.

| Pacote | Responsabilidade |
|---|---|
| `controller` | Receber requisições HTTP e retornar respostas |
| `service` | Aplicar regras de negócio e coordenar operações |
| `repository` | Acessar os dados com Spring Data JPA |
| `model` | Representar as entidades persistidas |
| `dto` | Receber e validar os dados de entrada |
| `mapper` | Converter DTOs em entidades |
| `config` | Configurar o ModelMapper |

A interface está em `src/main/resources/static`. As migrations estão em `src/main/resources/db/migration`, separadas por tabela.

## Requisitos

- JDK 17 instalado e selecionado no IntelliJ ou em `JAVA_HOME`.
- MySQL em execução. O projeto foi verificado com MySQL 8.4.
- Conexão com a internet para baixar as dependências na primeira execução.

## Executar localmente

1. Baixe o ZIP do repositório e extraia os arquivos ou clone o projeto.
2. Abra a pasta que contém o `pom.xml` no IntelliJ e carregue o projeto Maven.
3. Crie o banco no MySQL.

```sql
CREATE DATABASE farmacia;
```

4. Em **Run → Edit Configurations → Environment variables**, configure os valores correspondentes ao seu MySQL.

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/farmacia?serverTimezone=UTC` |
| `DB_USER` | `root` ou outro usuário com acesso ao banco |
| `DB_PASSWORD` | Sua senha do MySQL |
| `PORT` | `8082` (opcional) |

5. Execute a classe `ProjetoFarmaciaApplication`.
6. Abra [http://localhost:8082/](http://localhost:8082/) no navegador.

O Flyway cria as tabelas ao iniciar a aplicação. Se configurar outra porta, ajuste o endereço no navegador.

### Executar pelo terminal

No PowerShell, na pasta do projeto, configure as variáveis para a sessão e execute o Maven Wrapper.

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/farmacia?serverTimezone=UTC"
$env:DB_USER = "seu_usuario"
$env:DB_PASSWORD = "sua_senha"
.\mvnw.cmd spring-boot:run
```

Use suas próprias credenciais. Mantenha senhas fora dos arquivos enviados ao repositório.

## Rotas da API

O endereço local padrão é `http://localhost:8082`.

| Método | Rota | Operação |
|---|---|---|
| POST | `/medicamentos` | Cadastrar medicamento |
| GET | `/medicamentos` | Listar medicamentos |
| GET | `/medicamentos/{id}` | Consultar medicamento |
| PUT | `/medicamentos/{id}` | Atualizar medicamento |
| DELETE | `/medicamentos/{id}` | Excluir medicamento |
| POST | `/clientes` | Cadastrar cliente |
| GET | `/clientes` | Listar clientes |
| GET | `/clientes/{id}` | Consultar cliente |
| PUT | `/clientes/{id}` | Atualizar cliente |
| POST | `/vendas` | Registrar venda |
| GET | `/vendas` | Listar vendas |
| GET | `/vendas/{id}` | Consultar venda |

## Roteiro para avaliação

As operações podem ser realizadas pela interface ou pelo Postman. Para requisições com corpo, selecione JSON e envie `Content-Type: application/json`.

### 1. Cadastrar medicamento

Envie um POST para `/medicamentos`.

```json
{
  "nome": "Paracetamol",
  "fabricante": "Genérico",
  "preco": 12.50,
  "quantidadeEstoque": 20,
  "dataValidade": "2028-12-31"
}
```

Utilize uma data de validade futura quando executar o teste. Guarde o ID retornado.

### 2. Cadastrar cliente

Envie um POST para `/clientes`.

```json
{
  "nome": "Ana Silva",
  "email": "ana@example.com",
  "telefone": "11999999999"
}
```

Guarde o ID retornado.

### 3. Registrar venda

Envie um POST para `/vendas`, usando os IDs retornados pelos cadastros.

```json
{
  "clienteId": 1,
  "medicamentoId": 1,
  "quantidade": 2
}
```

Com o preço de R$ 12,50 e duas unidades, o total esperado é R$ 25,00. Consulte o medicamento para verificar a redução do estoque de 20 para 18.

### 4. Verificar as regras

- Envie um cadastro de medicamento com preço negativo e confira a resposta 400.
- Consulte um ID inexistente e confira a resposta 404.
- Tente vender uma quantidade maior que o saldo e confira a resposta 409.
- Cadastre um medicamento vencido e tente vendê-lo. A resposta esperada é 409.
- Atualize um medicamento pelo PUT e consulte os novos dados.
- Exclua um medicamento que ainda não esteja vinculado a uma venda e confira a resposta 204.

Medicamentos vinculados a vendas são preservados. A tentativa de exclusão retorna 409.

## Testes

Com o banco e as variáveis de ambiente configurados, execute o teste de inicialização.

```powershell
.\mvnw.cmd test
```

Esse teste utiliza o banco configurado em `DB_URL`. Os cenários do roteiro acima podem ser verificados manualmente pela interface ou pelo Postman.

## Objetivo acadêmico

Praticar orientação a objetos, arquitetura em camadas, persistência, validação de dados, migrations e integração entre uma API REST e uma interface web.
