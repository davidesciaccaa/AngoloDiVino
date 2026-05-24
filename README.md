# L'Angolo diVino

Applicazione full-stack per un cocktail bar, composta da:

- `frontend`: React + Vite
- `backend`: Spring Boot 3 + Java 21

La homepage React consuma le API Spring Boot per stato servizio e sezioni del menu. In sviluppo Vite usa un proxy verso il backend; il backend espone comunque CORS configurabile tramite variabili d'ambiente.

## Prerequisiti

- Node.js 20.19+ oppure 22.12+
- Java 21
- Maven 3.9+
- Docker e Docker Compose, opzionali

## Avvio in sviluppo

Terminale 1:

```bash
cd backend
mvn spring-boot:run
```

Terminale 2:

```bash
cd frontend
npm install
npm run dev
```

URL principali:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Health check: `http://localhost:8080/actuator/health`
- API stato: `http://localhost:8080/api/status`
- API menu: `http://localhost:8080/api/menu/sections`

## Configurazione CORS

In sviluppo il backend accetta di default:

- `http://localhost:5173`
- `http://127.0.0.1:5173`

Per cambiare gli origin consentiti:

```bash
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://example.com mvn spring-boot:run
```

Nel frontend puoi sovrascrivere la base URL delle API copiando `frontend/.env.example` in `frontend/.env`:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

Senza `.env`, il frontend usa `/api`; in sviluppo Vite fa proxy verso `http://localhost:8080`.

## Avvio con Docker

```bash
docker compose up --build
```

Con Docker:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

## Comandi utili

Frontend:

```bash
cd frontend
npm run lint
npm run build
npm run preview
```

Backend:

```bash
cd backend
mvn test
mvn package
```

## Struttura

```text
.
├── backend
│   ├── src/main/java/com/angolodivino
│   │   ├── config
│   │   ├── menu
│   │   └── status
│   └── src/main/resources
├── frontend
│   ├── src/api
│   ├── src/assets
│   └── src/components
└── compose.yaml
```
