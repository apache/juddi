Title: Continuous Integration

## Overview

This project uses [Maven](http://maven.apache.org/) in conjunction with the [BuildBot](http://buildbot.net/) Continuous Integration System which is maintained by the Apache Software Foundation.

## Build Configurations

For a list of all the current build configurations, visit [this link](http://ci.apache.org/builders).

jUDDI currently builds with the following configuration matrix.

| JPA Provider	|	JDK			| Operating System 	| .NET Runtime		| Application Container	| SOAP Framework |
| --- 			| ---			| ---				| ---				| ---					| ---			 |
| OpenJPA		| OpenJDK 6		| Ubuntu			| Microsoft.NET 3.5	| Apache Tomcat 6.x		| Apache CXF	 |
| Hibernate		| Oracle JDK 6	| Windows 7			| Mono	3.x			| Apache Tomcat 6.x		| .NET WCF, .NET ASP.NET |

## Access

The following is a link to the continuous integration system used by the project.

 - [http://ci.apache.org/buildbot.html](http://ci.apache.org/buildbot.html)

## Notifiers

### Email

Configuration for notifying developers/users when a build is unsuccessful, including user information and notification mode is done via the [development mailing list](mailing-list.html).

### IRC

The jUDDI project also employs the BuildBot's IRC bot, which enables us to trigger builds manually and to make alterations to the build process.