/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.xlt.test.http;

import org.apache.juddi.xlt.action.http.BrowseJuddiNode;
import org.apache.juddi.xlt.action.http.GoToJuddiPortal;
import org.apache.juddi.xlt.action.http.Homepage;
import org.apache.juddi.xlt.action.http.Login;
import org.junit.Test;

import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;

/**
 * Browse the UDDI through the UDDIBrowser Portlet
 */
public class TBrowse extends AbstractTestCase
{
	String username = getProperty("username");
	String password = getProperty("password");
	String urlString = getProperty("url");
	AbstractHtmlPageAction lastAction;
	
	@Test
	public void browse() throws Throwable
	{
		Homepage homepage = new Homepage(urlString);
		homepage.run();
		lastAction = homepage;
		
		Login login = new Login(lastAction, username, password);
		login.run();
		lastAction = login;
		
		lastAction.getWebClient().setJavaScriptEnabled(true);
		
		GoToJuddiPortal goToJuddiPortal = new GoToJuddiPortal(lastAction);
		goToJuddiPortal.run();
		lastAction = goToJuddiPortal;
		
		BrowseJuddiNode browseJuddiNode = new BrowseJuddiNode(lastAction);
		browseJuddiNode.run();
		lastAction = browseJuddiNode;
		
	}
	
}
