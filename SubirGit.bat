@echo off
cls
echo ========================================
echo   AUTOMATIZADOR DE GIT - SUBIENDO CAMBIOS
echo ========================================
echo.

REM Agrega todos los cambios del proyecto
git add .

echo.
REM Pide un mensaje para el commit
set /p mensaje="Escribe el mensaje para el commit: "

REM Realiza el commit con el mensaje ingresado
git commit -m "%mensaje%"

echo.
echo Subiendo los cambios a GitHub...
git push origin main

echo.
echo ========================================
echo   ¡PROCESO FINALIZADO CON EXITO!
echo ========================================
pause