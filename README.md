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
- Pannello admin: `http://localhost:5173/admin` (vedi [Pannello admin](#pannello-admin-gestione-prezzi))

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

## Pannello admin (gestione prezzi)

Il proprietario modifica i prezzi del menù da `http://localhost:5173/admin`. È una pagina separata, non raggiungibile dai link pubblici del sito.

Il pannello è **disabilitato finché non imposti `ADMIN_PASSWORD`**: senza quella variabile ogni endpoint `/api/admin/**` risponde `503`.

```bash
cd backend
ADMIN_PASSWORD='una-password-lunga' mvn spring-boot:run
```

Variabili disponibili:

| Variabile | Default | Significato |
| --- | --- | --- |
| `ADMIN_PASSWORD` | *(vuota)* | Password del pannello. Vuota = admin disattivato. |
| `ADMIN_SESSION_TTL` | `8h` | Durata del token di sessione. |
| `MENU_DATA_DIRECTORY` | `data` | Directory runtime del menù e dei backup. |
| `MENU_OVERRIDES_FILE` | vuoto | Vecchio file di override, migrato automaticamente al primo avvio. |
| `MENU_BACKUP_CRON` | `0 15 2 * * *` | Pianificazione Spring dei backup nel fuso `Europe/Rome`. |
| `MENU_TRANSLATION_ENABLED` | `false` | Abilita la traduzione automatica del menù tramite DeepL. |
| `DEEPL_AUTH_KEY` | *(vuota)* | Chiave API DeepL, letta esclusivamente dal backend. |

Come funziona:

- `src/main/resources/menu.default.json` è il catalogo iniziale versionato e non viene mai modificato a runtime.
- Il prezzo esposto dalle API è `null` quando assente, oppure un oggetto `price` con un array numerico `options`. I vini con due importi usano nell'ordine le etichette `glass` e `bottle`, coerenti con l'intestazione “Al calice / Bottiglia”; il frontend mantiene la resa `5 € / 22 €`.
- Il mapper privato della persistenza legge anche i dati legacy come `"2,50 €"`, `"25 €"`, il vecchio mojibake `"25 â‚¬"`, `"5 € / 22 €"`, `"6 € - 24 €"` e `"-"`. I segnaposto legacy `0`, `"0"`, `"0 €"` e `"0 â‚¬"` diventano prezzo assente (`null`). Il parser delle richieste admin è separato e rifiuta zero, stringhe con valuta e formati ambigui. Le risposte e le nuove scritture contengono soltanto importi numerici positivi, senza simboli di valuta.
- Al primo avvio il backend crea `data/menu.json` e le directory `data/backups/daily` e `data/backups/monthly`. Se trova il precedente `data/menu-overrides.json`, ne migra automaticamente i prezzi nel nuovo menù completo.
- Se `data/menu.json` esiste già viene validato e mantenuto senza copiarvi sopra il default. Tutte le operazioni `/admin` aggiornano questo file con una scrittura temporanea e una sostituzione atomica quando supportata dal filesystem.
- Un `menu.json` legacy esistente viene letto senza riscrittura all'avvio. La prima modifica autenticata lo serializza nel modello numerico solo dopo avere validato tutti gli importi; prezzi multipli e prezzi assenti vengono preservati e il backup pianificato continua a proteggere lo snapshot precedente.
- All'avvio e ogni giorno alle 02:15 (`Europe/Rome`) Spring crea il backup giornaliero e mensile eventualmente mancanti. Vengono conservati 30 giorni e 12 mesi.
- `data/menu.json`, il vecchio override e l'intera directory dei backup sono esclusi da Git; `mvn clean` elimina solo `target` e non li coinvolge.
- Il login (`POST /api/admin/login`) restituisce un token opaco tenuto **solo in memoria**: si perde a ogni riavvio del backend. Il browser lo tiene in `sessionStorage`, quindi la sessione muore chiudendo il browser.
- Il fallback `fallbackMenuSections` in `App.jsx` resta invariato e serve solo se il backend è irraggiungibile.

Poiché la forma JSON del prezzo è cambiata, frontend e backend di questa versione devono essere distribuiti insieme. Prima del deploy è consigliata una copia del volume `menu-data`; non è richiesto alcuno script manuale di migrazione.

Con Docker, passa la password al compose (per esempio con un file `.env` accanto a `compose.yaml`):

```bash
ADMIN_PASSWORD='una-password-lunga' docker compose up --build
```

Il volume `menu-data` conserva `menu.json` e tutti i backup tra un riavvio e l'altro dei container.

## Traduzioni dinamiche del menù

Italiano, inglese e tedesco di ogni voce sono salvati nello stesso `data/menu.json`; i vecchi documenti senza `translations` restano leggibili. Il sito pubblico non contatta mai DeepL.

- `POST /api/admin/menu/items` e `PUT /api/admin/menu/items/{id}` accettano `autoTranslate`. Con `true` il backend traduce in batch verso EN e DE prima dell'unica scrittura atomica; con `false` accetta `translations.en` e `translations.de` manuali e conserva le lingue non inviate.
- `POST /api/admin/menu/translations/backfill` genera soltanto campi mancanti, senza sovrascrivere traduzioni esistenti, e pubblica il risultato con una sola scrittura finale.
- Se la traduzione è richiesta ma disabilitata, senza chiave o temporaneamente indisponibile, l'API risponde `503` e il file del menù non viene modificato.

Per abilitarla, copia `.env.example` in `.env`, imposta `MENU_TRANSLATION_ENABLED=true` e inserisci una chiave valida in `DEEPL_AUTH_KEY`. Con il valore predefinito `false`, il backend parte e le modifiche manuali funzionano senza chiave.

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
│   │   ├── admin
│   │   ├── config
│   │   ├── menu
│   │   └── status
│   └── src/main/resources
├── frontend
│   ├── src/admin
│   ├── src/api
│   ├── src/assets
│   └── src/components
└── compose.yaml
```
