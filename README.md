# Port Tracking Backend 🚢

A Spring Boot REST API for managing port operations including container tracking, customs inspection, and document management.

---

## Recent Changes (Changelog)

- ✅ **Fixed**: `Reports` export (Excel/PDF) frontend bug — unrelated to backend, but downstream of `/api/fiches` response shape.
- ✅ **Fixed**: Fiche creation now strictly JSON (`POST /api/fiches`) — file uploads are handled separately via the `document` endpoints, never bundled into fiche creation.
- ✅ **Added**: Phone/email format validation on the fiche creation flow (`+212XXXXXXXXX` or valid email) — the redundant phone field on signup has been removed; `User.telephone` is now optional and populated later from the fiche.
- ✅ **Added**: Automatic inspection assignment — when an Opérateur places a conteneur (`assignEmplacement`), the backend now automatically creates one `Inspection` per required organisme (excluding ADII) and assigns it to the **least busy** matching inspector. Previously this only sent a notification with no actual task behind it.
- ✅ **Added**: Admin alert system — `POST /api/notifications/admin-alert` lets an Admin broadcast a message to every user of a given role (ADII / OPERATEUR / INSPECTEUR), optionally linked to a specific fiche.
- ✅ **Confirmed working as designed**: ADII is always locked as "Obligatoire" in the organismes selection (frontend), and document downloads (`/api/documents/{id}/download`) work correctly end-to-end.
- ⚠️ **Known gap vs. CDC**: the CDC specifies a Flutter mobile app for inspectors (QR scan + on-site photo capture). Inspection is currently handled entirely through the web interface — the mobile app has not been built yet.

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
src/main/java/com/port/tracking/

├── config/

│   └── SecurityConfig.java          # JWT + Spring Security + DaoAuthenticationProvider

├── auth/

│   ├── AuthController.java          # POST /api/auth/login, /register

│   ├── AuthService.java

│   ├── JwtTokenProvider.java        # Generate & validate JWT tokens

│   ├── JwtAuthFilter.java           # Filter every request

│   └── dto/

│       ├── LoginRequest.java

│       └── AuthResponse.java

├── user/

│   ├── User.java                    # Entity (telephone is now optional/nullable)

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

│       ├── CreateFicheRequest.java  # Plain JSON — no multipart, no files

│       ├── UpdateFicheStatutRequest.java

│       └── ResoumissionRequest.java

├── conteneur/

│   ├── Conteneur.java               # Entity

│   ├── ConteneurStatut.java         # Enum: ARRIVE, STOCKE, EN_INSPECTION, CHARGEMENT, PARTI

│   ├── ConteneurRepository.java

│   ├── ConteneurService.java        # assignEmplacement() now triggers auto-inspection creation

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

│   ├── InspectionService.java       # autoCreateInspectionsForConteneur() — auto-assigns by organisme + load balancing

│   ├── InspectionController.java

│   └── dto/

│       ├── InspectionDTO.java

│       └── EnregistrerResultatRequest.java

├── document/

│   ├── Document.java

│   ├── DocumentRepository.java

│   ├── DocumentService.java

│   └── DocumentController.java      # Upload/download are separate from fiche creation

├── historique/

│   ├── Historique.java

│   ├── HistoriqueRepository.java

│   └── HistoriqueService.java

├── notification/

│   ├── Notification.java

│   ├── NotificationRepository.java

│   ├── NotificationService.java     # sendAdminAlert() — new, broadcasts to a role
│   ├── NotificationController.java  # POST /api/notifications/admin-alert — ADMIN only
│   └── AdminAlertRequest.java       # DTO: targetRole, message, optional ficheId

└── qrcode/

├── QrCodeService.java

└── QrCodeController.java

---

## Database Schema

