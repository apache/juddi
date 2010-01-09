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
package org.apache.juddi.xlt.action.http;

import org.apache.juddi.xlt.validators.CommonValidator;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Go to the jUDDI portal page.
 *
 * 
 */
@SuppressWarnings("deprecation")
public class Logout extends AbstractHtmlPageAction
{
    /**
     * The element that got clicked to execute this action.
     */
    
	ClickableElement clickable;

    /**
     * The action's constructor.
     *
     * @param previousAction the previous action
     */
    public Logout(AbstractHtmlPageAction previousAction)
    {
        super(previousAction, null);
    }

    /* (non-Javadoc)
     * @see com.xceptance.xlt.api.actions.AbstractAction#preValidate()
     */
    @Override
    public void preValidate() throws Exception
    {
        HtmlPage page = getPreviousAction().getHtmlPage();

        String clickableXPath = "id('logout')/a[1]";
        clickable = HtmlPageUtils.findSingleHtmlElementByXPath(page, clickableXPath);

    }

    /* (non-Javadoc)
     * @see com.xceptance.xlt.api.actions.AbstractAction#execute()
     */
    @Override
    public void execute() throws Exception
    {
        loadPageByClick(clickable, 10000);
    }

    /* (non-Javadoc)
     * Ensure that all the necessary portlets show up on the page
     */
    @Override
    public void postValidate() throws Exception
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