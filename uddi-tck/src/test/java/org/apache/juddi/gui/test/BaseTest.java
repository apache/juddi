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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.SystemUtils;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDINode;
import org.apache.juddi.v3.tck.TckCommon;
import org.apache.juddi.v3.tck.TckPublisher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

/**
 *
 * @author alex
 */
public abstract class BaseTest {

    public static String BASE_URL = "http://localhost:8880/juddi-gui/";
    public static final List<WebDriver> drivers = new ArrayList<>();

    @BeforeClass
    public static void setUpClass() {
        if (!TckPublisher.isJUDDI())
            return;
        try {
            UDDIClient client = new UDDIClient();
            client.start();
            UDDINode uddiNode = client.getClientConfig().getUDDINode("uddiv3");
            BASE_URL = (String) uddiNode.getProperties().get("juddigui");
            if (BASE_URL==null) {
                BASE_URL = "http://localhost:8880/juddi-gui/";
            }
            
        } catch (Exception e) {
            //we should not have any issues reading the config
            e.printStackTrace();
            Assert.fail();
        }
        //setup properties for driver locations....
        
        boolean is64bit = System.getProperty("os.arch") != null && System.getProperty("os.arch").contains("64");
        String arch = is64bit ? "64bit" : "32bit";
        String os = "";
        String executableExtension = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            os = "windows";
            executableExtension = ".exe";
        }
        if (SystemUtils.IS_OS_LINUX) {
            os = "linux";
        }
        //TODO mac?

        File driversPath = new File("drivers");

        File chrome = new File(driversPath, os + "/googlechrome/" + arch + "/chromedriver" + executableExtension);
        if (chrome.exists()) {
            System.setProperty("webdriver.chrome.driver", chrome.getAbsolutePath());
        }
        File firefox = new File(driversPath, os + "/marionette/" + arch + "/geckodriver" + executableExtension);
        if (firefox.exists()) {
            System.setProperty("webdriver.gecko.driver", firefox.getAbsolutePath());
        }

        File ie = new File(driversPath, os + "/internetexplorer/" + arch + "/IEDriverServer" + executableExtension);
        if (ie.exists()) {
            System.setProperty("webdriver.ie.driver", ie.getAbsolutePath());
        }
        File edge = new File(driversPath, os + "/edge/" + arch + "/MicrosoftWebDriver" + executableExtension);
        if (edge.exists()) {
            System.setProperty("webdriver.edge.driver", edge.getAbsolutePath());
        }

        File opera = new File(driversPath, os + "/operachromium/" + arch + "/operadriver" + executableExtension);
        if (opera.exists()) {
            System.setProperty("webdriver.opera.driver", opera.getAbsolutePath());
        }
/*
        try {
            drivers.add(new SafariDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        try {
            //DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            //capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));
            drivers.add(new ChromeDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }*/
        try {
            //FirefoxProfile firefoxProfile = new FirefoxProfile();
            //firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);
            drivers.add(new FirefoxDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
/*
        try {
            //DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            //capabilities.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
            //capabilities.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
            drivers.add(new InternetExplorerDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        try {

            drivers.add(new EdgeDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        try {

            drivers.add(new OperaDriver());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
*/
    }

    @AfterClass
    public static void tearDownClass() {
        for (int i = 0; i < drivers.size(); i++) {
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
