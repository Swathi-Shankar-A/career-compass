@echo off
setlocal

echo ====================================================================
echo  Career Recommendation System - Start Apache Tomcat Server
echo ====================================================================
echo.

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "JRE_HOME=C:\Program Files\Java\jdk-21.0.12"

set "TOMCAT_DIR=%CATALINA_HOME%"
if "%TOMCAT_DIR%"=="" (
    if exist "C:\Users\%USERNAME%\Downloads\apache-tomcat-9.0.121-windows-x64\apache-tomcat-9.0.121" (
        set "TOMCAT_DIR=C:\Users\%USERNAME%\Downloads\apache-tomcat-9.0.121-windows-x64\apache-tomcat-9.0.121"
    )
)

if "%TOMCAT_DIR%"=="" (
    set /p TOMCAT_DIR="Enter your Apache Tomcat installation directory: "
)

if not exist "%TOMCAT_DIR%\bin\startup.bat" (
    echo [ERROR] Could not find startup.bat at: %TOMCAT_DIR%\bin
    pause
    exit /b 1
)

echo [INFO] Starting Apache Tomcat from: %TOMCAT_DIR%
echo [INFO] JAVA_HOME set to: %JAVA_HOME%
echo.

call "%TOMCAT_DIR%\bin\startup.bat"

echo.
echo ====================================================================
echo Tomcat is starting in a separate window.
echo Once started, open your browser at:
echo http://localhost:8085/CareerRecommendationSystem/
echo (or http://localhost:8080/CareerRecommendationSystem/ depending on your server.xml port)
echo ====================================================================
echo.
pause
