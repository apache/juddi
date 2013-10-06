@echo off
if "%NUNIT_HOME%"=="" (
	set NUNIT_HOME=C:\juddi\NUnit-2.6.1
	echo WARN - NUNIT_HOME is not defined, using default value, which is probably wrong
)
if not exist "%NUNIT_HOME%"   (
	echo The path %NUNIT_HOME% does not exist, download Nunit and setup the environment variable
	goto exit
)
echo Using %NUNIT_HOME% for NUNIT location

rem this batch file is designed to help test the juddi-client.net with an active UDDI server, primarily to determine if there are any
rem serialization issues. It must be ran AFTER the complete java build has completed and after the juddi-client.net has been built
rem it is designed to be ran from the root check out folder (ex c:\juddi\trunk\)
cd juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\
call startup.bat
cd ..\..\..\..\..
cd juddi-client.net
rem wait until tomcat has started
rem ping 192.0.2.2 -n 1 -w 20000 > nul

rem run tests
set exitcode=0

%NUNIT_HOME%\bin\nunit-console.exe juddi-client.net-integration.test\bin\Debug\juddi-client.net-integration.test.dll
set exitcode=%errorlevel%
echo Exit code captured as %exitcode%
cd ..
cd juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\
call shutdown.bat
ping 192.0.2.2 -n 1 -w 1000 > nul
call shutdown.bat
ping 192.0.2.2 -n 1 -w 1000 > nul
call shutdown.bat
cd ..\..\..\..\..
echo Exit code %exitcode%
exit /b %exitcode%
goto ok

:exit
exit /b 1

:ok