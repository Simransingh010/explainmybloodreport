# Blood Report Analyzer

A web application that parses blood test results and outputs risk analysis and lifestyle recommendations. Uses Google's Gemini AI to interpret medical data.

## What it does

Upload a blood report (PDF or image), get back:
- **Metrics Visualization**: Interactive charts showing your levels vs standard ranges.
- **Validation**: Code-based sanity checks against medical reference ranges. If the AI hallucinates "Normal" for a critical value, we flag it.
- **Risk Analysis**: AI-generated risk assessment.
- **Lifestyle Advice**: Simple recommendations.
- **Mobile Friendly**: Fully responsive design for premium feel on all devices.

That's it. It now supports concurrent uploads and rate limiting, so you can't crash it easily.

## Architecture

Backend: Spring Boot 3.5.0 with Java 17
Frontend: React 18 with Vite
Integration: Google Gemini AI API for text analysis

## Requirements

- Java 17 or later
- Node.js 18 or later
- Maven (included via wrapper)
- Google Gemini API key

## Building and Running

### Getting the API Key

Go to https://aistudio.google.com/app/apikey and get an API key.
Set it in `set-env.sh` or `backend/src/main/resources/application.properties` (but don't commit it).
Using `set-env.sh` is preferred so you don't accidentally commit your key.

```bash
source ./set-env.sh
```

### Quick Start

From project root:

```bash
./start.sh
```

This starts both backend and frontend. Logs go to `backend.log` and `frontend.log`. Kill with Ctrl+C.

Backend runs on port 8080, frontend on 5173.

### Manual Build

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
Rate limited to 10 requests/minute per IP.

Content-Type: multipart/form-data
Parameter: file (max 10MB, PDF/JPG/PNG)

Response:
```json
{
  "fileName": "string",
  "fileSize": number,
  "metrics": [
    {
      "name": "Glucose",
      "value": 95.0,
      "unit": "mg/dL",
      "minNormal": 70.0,
      "maxNormal": 100.0,
      "status": "normal"
    }
  ],
  "validationWarnings": [
    "⚠️ HIGH: Glucose is 120.0 mg/dL (Normal: 70.0-100.0)"
  ],
  "riskFactors": ["array", "of", "strings"],
  "lifestyleAdvice": ["array", "of", "strings"],
  "message": "string"
}
```

## Configuration

Backend `application.properties`:
- Rate limiting: 10 req/min
- Max file size: 10MB
- CORS enabled for localhost

## Known Limitations

- PDF text extraction relies on the file being text-based or clear enough for AI to parse.
- Image analysis works but is limited by the AI's ability to "read" the image text without OCR preprocessing.
- No database. Data is ephemeral.
- No authentication.

## TODO

If you actually want to turn this into something useful:

- Add OCR for better image support (Tesseract)
- Implement a real database for report history
- Add user authentication

## License

Educational use only. Don't use this for actual medical decisions, obviously.
