Title: Source Code

## Overview

This project uses Git to manage its source code. Instructions on Subversion use can be found at http://git-scm.com/.
Web Access

The following is a link to the view online source repository.

    [https://git-wip-us.apache.org/repos/asf?p=juddi.git;a=summary)]
	
### Developer access

Everyone can access the Git repository via HTTP, but Committers must checkout the Subversion repository via HTTPS.

    $ git clone https://git-wip-us.apache.org/repos/asf/juddi.git
	
To commit changes to the repository, execute the following command to commit your changes (git will prompt you for your password)

    $ git commit  .

Please append comments using the following format:

	JUDDI-1234 A description of the change(s)
	JUDDI-689 A description of the change(s)
	
This will auto link the commit with the JIRA tickets
	
If you already are a Apache committer, then you can use push to synch your local changes to the main source repository. (You did remember to run ALL the tests before committing? mvn clean install -Pdist)

	$ git push
		
### Access through a proxy

The Git client can go through a HTTP proxy, if you configure it to do so. 

	$ git config --global http.proxy (server)

See the [http://git-scm.com/docs](Manual) for more information.


### Approximate build times

Often (well, not really that often) we get asked about how long it takes to build jUDDI. Here you go.

Numbers below are on Windows 10, SSD, AMD 8 core CPU and with linear surefire execution.

| Configuration						   | Build time (hh:mm:ss)  | Description |
| ------------------------------------ | ---------------------- | --------------------------------------------- |
| mvn clean install -DskipTests=true   | 00:01:16               | skipping the tests
| mvn clean install				       | 00:27:25               | the normal build with tests
| nvn clean install -T 1C 			   | 00:15:33               | the normal build with one thread per CPU core
| mvn clean install -Pdist			   | 00:41:02               | just like the release profile but without sigatures, includes integration tests
| mvn clean install -Pdist -T 1C 	   | 00:40:30               | just like the release profile but without sigatures, includes integration tests
| mvn release:prepare -Papache-release |                        | this happens when we cut a release (the whole 9 yards), includes integration tests
| mvn site							   | 00:16:37               | generate the website
| mvn site:stage					   | 00:00:28               | prepare the site for upload

