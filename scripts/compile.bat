@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo  Career Recommendation System - Compilation Script (Windows)
echo ====================================================================
echo.

:: Ensure project root is current directory
cd /d "%~dp0\.."

:: Verify javac is available
where javac >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] 'javac' compiler was not found in your PATH.
    echo Please ensure JDK 11, 17, or 21 is installed and added to PATH.
    pause
    exit /b 1
)

:: Ensure target output directory exists
if not exist "src\main\webapp\WEB-INF\classes" (
    mkdir "src\main\webapp\WEB-INF\classes"
)

:: Build list of all Java source files
set "SOURCES_FILE=%TEMP%\career_sources.txt"
if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"

for /R "src\main\java" %%F in (*.java) do (
    echo %%F >> "%SOURCES_FILE%"
)

echo [INFO] Compiling Java classes without Maven...
javac -cp "lib/*" -d "src\main\webapp\WEB-INF\classes" @"%SOURCES_FILE%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please inspect error messages above.
    del "%SOURCES_FILE%" >nul 2>nul
    pause
    exit /b 1
)

:: Copy active db.properties to WEB-INF/classes
if exist "config\db.properties" (
    copy /Y "config\db.properties" "src\main\webapp\WEB-INF\classes\db.properties" >nul
)

del "%SOURCES_FILE%" >nul 2>nul

echo.
echo [SUCCESS] Compilation completed successfully!
echo Classes compiled into: src\main\webapp\WEB-INF\classes
echo.
pause
