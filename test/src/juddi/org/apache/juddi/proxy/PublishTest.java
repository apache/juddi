/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.proxy;

import junit.framework.*;

/**
 * @author Steve Viens (sviens@users.sourceforge.net)
 */
public class PublishTest extends TestCase
{
  public PublishTest(String testName)
  {
      super(testName);
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite();

//    suite.addTest(AddPublisherAssertionsTest.suite());
//    suite.addTest(DeleteBindingTest.suite());
//    suite.addTest(DeleteBusinessTest.suite());
//    suite.addTest(DeletePublisherAssertionTest.suite());
//    suite.addTest(DeleteServiceTest.suite());
//    suite.addTest(DeleteTModelTest.suite());
//    suite.addTest(DiscardAuthTokenTest.suite());
//    suite.addTest(GetAssertionStatusReport.suite());
//    suite.addTest(GetAuthTokenTest.suite());
//    suite.addTest(GetPublisherAssertionsTest.suite());
//    suite.addTest(GetRegisteredInfoTest.suite());
//    suite.addTest(SaveBindingTest.suite());
//    suite.addTest(SaveBusinessTest.suite());
//    suite.addTest(SaveServiceTest.suite());
//    suite.addTest(SaveTModelTest.suite());
//    suite.addTest(SetPublisherAssertionsTest.suite());

    return suite;
  }

  public static void main(String args[])
  {
    String[] testCaseName = { PublishTest.class.getName() };
    junit.textui.TestRunner.main(testCaseName);
  }
}