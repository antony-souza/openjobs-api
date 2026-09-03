# OpenJobs
> O projeto está em desenvolvimento e novas funcionalidades serão adicionadas gradualmente.
OpenJobs é uma API REST para publicação de vagas e gerenciamento de candidaturas, desenvolvida com Java e Spring Boot.

O projeto tem como objetivo explorar o desenvolvimento de aplicações backend utilizando o ecossistema Spring, organização modular e separação das regras de negócio através de casos de uso.

## Tecnologias

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Lombok
- SpringDoc OpenAPI
- Maven

## Funcionalidades

- Cadastro de usuários
- Publicação de vagas
- Listagem de vagas
- Visualização de vagas
- Candidatura a vagas
- Consulta de candidaturas

## Arquitetura

O projeto é organizado por módulos de domínio. Cada módulo concentra seus próprios models, repositories, controllers, DTOs e casos de uso.

```text
src/main/java/com/antony/openjobs/
│
├── OpenjobsApplication.java
│
├── modules/
│   │
│   ├── users/
│   │   ├── model/
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── usecases/
│   │   │   ├── CreateUserUseCase.java
│   │   │   └── FindUserUseCase.java
│   │   ├── controller/
│   │   │   └── UserController.java
│   │   └── dto/
│   │       └── CreateUserRequest.java
│   │
│   ├── jobs/
│   │   ├── model/
│   │   │   └── Job.java
│   │   ├── repository/
│   │   │   └── JobRepository.java
│   │   ├── usecases/
│   │   │   ├── CreateJobUseCase.java
│   │   │   ├── ListJobsUseCase.java
│   │   │   └── FindJobUseCase.java
│   │   ├── controller/
│   │   │   └── JobController.java
│   │   └── dto/
│   │
│   └── applications/
│       ├── model/
│       │   └── Application.java
│       ├── repository/
│       │   └── ApplicationRepository.java
│       ├── usecases/
│       │   └── ApplyToJobUseCase.java
│       ├── controller/
│       │   └── ApplicationController.java
│       └── dto/
│
├── config/
└── exception/
