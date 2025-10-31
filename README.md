
```markdown
# 🏆 GRAwards

O **GRAwards** é uma aplicação **Spring Boot (Java 21)** com **Maven** e **banco de dados H2** que realiza a **importação automática de dados a partir de um arquivo CSV** ao iniciar o projeto.  
O projeto também inclui um **Dockerfile multi-stage otimizado**, facilitando a construção e execução do container.

---

## 🚀 Funcionalidades principais

- Importa dados de um arquivo CSV automaticamente ao iniciar  
- Banco de dados H2 em memória (ou persistido em arquivo, se configurado)  
- Build automatizado com Maven  
- Imagem Docker leve e segura (baseada em Eclipse Temurin JRE 21)  
- Execução simples com Docker  

---

## ⚙️ Carga automática de CSV

A importação é feita automaticamente ao iniciar o Spring Boot, utilizando o metodo init dentro do MovieService, através do @PostConstruct.

Exemplo:

```java
    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void init() {
        try {
            var reader = Files.newBufferedReader(Paths.get("src/main/resources/data/Movielist.csv"));

            var moviesDTO = new CsvToBeanBuilder<MovieDTO>(reader)
                    .withType(MovieDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build()
                    .parse();

            var moviesPersist = moviesDTO.stream().map(Movie::new).toList();

            movieRepository.saveAll(moviesPersist);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar os filmes", e);
        }
    }
````

Porém foi implementado um metodo caso seja necessario informar mais arquivos, atraves do Endpoint Rest a seguir:

````
  /movie/import
````
Lembrando que o arquivo precisa seguir o mesmo padrao de dados no arquivo base.

---
Na pasta examples, possui um arquivo com o cUrl necessario para executar o processo pedido.

📦 Dependência usada: [OpenCSV](http://opencsv.sourceforge.net/)

```xml
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.9</version>
</dependency>
```

---

## 🐳 Docker

### 📄 Dockerfile

```Dockerfile
# ============================================================
# Etapa 1 — Build da aplicação com Maven
# ============================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# ============================================================
# Etapa 2 — Imagem final (runtime leve com JRE 21)
# ============================================================
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

---

### 📦 .dockerignore

```
target/
.git/
.idea/
```

---

## 🧰 Comandos Docker

### 1️⃣ Build da imagem

```bash
docker build -t grawards:latest .
```

### 2️⃣ Executar o container

```bash
docker run -d -p 8080:8080 --name grawards grawards:latest
```

A aplicação ficará acessível em:
👉 [http://localhost:8080](http://localhost:8080)

## 🧪 Executar localmente (sem Docker)

```bash
mvn spring-boot:run
```

Acesse:

* Aplicação: [http://localhost:8080](http://localhost:8080)
* Console H2: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---
