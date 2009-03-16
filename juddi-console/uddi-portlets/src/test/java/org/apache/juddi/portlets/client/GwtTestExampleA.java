package org.apache.juddi.portlets.client;

import junit.framework.Assert;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestExampleA extends GWTTestCase {

   public String getModuleName() {
      return "org.apache.juddi.portlets.Application";
   }

   public void testSomething() {
      // Not much to actually test in this sample app
      // Ideally you would test your Controller here (NOT YOUR UI)
      // (Make calls to RPC services, test client side model objects, test client side logic, etc)
      Assert.assertTrue(true);
   }
}