@echo off
setlocal
set "PORT=8080"

echo =========================================
echo Buscando proceso en el puerto %PORT%...
echo =========================================

:: Busca el PID (Process ID) que está escuchando en el puerto
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%PORT% ^| findstr LISTENING') do set "PID=%%a"

if "%PID%"=="" (
    echo El puerto %PORT% esta libre. No hay nada que cerrar.
    pause
    exit
)

echo Se encontro un proceso con PID: %PID% usando el puerto %PORT%.
echo.
set /p "CHOICE=Deseas liberar el puerto y cerrar el proceso? (S/N): "

if /i "%CHOICE%"=="S" (
    taskkill /F /PID %PID%
    echo.
    echo Puerto %PORT% liberado exitosamente.
) else (
    echo Operacion cancelada por el usuario.
)

echo.
pause

