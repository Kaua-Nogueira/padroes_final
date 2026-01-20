@echo off
echo ==========================================
echo   Medical Consultation System - Launcher
echo ==========================================
echo.
echo [1/2] Building project...
call "apache-maven-3.9.6\bin\mvn.cmd" clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b %ERRORLEVEL%
)
echo.
echo [2/2] Running Demonstration...
echo.
call "apache-maven-3.9.6\bin\mvn.cmd" -pl infrastructure exec:java
echo.
echo ==========================================
echo   Execution Finished
echo ==========================================
pause
