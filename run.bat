@echo off
echo Starting AI Chatbot...

start cmd /k "ollama run tinyllama"
start cmd /k "cd ai-support && .\mvnw spring-boot:run"

ping 127.0.0.1 -n 16 > nul
start http://localhost:8080