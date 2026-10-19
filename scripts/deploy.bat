@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo  Career Recommendation System - Deployment Script to Apache Tomcat
echo ====================================================================
echo.

:: Move to project root
cd /d "%~dp0\.."

:: 1. Check CATALINA_HOME
set "TOMCAT_DIR=%CATALINA_HOME%"

if "%TOMCAT_DIR%"=="" (
    if exist "C:\Users\%USERNAME%\Downloads\apache-tomcat-9.0.121-windows-x64\apache-tomcat-9.0.121" (
        set "TOMCAT_DIR=C:\Users\%USERNAME%\Downloads\apache-tomcat-9.0.121-windows-x64\apache-tomcat-9.0.121"
    ) else if exist "C:\Program Files\Apache Software Foundation\Tomcat 9.0" (
        set "TOMCAT_DIR=C:\Program Files\Apache Software Foundation\Tomcat 9.0"
    )
)

if "%TOMCAT_DIR%"=="" (
    set /p TOMCAT_DIR="Enter your Apache Tomcat installation path: "
)

if not exist "%TOMCAT_DIR%\webapps" (
    echo [ERROR] Could not locate webapps directory at: %TOMCAT_DIR%\webapps
    echo Please verify your Tomcat installation path.
    pause
    exit /b 1
)

echo [INFO] Target Tomcat directory: %TOMCAT_DIR%
echo [INFO] Compiling latest Java source code...
call scripts\compile.bat

set "DEPLOY_DIR=%TOMCAT_DIR%\webapps\CareerRecommendationSystem"

echo [INFO] Deploying web application to: %DEPLOY_DIR%
if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"

xcopy /E /I /Y "src\main\webapp\*" "%DEPLOY_DIR%\" >nul

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to copy web application files.
    pause
    exit /b 1
)

echo.
echo ====================================================================
echo [SUCCESS] Deployed successfully to Apache Tomcat!
echo.
echo Application URL:
echo http://localhost:8080/CareerRecommendationSystem/
echo.
echo Admin Login URL:
echo http://localhost:8080/CareerRecommendationSystem/admin/login
echo ====================================================================
echo.
pause
