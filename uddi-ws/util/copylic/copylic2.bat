@echo off
SET relpath=%~p1
:loop
If "%relpath:~0,4%"=="java" GoTo :done
SET relpath=%relpath:~1,1000%
rem echo %relpath%
GoTo :loop

:done
md ..\..\src\main\%relpath%
copy lic.txt + %1 ..\..\src\main\%relpath%%~n1%~x1
