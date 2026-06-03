# Port Tracking Backend 🚢

A Spring Boot REST API for managing port operations including container tracking, customs inspection, and document management.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.14 |
| Spring Security + JWT | JJWT 0.11.5 |
| Spring Data JPA + Hibernate | 6.6.x |
| MySQL | 8.0 |
| Swagger / OpenAPI | SpringDoc 2.3.0 |
| ZXing (QR Code) | 3.5.2 |
| Lombok | Latest |
| MapStruct | 1.5.5 |

---

## Project Structure

```
src/main/java/com/port/tracking/
├── config/
│   └── SecurityConfig.java          # JWT + Spring Security config
├── auth/
│   ├── AuthController.java          # POST /api/auth/login, /register
│   ├── AuthService.java
│   ├── JwtTokenProvider.java        # Generate & validate JWT tokens
│   ├── JwtAuthFilter.java           # Filter every request
│   └── dto/
│       ├── LoginRequest.java
│       └── AuthResponse.java
├── user/
│   ├── User.java                    # Entity
│   ├── UserRole.java                # Enum: IMPORTATEUR, ADII, OPERATEUR, INSPECTEUR, ADMIN
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── UserController.java
│   ├── UserDetailsServiceImpl.java
│   └── dto/
│       ├── UserDTO.java
│       └── CreateUserRequest.java
├── fiche/                           # "Fiche Suiveuse" — core module
│   ├── FicheSuiveuse.java           # Entity
│   ├── FicheStatut.java             # Enum: EN_ATTENTE, APPROUVEE, REJETEE, PLACEE, DEDOUANEE, LIBEREE
│   ├── FicheRepository.java
│   ├── FicheService.java
│   ├── FicheController.java
│   └── dto/
│       ├── FicheDTO.java
│       ├── CreateFicheRequest.java
│       └── UpdateFicheStatutRequest.java
├── conteneur/
│   ├── Conteneur.java               # Entity
│   ├── ConteneurStatut.java         # Enum: ARRIVE, STOCKE, EN_INSPECTION, CHARGEMENT, PARTI
│   ├── ConteneurRepository.java
│   ├── ConteneurService.java
│   ├── ConteneurController.java
│   └── dto/
│       ├── ConteneurDTO.java
│       └── AssignEmplacementRequest.java
├── marchandise/
│   ├── Marchandise.java
│   ├── ClassificationMarchandise.java  # Enum: DANGEREUSE, PERISSABLE, STANDARD, FRAGILE
│   ├── MarchandiseRepository.java
│   ├── MarchandiseService.java
│   ├── MarchandiseController.java
│   └── dto/
│       ├── MarchandiseDTO.java
│       └── CreateMarchandiseRequest.java
├── inspection/
│   ├── Inspection.java
│   ├── InspectionResultat.java      # Enum: CONFORME, NON_CONFORME
│   ├── InspectionRepository.java
│   ├── InspectionService.java
│   ├── InspectionController.java
│   └── dto/
│       ├── InspectionDTO.java
│       └── EnregistrerResultatRequest.java
├── document/
│   ├── Document.java
│   ├── DocumentRepository.java
│   ├── DocumentService.java
│   └── DocumentController.java
├── historique/
│   ├── Historique.java
│   ├── HistoriqueRepository.java
│   └── HistoriqueService.java
├── notification/
│   ├── Notification.java
│   ├── NotificationRepository.java
│   ├── NotificationService.java
│   └── NotificationController.java
└── qrcode/
    ├── QrCodeService.java
    └── QrCodeController.java
```

---

## Database Schema

```
users               → id, nom, email, password, role, created_at
fiches_suiveuses    → id, importateur_id, statut, created_at, updated_at
marchandises        → id, fiche_id, classification, poids, volume, code_sh
conteneurs          → id, fiche_id, statut, zone, rangee, position, quai, arrived_at
inspections         → id, conteneur_id, inspecteur_id, organisme, resultat, date, commentaire
documents           → id, fiche_id, type, file_path, uploaded_at
historique          → id, fiche_id, acteur_id, action, details, timestamp
notifications       → id, user_id, message, lu, created_at
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Clone the repository

```bash
git clone https://github.com/your-username/port-tracking-backend.git
cd port-tracking-backend
```

### 2. Create the MySQL database

```sql
CREATE DATABASE port_tracking;
```

### 3. Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/port_tracking
spring.datasource.username=root
spring.datasource.password=yourpassword
jwt.secret=your_super_secret_key
jwt.expiration=86400000
file.upload-dir=./uploads
server.port=8080
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### Users (Admin only)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | Get all users |
| POST | `/api/admin/users` | Create a user |
| GET | `/api/admin/users/{id}` | Get user by ID |
| DELETE | `/api/admin/users/{id}` | Delete a user |

### Fiche Suiveuse
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/fiches` | Create a fiche | IMPORTATEUR, ADMIN |
| GET | `/api/fiches` | List all fiches | Authenticated |
| GET | `/api/fiches/{id}` | Get fiche by ID | Authenticated |
| PUT | `/api/fiches/{id}/statut` | Update fiche status | ADII, ADMIN |
| GET | `/api/fiches/{id}/historique` | Get fiche history | Authenticated |

### Conteneurs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/conteneurs` | Create a container | OPERATEUR, ADMIN |
| GET | `/api/conteneurs/{id}` | Get container by ID | Authenticated |
| PUT | `/api/conteneurs/{id}/emplacement` | Assign location | OPERATEUR, ADMIN |
| GET | `/api/conteneurs/{id}/dwell-time` | Get dwell time (hours) | Authenticated |
| GET | `/api/conteneurs/fiche/{ficheId}` | Get containers by fiche | Authenticated |

### Marchandises
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/marchandises` | Create merchandise |
| GET | `/api/marchandises/{id}` | Get by ID |
| GET | `/api/marchandises/fiche/{ficheId}` | Get by fiche |

### Inspections
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/inspections` | Create inspection | ADII, ADMIN |
| GET | `/api/inspections/mes-taches` | Get my tasks | INSPECTEUR |
| PUT | `/api/inspections/{id}/resultat` | Submit result | INSPECTEUR |

### Documents
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/documents/upload` | Upload a file |
| GET | `/api/documents/{id}/download` | Download a file |
| GET | `/api/documents/fiche/{ficheId}` | Get docs by fiche |

### Notifications
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications/me` | Get my notifications |
| GET | `/api/notifications/me/unread` | Get unread |
| PUT | `/api/notifications/{id}/lu` | Mark as read |

### QR Code
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/qrcode/{conteneurId}` | Generate QR code image |

---

## Authentication

All protected endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

To get a token, call `POST /api/auth/login` with your credentials.

---

## User Roles

| Role | Description |
|---|---|
| `ADMIN` | Full access to all endpoints |
| `IMPORTATEUR` | Create fiches and upload documents |
| `ADII` | Approve/reject fiches, create inspections |
| `OPERATEUR` | Assign container locations |
| `INSPECTEUR` | View assigned tasks and submit inspection results |

---

## Built With ❤️ for PFE

> Port Container Tracking System — Backend API  
> Built with Spring Boot 3.5 + MySQL + JWT Authentication
