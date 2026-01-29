#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🩺 Starting Blood Report Analyzer MVP...${NC}\n"

# Function to cleanup processes on exit
cleanup() {
    echo -e "\n${YELLOW}Shutting down servers...${NC}"
    kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
    exit
}

trap cleanup SIGINT SIGTERM

# Install frontend dependencies if needed
if [ ! -d "frontend/node_modules" ]; then
    echo -e "${YELLOW}📦 Installing frontend dependencies...${NC}"
    cd frontend
    npm install
    cd ..
    echo ""
fi

# Start backend and show output
echo -e "${GREEN}🚀 Starting Backend (Spring Boot)...${NC}"
echo -e "${YELLOW}⏳ This may take a minute on first run (downloading dependencies)...${NC}\n"

cd backend
./mvnw spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Monitor backend startup
echo -e "${BLUE}Waiting for backend to be ready...${NC}"
BACKEND_READY=false
BACKEND_TIMEOUT=120  # 2 minutes timeout
BACKEND_ELAPSED=0

while [ $BACKEND_ELAPSED -lt $BACKEND_TIMEOUT ]; do
    # Show some progress indicators from log
    if grep -q "Downloading" backend.log 2>/dev/null; then
        echo -e "${YELLOW}📥 Downloading Maven dependencies...${NC}"
        sleep 5
        BACKEND_ELAPSED=$((BACKEND_ELAPSED + 5))
        continue
    fi
    
    if grep -q "Building" backend.log 2>/dev/null; then
        echo -e "${YELLOW}🔨 Building project...${NC}"
    fi
    
    # Check if backend is ready
    if grep -q "Started BloodReportAnalyzerApplication" backend.log 2>/dev/null; then
        BACKEND_READY=true
        echo -e "${GREEN}✅ Backend is ready!${NC}\n"
        break
    fi
    
    # Check for errors
    if grep -qi "error" backend.log 2>/dev/null || ! kill -0 $BACKEND_PID 2>/dev/null; then
        echo -e "${RED}❌ Backend failed to start. Check backend.log for details${NC}"
        cat backend.log
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
    
    sleep 2
    BACKEND_ELAPSED=$((BACKEND_ELAPSED + 2))
done

if [ "$BACKEND_READY" = false ]; then
    echo -e "${RED}❌ Backend startup timeout. Check backend.log for details${NC}"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

# Start frontend
echo -e "${GREEN}🎨 Starting Frontend (React + Vite)...${NC}"
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for frontend to be ready
echo -e "${BLUE}Waiting for frontend to be ready...${NC}"
FRONTEND_READY=false
FRONTEND_TIMEOUT=30
FRONTEND_ELAPSED=0

while [ $FRONTEND_ELAPSED -lt $FRONTEND_TIMEOUT ]; do
    if grep -q "Local:.*http://localhost:5173" frontend.log 2>/dev/null; then
        FRONTEND_READY=true
        echo -e "${GREEN}✅ Frontend is ready!${NC}\n"
        break
    fi
    
    if ! kill -0 $FRONTEND_PID 2>/dev/null; then
        echo -e "${RED}❌ Frontend failed to start. Check frontend.log for details${NC}"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
    
    sleep 1
    FRONTEND_ELAPSED=$((FRONTEND_ELAPSED + 1))
done

if [ "$FRONTEND_READY" = false ]; then
    echo -e "${RED}❌ Frontend startup timeout. Check frontend.log for details${NC}"
    kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
    exit 1
fi

# Both servers are ready!
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🎉 Both servers are running!${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}Backend:${NC}  http://localhost:8080"
echo -e "${GREEN}Frontend:${NC} http://localhost:5173"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "\n${BLUE}💡 Open http://localhost:5173 in your browser to get started!${NC}"
echo -e "\n${YELLOW}View logs:${NC}"
echo -e "  Backend:  tail -f backend.log"
echo -e "  Frontend: tail -f frontend.log"
echo -e "\n${YELLOW}Press Ctrl+C to stop both servers${NC}\n"

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID
