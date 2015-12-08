/*
 * Copyright 2015 The Apache Software Foundation.
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
package org.apache.juddi.gui.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

/**
 *
 * @author alex
 */
public abstract class BaseTest {
     public static String BASE_URL="http://localhost:8880/juddi-gui/";
     public static List<WebDriver> drivers;

     @BeforeClass
     public static void setUpClass() {
          drivers = new ArrayList<WebDriver>();
          try {
               DesiredCapabilities capabilities = DesiredCapabilities.chrome();
               capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));
               drivers.add(new ChromeDriver(capabilities));
          } catch (Throwable ex) {
               ex.printStackTrace();
          }
          try {
               drivers.add(new FirefoxDriver());
          } catch (Throwable ex) {
               ex.printStackTrace();
          }
           try {
               drivers.add(new SafariDriver());
          } catch (Throwable ex) {
               ex.printStackTrace();
          }
            try {
               drivers.add(new InternetExplorerDriver());
          } catch (Throwable ex) {
               ex.printStackTrace();
          }
         
          Assert.assertFalse(drivers.isEmpty());
     }

     @AfterClass
     public static void tearDownClass() {
          for (int i=0; i < drivers.size(); i++){
              drivers.get(i).close();
          }
          drivers.clear();
     }

     @Before
     public void setUp() {
     }

     @After
     public void tearDown() {
     }

}
