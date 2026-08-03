# Gestion Commerciale

Gestion Commerciale est une application de gestion commerciale moderne conçue pour centraliser la gestion des clients, produits, commandes et du stock. Le projet est structuré autour d’un backend Java Spring Boot et d’un frontend dédié.

## Vue d’ensemble

Cette application permet de :

- gérer les clients
- gérer les produits
- créer et suivre les commandes
- contrôler les niveaux de stock
- exposer une API REST documentée avec Swagger/OpenAPI

## Structure du dépôt

```text
GestionCommerciale/
├── backend/          # API Spring Boot
├── frontend/         # Interface utilisateur
├── screenshots/      # Captures d’écran
├── docs/             # Documentation complémentaire
├── .github/          # Workflows GitHub Actions
├── README.md         # Documentation principale
├── LICENSE           # Licence du projet
└── .gitignore        # Fichiers à ignorer
```

## Stack technique

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- GitHub Actions pour la CI

## Prérequis

Avant de lancer le projet, assurez-vous d’avoir :

- Java 21
- Maven
- Node.js et npm (si vous utilisez le frontend)
- PostgreSQL

## Démarrage rapide

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

L’API sera disponible sur :

- http://localhost:8080
- http://localhost:8080/swagger-ui/index.html

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Intégration continue

Le dépôt contient un workflow GitHub Actions dans [.github/workflows/ci-backend.yml](.github/workflows/ci-backend.yml) qui vérifie automatiquement :

- la compilation du backend
- l’exécution des tests
- le packaging de l’application

## Contribution

Les contributions sont les bienvenues. Consultez [CONTRIBUTING.md](CONTRIBUTING.md) pour les instructions.

## Captures d’écran

Les captures d’écran du projet sont disponibles dans [screenshots](screenshots/).

## Licence

Ce projet est distribué sous licence MIT.
