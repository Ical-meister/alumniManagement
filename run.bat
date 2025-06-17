@echo off
cd /d "%~dp0"

REM === Set up classpath for MySQL JDBC driver and source folder ===
set CLASSPATH=lib\mysql-connector-j-9.3.0.jar;lib\LGoodDatePicker-11.2.1.jar;src


REM === Clean old class files ===
echo Cleaning up old class files...
del /q src\*.class 2>nul

REM === Compile all Java source files ===
echo Compiling Java files...
javac -cp "%CLASSPATH%" src\*.java
if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed. Please check your Java source files.
    pause
    exit /b
)

REM === Run the main class ===
echo Starting the application...
java -cp "%CLASSPATH%" AlumniLoginApp

pause
