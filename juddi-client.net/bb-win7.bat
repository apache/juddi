@echo off
rem Hi, this is for running nunit tests on ASF's build bot CI servers, specifically the Windows 7 bb server

set errorlevel=
c:\juddi\NUnit-2.6.1\bin\nunit-console.exe juddi-client.net\juddi-client.net.test\bin\Debug\juddi-client.net.test.dll
rem "C:\Program Files\NUnit 2.6.2\bin\nunit-console.exe" juddi-client.net\juddi-client.net.test\bin\Debug\juddi-client.net.test.dll
exit /b %errorlevel%
