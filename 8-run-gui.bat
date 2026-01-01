@echo off
echo =====================================================
echo       Budget Management System - GUI Mode
echo =====================================================
echo.

cd /d "%~dp0"

echo Checking Java installation...
java -version 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java JDK 11 or higher.
    pause
    exit /b 1
)

echo.
echo Building project...
cd budget-app
call mvn compile -q -DskipTests
if errorlevel 1 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Starting GUI application...
echo.
call mvn exec:java -Dexec.mainClass="com.hyildizoglu.gui.BudgetGUI" -q

echo.
echo Application closed.
pause


