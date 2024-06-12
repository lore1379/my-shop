# Elaborato ATTSW: my-shop

Prototipo Maven di un semplice sito di e-commerce realizzato per l'esame di ATTSW usando il Behavior-driven development (BDD) process.
L'elaborato è eseguito su GitHub Actions (Java 8, Java 11, Java 17) e usa le seguenti tecnologie: 
- JUnit: framework di test.
- AssertJ: test dependency.
- Mockito: framework utilizzato per il Mocking.
- MongoDB: database per implementare la Repository.
- Picocli: parser per command line Java.
- PIT: framework per il mutation testing. 
- Cucumber: tool per il supporto del Behavior-Driven Development

Per eseguire la build Maven è necessario lanciare il comando: mvn verify

La documentazione dettagliata si trova nel file [Report.pdf
](https://github.com/lore1379/my-shop/blob/main/Report.pdf).

Risultato delle builds:

[![Java CI with Maven in Linux](https://github.com/lore1379/my-shop/actions/workflows/maven.yml/badge.svg)](https://github.com/lore1379/my-shop/actions/workflows/maven.yml)

[![Java CI with Maven, Mutation Testing for PR](https://github.com/lore1379/my-shop/actions/workflows/mutation-test.yml/badge.svg)](https://github.com/lore1379/my-shop/actions/workflows/mutation-test.yml)

CoverAlls:

[![Coverage Status](https://coveralls.io/repos/github/lore1379/my-shop/badge.svg)](https://coveralls.io/github/lore1379/my-shop)

SonarCloud:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=bugs)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=coverage)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=lore1379_my-shop&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=lore1379_my-shop)
