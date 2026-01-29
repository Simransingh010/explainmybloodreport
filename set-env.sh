#!/bin/bash

# Export environment variable for local development
# Replace with your actual Gemini API key
export GEMINI_API_KEY="your_actual_gemini_api_key_here"

echo "✅ Environment variable set!"
echo "Run this before starting your backend:"
echo "  source ./set-env.sh"
echo "  cd backend && ./mvnw spring-boot:run"