```sql
users               → id, nom, email, password, role, organisme, created_at
fiches_suiveuses    → id, importateur_id, importateur_nom, importateur_adresse, importateur_contact, statut, organismes, created_at, updated_at
marchandises        → id, fiche_id, description, classification, code_sh, poids, volume
conteneurs          → id, fiche_id, statut, zone, rangee, position, quai, arrived_at
inspections         → id, conteneur_id, inspecteur_id, organisme, resultat, date, commentaire, photo_path, delay_alert_sent
documents           → id, fiche_id, type, file_path, uploaded_at
historique          → id, fiche_id, acteur_id, action, details, timestamp
notifications        → id, user_id, fiche_id, message, lu, created_at
```

> Note: `users.telephone` is nullable — it's no longer required at signup. It gets populated when the user (as Importateur) creates their first fiche, where the actual contact validation (`+212XXXXXXXXX` or email) happens on the frontend.

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Clone the repository

```bash
git clone https://github.com/Younes-Germat1/port-tracking-backend.git
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

### 4. Insert test users (BCrypt password = 123456)

```sql
USE port_tracking;

INSERT INTO users (nom, email, password, role, created_at) VALUES
                                                               ('Admin Port', 'admin@port.ma', '$2a$10$slYQmyNdgTY18LlZkbC7SOyfEjFnEAUdGlFVRFMl5wBrTRO6FQCK6', 'ADMIN', NOW()),
                                                               ('Importateur Test', 'importateur@port.ma', '$2a$10$slYQmyNdgTY18LlZkbC7SOyfEjFnEAUdGlFVRFMl5wBrTRO6FQCK6', 'IMPORTATEUR', NOW()),
                                                               ('Agent ADII', 'adii@port.ma', '$2a$10$slYQmyNdgTY18LlZkbC7SOyfEjFnEAUdGlFVRFMl5wBrTRO6FQCK6', 'ADII', NOW()),
                                                               ('Operateur Port', 'operateur@port.ma', '$2a$10$slYQmyNdgTY18LlZkbC7SOyfEjFnEAUdGlFVRFMl5wBrTRO6FQCK6', 'OPERATEUR', NOW()),
                                                               ('Inspecteur Test', 'inspecteur@port.ma', '$2a$10$slYQmyNdgTY18LlZkbC7SOyfEjFnEAUdGlFVRFMl5wBrTRO6FQCK6', 'INSPECTEUR', NOW());
```

> ⚠️ For inspectors, also set the `organisme` column (e.g. `ONSSA`, `AMSSNUR`, `LPEE`) — automatic inspection assignment matches inspectors to tasks by this field. An inspector with `organisme = NULL` will never receive auto-assigned inspections.

### 5. Run the application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

---

## API Documentation

Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user (body: `User` entity + `?role=` param) | Public |
| POST | `/api/auth/login` | Login and get JWT token | Public |

### Fiche Suiveuse
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/fiches` | Create a fiche — plain JSON body (importateur + marchandises + organismes) | IMPORTATEUR, ADMIN |
| GET | `/api/fiches` | List all fiches | Authenticated |
| GET | `/api/fiches/{id}` | Get fiche by ID | Authenticated |
| PUT | `/api/fiches/{id}/statut?acteurId={id}` | Update fiche status (approve/reject/place/dédouane/libère) | ADII, ADMIN, OPERATEUR, INSPECTEUR |
| PUT | `/api/fiches/{id}/resoumission` | Re-submit a rejected fiche with corrected info | IMPORTATEUR, ADMIN |
| GET | `/api/fiches/{id}/historique` | Get fiche history | Authenticated |

### Documents
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/documents/upload` | Upload a file (`ficheId`, `type`, `file` as multipart) | IMPORTATEUR, ADMIN |
| GET | `/api/documents/{id}/download` | Download a file | Authenticated |
| GET | `/api/documents/fiche/{ficheId}` | Get docs by fiche | Authenticated |

### Conteneurs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/conteneurs` | Create a container for a fiche | OPERATEUR, ADMIN |
| GET | `/api/conteneurs` | List all containers | Authenticated |
| GET | `/api/conteneurs/{id}` | Get container by ID | Authenticated |
| PUT | `/api/conteneurs/{id}/emplacement` | Assign location — **also auto-creates inspections** | OPERATEUR, ADMIN |
| GET | `/api/conteneurs/{id}/dwell-time` | Get dwell time (hours) | Authenticated |
| GET | `/api/conteneurs/fiche/{ficheId}` | Get containers by fiche | Authenticated |

