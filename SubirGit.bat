@echo off
cd /d "C:\Proyectos\serviciomedico-main"

echo.
echo ========================================
echo   AUTOMATIZADOR DE GIT
echo ========================================
echo.

git status --short
echo.

set /p mensaje=Escribe el mensaje para el commit: 
if "%mensaje%"=="" set mensaje=Actualizacion automatica

git add .
git commit -m "%mensaje%"
if errorlevel 1 (
    echo.
    echo [ERROR] Fallo el commit. No hay cambios o hubo un problema.
    pause
    exit /b 1
)

git push
if errorlevel 1 (
    echo.
    echo [ERROR] Fallo el push. Verifica credenciales y conexion.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   PROCESO FINALIZADO CON EXITO
echo ========================================
pause