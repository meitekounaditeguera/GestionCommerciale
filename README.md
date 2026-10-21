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
| **Graphiques & alertes de stock** | ![Graphiques et alertes de stock](screenshots/Capture%20d'%C3%A9cran%202026-08-11%20173853.png) |
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

En local, aucune autre variable n'est nécessaire : sans profil précisé, le backend démarre avec le profil `dev` (`spring.profiles.default=dev`), qui fournit une clé JWT de développement ([application-dev.properties](backend/src/main/resources/application-dev.properties)) et crée les comptes de démonstration ci-dessous.

### Comptes de démonstration

En profil `dev` uniquement, au premier démarrage, le backend crée automatiquement un compte par rôle :

| Identifiant | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | Administrateur |
| `gestionnaire` | `gestionnaire123` | Gestionnaire |
| `caissier` | `caissier123` | Caissier |

## Configuration en production

En production, activez le profil `prod` : la clé JWT de développement et les comptes de démonstration ne sont alors **pas** chargés.

```bash
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET="$(openssl rand -base64 48)"   # obligatoire, au moins 32 caractères
```

Sans `JWT_SECRET`, ou avec une clé de moins de 32 caractères, l'application refuse de démarrer (`IllegalStateException : Définir la variable d'environnement JWT_SECRET`). Le code ne contient aucune clé de secours. Une clé connue de tous permettrait de forger des tokens valides. Changer la clé déconnecte tous les utilisateurs, car leurs tokens deviennent invalides.

| Variable | Obligatoire | Rôle |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | oui | `prod` (désactive le profil `dev`) |
| `JWT_SECRET` | oui | Clé de signature des tokens JWT, au moins 32 caractères |
| `DB_USERNAME` / `DB_PASSWORD` | oui | Identifiants PostgreSQL (`DB_USERNAME` vaut `postgres` par défaut) |
| `MAIL_HOST` / `MAIL_PORT` | pour l'alerte stock | Serveur SMTP (par défaut `localhost` / `587`) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | pour l'alerte stock | Identifiants SMTP |
| `ALERTE_STOCK_DESTINATAIRE` | pour l'alerte stock | Adresse qui reçoit l'alerte quotidienne |
| `ALERTE_STOCK_CRON` | non | Planification de l'alerte (par défaut `0 0 8 * * *`, tous les jours à 8h) |
| `FACTURE_STOCKAGE_DOSSIER` | non | Dossier d'archivage des factures PDF (par défaut `./factures`) |

**Comptes utilisateurs :** aucun compte n'est créé automatiquement en production. Les comptes `admin`, `gestionnaire` et `caissier` et leurs mots de passe publics n'existent qu'en profil `dev`. Créez les comptes manuellement dans la table `utilisateurs`. Le mot de passe doit y être haché avec BCrypt, et le rôle doit valoir `ADMIN`, `GESTIONNAIRE` ou `CAISSIER` :

```sql
INSERT INTO utilisateurs (username, password, role)
VALUES ('prenom.nom', '<hash BCrypt du mot de passe>', 'ADMIN');
```

## Intégration continue

Le dépôt contient deux workflows GitHub Actions, exécutés à chaque push et pull request vers `main` :

[.github/workflows/ci-backend.yml](.github/workflows/ci-backend.yml) vérifie automatiquement :

- la compilation du backend
- l'exécution des tests
- le packaging de l'application

[.github/workflows/ci-frontend.yml](.github/workflows/ci-frontend.yml) vérifie automatiquement, sous Node 22 :

- l'installation des dépendances (`npm ci`)
- l'exécution des tests (Vitest)
- le build de production Angular

## Outils et assistance

ChatGPT et Gemini ont été consultés pendant le développement.
Claude Code a écrit, à partir d'instructions de l'auteur, l'alerte email, l'archivage PDF, l'estimation de rupture, les droits par rôle, les tests et la documentation technique.
L'auteur a défini les besoins, relu et validé chaque modification.

## Contribution

Les contributions sont les bienvenues. Consultez [CONTRIBUTING.md](CONTRIBUTING.md) pour les instructions.

## Licence

Ce projet est distribué sous licence MIT — voir [LICENSE](LICENSE).