### Inspections
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/inspections` | Manually create an inspection (rarely needed now — see auto-assignment below) | ADII, ADMIN |
| GET | `/api/inspections` | Get all inspections | ADMIN, ADII, INSPECTEUR |
| GET | `/api/inspections/mes-taches?inspecteurId={id}` | Get my tasks (pending first, sorted by dwell time) | INSPECTEUR, ADMIN, ADII |
| GET | `/api/inspections/{id}` | Get inspection by ID | INSPECTEUR, ADMIN, ADII |
| PUT | `/api/inspections/{id}/resultat` | Submit result (CONFORME/NON_CONFORME) | INSPECTEUR, ADMIN, ADII |
| POST | `/api/inspections/{id}/photo` | Upload inspection photo | INSPECTEUR, ADMIN |

> **Auto-assignment**: inspections are now created automatically when a conteneur is placed. For each organisme required by the fiche (excluding ADII), the backend picks the inspector registered for that organisme with the fewest pending inspections, and creates the task for them. Manual `POST /api/inspections` still works as a fallback but is rarely needed.

### Notifications
| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/notifications?userId={id}` | Get my notifications | Authenticated |
| GET | `/api/notifications/unread?userId={id}` | Get unread notifications | Authenticated |
| PUT | `/api/notifications/{id}/lu` | Mark one as read | Authenticated |
| PUT | `/api/notifications/lu-tout?userId={id}` | Mark all as read | Authenticated |
| POST | `/api/notifications/admin-alert` | Send a targeted alert to every user of a role, optionally linked to a fiche | **ADMIN only** |

### QR Code
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/qrcode/{conteneurId}` | Generate QR code image |

---

## Authentication

All protected endpoints require a Bearer token in the Authorization header:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

To get a token, call `POST /api/auth/login` with your credentials.

---

## User Roles & Permissions

| Role | Dashboard shows | Can do |
|---|---|---|
| `IMPORTATEUR` | Status overview, fiche timeline, conteneurs, dwell time | Create/re-submit fiches, upload documents, track status |
| `ADII` | Decisions to make, daily/weekly goal graph | Approve/reject fiches |
| `OPERATEUR` | Placement queue, dwell-time alerts, goal graph, port map | Create containers, assign locations (triggers auto-inspection), schedule manutention, release cargo |
| `INSPECTEUR` | Assigned inspections, goal graph | Record CONFORME/NON_CONFORME results, upload photos |
| `ADMIN` | Global stats, real-time workflow graph with bottleneck detection | Manage users, export reports, send targeted alerts to any role |

---

## Default Test Accounts

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@port.ma | 123456 |
| IMPORTATEUR | importateur@port.ma | 123456 |
| ADII | adii@port.ma | 123456 |
| OPERATEUR | operateur@port.ma | 123456 |
| INSPECTEUR | inspecteur@port.ma | 123456 |

---

## Connection with Flutter Mobile App

> ⚠️ **Not yet built.** The CDC specifies a Flutter mobile app for inspectors (QR scan on-site, photo capture). This is currently a gap — inspection is handled entirely via the web interface. Once built, configure the IP in `lib/core/constants.dart`:

```dart
static const String baseUrl = 'http://YOUR_PC_IP:8080';
```

> Make sure your phone and PC are on the same WiFi network!

---

## Known Issues & Solutions

| Problem | Solution |
|---|---|
| 403 on login | Check BCrypt password hash in DB |
| Connection refused | Verify Spring Boot is running on port 8080 |
| Firewall blocking | Run: `netsh advfirewall firewall add rule name="Spring Boot 8080" dir=in action=allow protocol=TCP localport=8080 profile=any` |
| Inspector never gets auto-assigned tasks | Check that `users.organisme` is set for that inspector and matches an organisme on the fiche |

---

## Built With ❤️ for PFE

> Port Container Tracking System — Backend API  
> Built with Spring Boot 3.5 + MySQL + JWT Authentication