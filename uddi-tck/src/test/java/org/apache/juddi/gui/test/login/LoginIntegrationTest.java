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
package org.apache.juddi.gui.test.login;

import org.apache.juddi.gui.test.BaseTest;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 *
 * @author alex
 */
public class LoginIntegrationTest extends BaseTest {

    @Test
    public void testSplashAndLoginLogout() throws Exception {
        System.out.println("testSplashAndLoginLogout");
        Assume.assumeFalse(drivers.isEmpty());
        for (int i = 0; i < drivers.size(); i++) {
            System.out.println("nav to " + BASE_URL);
            drivers.get(i).navigate().to(BASE_URL);
            if ((BASE_URL + "/home.jsp").equalsIgnoreCase(drivers.get(i).getCurrentUrl())) {
                //already based the i18n screen
            } else {
                drivers.get(i).findElement(By.id("login_page_go")).click();
            }
            //make sure the gui is pointed to the right server.
            drivers.get(i).navigate().to("javascript:switchNode('integrationTest')");

            drivers.get(i).navigate().to(BASE_URL + "/home.jsp");

            //todo put these in a props file
            drivers.get(i).findElement(By.id("username")).sendKeys("juddi");
            drivers.get(i).findElement(By.id("password")).sendKeys("juddi");
            drivers.get(i).findElement(By.id("loginbutton")).click();
            Thread.sleep(2000);
            Assert.assertTrue(drivers.get(i).findElement(By.id("logout_button")).isDisplayed());
            drivers.get(i).findElement(By.id("logout_button")).click();
            try {
                drivers.get(i).findElement(By.id("logout_button"));
                Assert.fail("logout failed");
            } catch (Exception ex) {
            }

        }
    }
}
