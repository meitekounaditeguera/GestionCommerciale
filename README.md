# Gestion Commerciale

**Gestion Commerciale** est un système de gestion commerciale, de stock et de suivi d'activité développé de manière indépendante, avec une architecture pensée comme en entreprise : séparation claire des responsabilités entre l'API et l'interface, sécurité par rôles, traçabilité complète des actions et documentation de l'API.

L'application couvre l'ensemble du cycle d'une activité commerciale : clients, catalogue produits, stock, ventes, achats fournisseurs et pilotage via un tableau de bord.

## Aperçu

- Tableau de bord avec indicateurs financiers, graphiques de ventes et alertes de stock
- Catalogue produits avec scan de code-barres / QR code par webcam
- Prise de commande, facturation PDF et export Excel
- Achats fournisseurs avec flux de validation et réception de stock
- Gestion des rôles (Admin, Gestionnaire, Caissier) et journal d'audit complet
- API REST documentée (Swagger / OpenAPI)

## Fonctionnalités clés

### Tableau de bord

- Indicateurs clés : clients, produits, commandes, chiffre d'affaires (journalier, hebdomadaire, mensuel, annuel)
- Graphiques : évolution du chiffre d'affaires sur 12 mois, ventes par catégorie, top des produits vendus
- Meilleur client, produit le plus vendu, catégorie la plus populaire, nouveaux clients du mois
- Alertes de stock à deux niveaux (rupture imminente / stock bas) avec estimation du délai avant épuisement

### Produits & stock

- Catalogue produits avec catégories, prix, quantités et code-barres / QR code
- **Scanner intégré via la webcam** (`html5-qrcode`) pour rechercher un produit ou l'ajouter à une commande sans saisie manuelle
- Réapprovisionnement du stock et historique complet des mouvements (entrées/sorties, motif, commande ou réception associée)
- Export du catalogue au format Excel

### Achats & commandes

- Prise de commande client avec sélection ou scan des produits, calcul automatique du montant
- Génération de factures PDF (mise en page A4, générée côté client avec jsPDF)
- Commandes fournisseurs avec flux de validation (Brouillon → Validée → Livrée / Annulée) et réception qui met à jour le stock automatiquement
- Export des ventes au format Excel

### Sécurité & traçabilité

- Authentification par token JWT
- Trois rôles applicatifs aux permissions distinctes :

  | Rôle | Périmètre d'accès |
  |---|---|
  | **Admin** | Accès complet : tableau de bord, stock, achats, suppression de données, journal d'audit |
  | **Gestionnaire** | Tableau de bord, stock, fournisseurs, achats et commandes — sans suppression ni accès au journal d'audit |
  | **Caissier** | Prise de commande et facturation uniquement |

- **Journal d'audit** : chaque création, modification ou suppression (clients, produits, fournisseurs, commandes...) est tracée avec l'utilisateur, la date et le détail de l'action

## Captures d'écran

| | |
|---|---|
| **Connexion** | ![Connexion](screenshots/Capture%20d'%C3%A9cran%202026-08-11%20080716.png) |
| **Tableau de bord** | ![Tableau de bord](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20165647.png) |
| **Graphiques & alertes de stock** | ![Graphiques et alertes de stock](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20170919.png) |
| **Catalogue produits (scan & export)** | ![Catalogue produits](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20172024.png) |
| **Prise de commande (scanner intégré)** | ![Prise de commande](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20172555.png) |
| **Commandes & facturation PDF** | ![Commandes et facturation](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20175658.png) |
| **Achats fournisseurs (flux de validation)** | ![Achats fournisseurs](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20180846.png) |
| **Journal d'audit** | ![Journal d'audit](screenshots/Capture%20d'%C3%A9cran%202026-08-08%20202710.png) |

D'autres captures sont disponibles dans le dossier [screenshots](screenshots/).

## Stack technique

**Backend**
- Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA
- PostgreSQL
- Maven
- Documentation API : springdoc-openapi (Swagger UI)

**Frontend**
- Angular (composants standalone), TypeScript
- Bootstrap 5 + Bootstrap Icons
- Chart.js / ng2-charts (graphiques du tableau de bord)
- html5-qrcode (scan code-barres / QR code par webcam)
- jsPDF + jspdf-autotable (génération des factures PDF)
- ExcelJS (export des listes en Excel)

## Structure du dépôt

```text
GestionCommerciale/
├── backend/          # API Spring Boot
├── frontend/         # Interface utilisateur Angular
├── database/         # Scripts et ressources liés à la base de données
├── screenshots/      # Captures d'écran de l'application
├── docs/             # Documentation complémentaire
├── .github/          # Workflows GitHub Actions
├── README.md         # Documentation principale
├── LICENSE           # Licence du projet
└── .gitignore        # Fichiers à ignorer
```

## Prérequis

- Java 21
- Maven
- Node.js et npm
- PostgreSQL

## Démarrage rapide

### Backend

Les identifiants PostgreSQL ne sont jamais commités : ils sont lus depuis les variables d'environnement `DB_USERNAME` (par défaut `postgres`) et `DB_PASSWORD`.

```bash
export DB_PASSWORD="votre-mot-de-passe-postgres"

cd backend
./mvnw spring-boot:run
```

L'API sera disponible sur :

- http://localhost:8080
- http://localhost:8080/swagger-ui/index.html (documentation interactive)

### Frontend

```bash
cd frontend
npm install
npm start        # ou : ng serve
```

L'application sera disponible sur http://localhost:4200.

### Comptes de démonstration

Au premier démarrage, le backend crée automatiquement un compte par rôle :

| Identifiant | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | Administrateur |
| `gestionnaire` | `gestionnaire123` | Gestionnaire |
| `caissier` | `caissier123` | Caissier |

## Intégration continue

Le dépôt contient un workflow GitHub Actions dans [.github/workflows/ci-backend.yml](.github/workflows/ci-backend.yml) qui vérifie automatiquement :

- la compilation du backend
- l'exécution des tests
- le packaging de l'application

## Contribution

Les contributions sont les bienvenues. Consultez [CONTRIBUTING.md](CONTRIBUTING.md) pour les instructions.

## Licence

Ce projet est distribué sous licence MIT — voir [LICENSE](LICENSE).
