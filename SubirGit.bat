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
git push

echo.
echo Listo. Presione una tecla para continuar.
pause