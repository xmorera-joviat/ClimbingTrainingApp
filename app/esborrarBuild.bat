@echo off
PowerShell -Command "if (Test-Path .\build) { Remove-Item -Path .\build -Recurse -Force }"
