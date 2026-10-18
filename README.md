# CPF – Catalogue des Formations Professionnelles (AMU)

Application Spring Boot de gestion des formations CPF de l'université Aix-Marseille.

## Prérequis

- Java 21
- Avoir un compte utilisateur AMU pour l'authentification CAS

## Lancer
Le profil `test` est actif par défaut. La base de données est en mémoire (H2), recréée à chaque démarrage.

```bash
git clone https://etulab.univ-amu.fr/ter1/gestion-des-formations-cpf
cd cpf
mvn spring-boot:run
```

L'application est accessible sur [http://localhost:8080](http://localhost:8080).  
La connexion redirige vers le CAS AMU (`ident.univ-amu.fr`).


## Build JAR

```bash
mvn clean package -DskipTests
java -jar target/cpf-0.0.1-SNAPSHOT.jar
```
