@echo off
rem Hi, this is for running nunit tests on ASF's build bot CI servers, specifically the Windows 7 bb server
if "%NUNIT_HOME%"=="" (
	set NUNIT_HOME=c:\juddi\NUnit-2.6.1
	echo WARN - NUNIT_HOME is not defined, using default value, which is probably wrong
)
if not exist "%NUNIT_HOME%"   (
	echo The path %NUNIT_HOME% does not exist, download Nunit and setup the environment variable
	goto exit
)
echo Using %NUNIT_HOME% for NUNIT location

set errorlevel=
rem c:\juddi\NUnit-2.6.1\bin\nunit-console.exe juddi-client.net\juddi-client.net.test\bin\Debug\juddi-client.net.test.dll
"%NUNIT_HOME%\bin\nunit-console.exe" juddi-client.net\juddi-client.net.test\bin\Debug\juddi-client.net.test.dll
exit /b %errorlevel%
