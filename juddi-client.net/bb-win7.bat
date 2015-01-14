@echo off
rem Licensed to the Apache Software Foundation (ASF) under one
rem or more contributor license agreements.  See the NOTICE file
rem distributed with this work for additional information
rem regarding copyright ownership.  The ASF licenses this file
rem to you under the Apache License, Version 2.0 (the
rem "License"); you may not use this file except in compliance
rem with the License.  You may obtain a copy of the License at
rem 
rem   http://www.apache.org/licenses/LICENSE-2.0
rem 
rem Unless required by applicable law or agreed to in writing,
rem software distributed under the License is distributed on an
rem "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
rem KIND, either express or implied.  See the License for the
rem specific language governing permissions and limitations
rem under the License.
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
