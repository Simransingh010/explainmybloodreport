# Blood Report Analyzer

A web application that parses blood test results and outputs risk analysis and lifestyle recommendations. Uses Google's Gemini AI to interpret medical data.

## What it does

Upload a blood report (PDF or image), get back:
- Risk factors identified from test values
- Basic lifestyle recommendations in plain language

That's it. No database, no user accounts, just file upload and analysis.

## Architecture

Backend: Spring Boot 3.5.0 with Java 17
Frontend: React 18 with Vite
Integration: Google Gemini AI API for text analysis

```
explainmybloodreport/
├── backend/
│   ├── src/main/java/com/bloodreport/analyzer/
│   │   └── controller/BloodReportController.java
│   ├── src/main/resources/application.properties
│   └── pom.xml
└── frontend/
    ├── src/
    │   ├── pages/LandingPage.jsx
    │   ├── pages/AnalyzerPage.jsx
    │   └── App.jsx
    └── package.json
```

## Requirements

- Java 17 or later
- Node.js 18 or later
- Maven (included via wrapper)
- Google Gemini API key

## Building and Running

### Getting the API Key

Go to https://aistudio.google.com/app/apikey and get an API key. Edit `backend/src/main/resources/application.properties`:

```properties
gemini.api.key=YOUR_ACTUAL_KEY_HERE
```

Don't commit your API key to version control. You've been warned.

### Quick Start

From project root:

```bash
./start.sh
```

This starts both backend and frontend. Logs go to `backend.log` and `frontend.log`. Kill with Ctrl+C.

Backend runs on port 8080, frontend on 5173.

### Manual Build

If you want to run them separately for some reason:

Backend:
```bash
cd backend
./mvnw spring-boot:run
```

Frontend:
```bash
cd frontend
npm install
npm run dev
```

## API

### GET /api/blood-report/health

Health check. Returns OK if the backend is alive.

### POST /api/blood-report/upload

Upload a blood report file.

Content-Type: multipart/form-data
Parameter: file (max 10MB, PDF/JPG/PNG)

Response:
```json
{
  "fileName": "string",
  "fileSize": number,
  "riskFactors": ["array", "of", "strings"],
  "lifestyleAdvice": ["array", "of", "strings"],
  "message": "string"
}
```

## Configuration

Backend in `application.properties`:
- server.port=8080
- spring.servlet.multipart.max-file-size=10MB
- CORS allows localhost:5173
- gemini.api.key must be set

Frontend connects to localhost:8080 by default.

## Known Limitations

- PDF text extraction only works on text-based PDFs, not scanned images
- Image analysis relies on Gemini's OCR capabilities
- No data persistence whatsoever
- No authentication or authorization
- Single file analysis only, no batching
- Error handling is basic

## TODO

If you actually want to turn this into something useful:

- Add OCR for scanned documents
- Implement a real database for report history
- Add user authentication
- Build proper data visualization for metrics
- Validate AI responses against known medical ranges
- Add unit tests (yes, there are none)
- Handle concurrent uploads properly
- Implement rate limiting on the API

## License

Educational use only. Don't use this for actual medical decisions, obviously.
