@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ========================================
echo   AUTOMATIZADOR DE GIT - SUBIENDO CAMBIOS
echo ========================================
echo.

cd /d "C:\Proyectos\serviciomedico-main"

if not exist ".git" (
    echo [ERROR] Esta carpeta no es un repositorio Git.
    echo Ruta actual: %CD%
    echo.
    pause
    exit /b 1
)

git remote get-url origin >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No hay remote 'origin' configurado.
    echo.
    pause
    exit /b 1
)

echo Archivos con cambios:
echo ----------------------------------------
git status --short
echo ----------------------------------------
echo.

REM Verificar si hay cambios con git status --porcelain
git status --porcelain | findstr "." >nul
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] No hay cambios para subir. El repositorio esta al dia.
    echo.
    pause
    exit /b 0
)

set "mensaje="
set /p "mensaje=Escribe el mensaje para el commit: "

if "!mensaje!"=="" (
    set "mensaje=Actualizacion automatica - %DATE% %TIME%"
    echo.
    echo [INFO] No escribiste mensaje. Se usara: !mensaje!
)

echo.
echo Agregando cambios...
git add .
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al agregar cambios (git add).
    echo.
    pause
    exit /b 1
)

echo Haciendo commit...
git commit -m "!mensaje!"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al hacer commit.
    echo.
    pause
    exit /b 1
)

echo.
echo Subiendo los cambios a GitHub...
git push
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Fallo al subir los cambios (git push).
    echo Verifica tu conexion y credenciales de GitHub.
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   PROCESO FINALIZADO CON EXITO
echo ========================================
echo.
pause
exit /b 0