Welcome to the Apache JUDDI Project!

Here's some quick notes for building, testing and deploying JUDDI from source.

1) Aquire a JDK5 or higher and setup the JAVA_HOME environment variable
2) Aquire Apache Maven. Known working version: 3.0.4
3) Setup an environment variable, MAVEN_OPTS=-Xmx768m -XX:MaxPermSize=512m
4) Make sure the Maven/bin folder and the JDK/bin folders are in the current path
5) execute "mvn clean install"

That should build the whole project. Depending on your computer's speed, it can take up to 15 minutes to build.

To attach the debugger to the build process
mvn -Dmaven.surefire.debug clean install
It listens on port 5005 by default. More info on debugginb maven projects is here http://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html

To setup an Eclipse environment with support for building using the Google Web Toolkit portlets in Pluto, see the blog entry here http://apachejuddi.blogspot.com/2013_02_01_archive.html
Eclipse will initially complain about maven plugins.

To setup a Netbeans environment, the process is much simplier.
Install Netbeans and open the project. Compiling from Netbeans however doesn't work and you'll have to resort to command line builds


To build your changes locally and skip the the tests run:
mvn install -DskipTests=true

To also build the Pluto/GWT/Portlet interface
cd juddi-console
mvn clean install

To start Juddi's embedded Tomcat server:
juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\startup.bat
juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\startup.sh



When building on Windows, you may run into an error similar to this:

SEVERE: The business service was not found for the given key:  uddi:uddi.joepublisher.com:serviceone
org.apache.juddi.v3.error.InvalidKeyPassedException: The business service was not found for the given key:  uddi:uddi.jo
epublisher.com:serviceone
        at org.apache.juddi.validation.ValidatePublish.validateDeleteService(ValidatePublish.java:174)
        at org.apache.juddi.api.impl.UDDIPublicationImpl.deleteService(UDDIPublicationImpl.java:291)
        at org.apache.juddi.v3.tck.TckBusinessService.deleteService(TckBusinessService.java:228)
        at org.apache.juddi.v3.tck.TckBusinessService.deleteJoePublisherService(TckBusinessService.java:85)
        at org.apache.juddi.subscription.SubscriptionNotifierTest.teardown(SubscriptionNotifierTest.java:173)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
        at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
        at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
        at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:37)
        at org.junit.runners.ParentRunner.run(ParentRunner.java:220)
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
        at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
        at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:115)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)
Tests run: 2, Failures: 1, Errors: 1, Skipped: 0, Time elapsed: 3.661 sec <<< FAILURE!
testGetSubscriptionResults(org.apache.juddi.subscription.SubscriptionNotifierTest)  Time elapsed: 0.005 sec  <<< ERROR!
java.lang.NullPointerException
        at org.apache.juddi.subscription.SubscriptionNotifierTest.testGetSubscriptionResults(SubscriptionNotifierTest.ja
va:146)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
        at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
        at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
        at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
        at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
        at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:31)
        at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:73)
        at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:46)
        at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:180)
        at org.junit.runners.ParentRunner.access$000(ParentRunner.java:41)
        at org.junit.runners.ParentRunner$1.evaluate(ParentRunner.java:173)
        at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:28)
        at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:31)
        at org.junit.runners.ParentRunner.run(ParentRunner.java:220)
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
        at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
        at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:115)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)

org.apache.juddi.subscription.SubscriptionNotifierTest  Time elapsed: 0.038 sec  <<< FAILURE!
java.lang.AssertionError: No exception should be thrown.
        at org.junit.Assert.fail(Assert.java:91)
        at org.apache.juddi.v3.tck.TckBusinessService.deleteService(TckBusinessService.java:232)
        at org.apache.juddi.v3.tck.TckBusinessService.deleteJoePublisherService(TckBusinessService.java:85)
        at org.apache.juddi.subscription.SubscriptionNotifierTest.teardown(SubscriptionNotifierTest.java:173)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
        at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
        at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
        at org.junit.internal.runners.statements.RunAfters.evaluate(RunAfters.java:37)
        at org.junit.runners.ParentRunner.run(ParentRunner.java:220)
        at org.apache.maven.surefire.junit4.JUnit4Provider.execute(JUnit4Provider.java:252)
        at org.apache.maven.surefire.junit4.JUnit4Provider.executeTestSet(JUnit4Provider.java:141)
        at org.apache.maven.surefire.junit4.JUnit4Provider.invoke(JUnit4Provider.java:112)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.apache.maven.surefire.util.ReflectionUtils.invokeMethodWithArray(ReflectionUtils.java:189)
        at org.apache.maven.surefire.booter.ProviderFactory$ProviderProxy.invoke(ProviderFactory.java:165)
        at org.apache.maven.surefire.booter.ProviderFactory.invokeProvider(ProviderFactory.java:85)
        at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:115)
        at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:75)


Results :

Failed tests:   org.apache.juddi.subscription.SubscriptionNotifierTest: No exception should be thrown.

Tests in error:
  testGetSubscriptionResults(org.apache.juddi.subscription.SubscriptionNotifierTest)

Tests run: 141, Failures: 1, Errors: 1, Skipped: 0

Right click properties on the check out folder and uncheck the read only flag on the folder, then build again.

If that doesn't work and you have SOAP UI running, close it.