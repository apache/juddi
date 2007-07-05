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
package org.apache.juddi.datastore.jdbc;

import java.sql.Connection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class TestAll extends TestCase
{
  public TestAll(String testName)
  {
    super(testName);
  }

  public static Test suite()
  {
  	Connection conn = null;
  	TestSuite suite = new TestSuite();

    try 
		{   
//    suite.addTest(new TestBusinessEntityTable.test(conn));
//    suite.addTest(new TestBusinessDescTable.test(conn));
//    suite.addTest(new TestBusinessCategoryTable.test(conn));
//    suite.addTest(new TestBusinessIdentifierTable.test(conn));
//    suite.addTest(new TestBusinessNameTable.test(conn));
//    suite.addTest(new TestDiscoveryURLTable.test(conn));
//    suite.addTest(new TestContactTable.test(conn));
//    suite.addTest(new TestContactDescTable.test(conn));
//    suite.addTest(new TestPhoneTable.test(conn));
//    suite.addTest(new TestEmailTable.test(conn));
//    suite.addTest(new TestAddressTable.test(conn));
//    suite.addTest(new TestAddressLineTable.test(conn));
//    suite.addTest(new TestBusinessServiceTable.test(conn));
//    suite.addTest(new TestServiceDescTable.test(conn));
//    suite.addTest(new TestServiceCategoryTable.test(conn));
//    suite.addTest(new TestServiceNameTable.test(conn));
//	  suite.addTest(new TestBindingTemplateTable.test(conn));
//    suite.addTest(new TestBindingDescTable.test(conn));
//    suite.addTest(new TestTModelInstanceInfoTable.test(conn));
//    suite.addTest(new TestTModelInstanceInfoDescTable.test(conn));
//    suite.addTest(new TestInstanceDetailsDescTable.test(conn));
//    suite.addTest(new TestInstanceDetailsDocDescTable.test(conn));
//    suite.addTest(new TestTModelTable.test(conn));
//    suite.addTest(new TestTModelDescTable.test(conn));
//    suite.addTest(new TestTModelCategoryTable.test(conn));
//    suite.addTest(new TestTModelIdentifierTable.test(conn));
//    suite.addTest(new TestTModelDocDescTable.test(conn));
//    suite.addTest(new TestPublisherAssertionTable.test(conn));
//    suite.addTest(new TestAuthTokenTable.test(conn));
//    suite.addTest(new TestPublisherTable.test(conn));
    }
    catch(Exception ex) 
		{
    	ex.printStackTrace();
		}
    
    return suite;
  }

  public static void main(String args[])
  {
    String[] testCaseName = { TestAll.class.getName() };
    junit.textui.TestRunner.main(testCaseName);
  }
}