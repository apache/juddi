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
/**
 * 
 */
package org.apache.juddi.xlt.action.http;

import java.net.URL;

import org.apache.juddi.xlt.validators.CommonValidator;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;


/**
 * Retrieves the homepage for a given url.
 *
 */
public class Homepage extends AbstractHtmlPageAction 
{
	
	/**
	 * The homepage url to open
	 */
	private URL url;
	private String urlString;
	
	/**
	 * Constructor to except the url parameter as a string
	 * @param urlString
	 */
	public Homepage(String urlString)
	{
		super(null);
		this.urlString = urlString;
		
	}
	
	
	@Override
	public void preValidate() throws Exception 
	{
		URL url = new URL(urlString);
		this.url = url; 
		
		Assert.assertNotNull("Url is null", url);
	}
	
	
	@Override
	protected void execute() throws Exception 
	{
		loadPage(url);

	}

	@Override
	protected void postValidate() throws Exception 
	{
		
		//Get homepage
		HtmlPage page = getHtmlPage();
		
		//Common validator
		CommonValidator.getInstance().validate(page);
		
		//Validate login text id('content')/form/fieldset/legend
		String xpathToLoginText = "id('content')/form/fieldset/legend";
        HtmlElement loginText = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToLoginText);
        Assert.assertTrue("Element does not contain text 'Login to Pluto'", loginText.asText().contains("Login to Pluto"));
	}
}
