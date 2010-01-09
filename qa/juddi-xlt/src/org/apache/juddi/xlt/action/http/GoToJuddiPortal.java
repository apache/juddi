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
public class GoToJuddiPortal extends AbstractHtmlPageAction
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
    public GoToJuddiPortal(AbstractHtmlPageAction previousAction)
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

        String clickableXPath = "id('navigation')/li[1]/a[1]";
        clickable = HtmlPageUtils.findSingleHtmlElementByXPath(page, clickableXPath);

    }

    /* (non-Javadoc)
     * @see com.xceptance.xlt.api.actions.AbstractAction#execute()
     */
    @Override
    public void execute() throws Exception
    {
        loadPageByClick(clickable, 60000);
    }

    /* (non-Javadoc)
     * Ensure that all the necessary portlets show up on the page
     */
    @Override
    public void postValidate() throws Exception
    {
        HtmlPage page = getHtmlPage();
        
        //Common validator
        CommonValidator.getInstance().validate(page);

        //Check for the UDDIBrowser Portlet
        String xpathToText0 = "id('portlets-left-column')/div[@class='portlet'][1]/div[@class='header'][1]/h2[@class='title'][1]";
        HtmlElement txtElement0 = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToText0);
        Assert.assertTrue("Element does not contain text 'UDDIBrowser Portlet'", txtElement0.asText().contains("UDDIBrowser Portlet"));

        //Check for the UDDISearch Portlet
        String xpathToText1 = "id('portlets-right-column')/div[@class='portlet'][1]/div[@class='header'][1]/h2[@class='title'][1]";
        HtmlElement txtElement1 = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToText1);
        Assert.assertTrue("Element does not contain text 'UDDISearch Portlet'", txtElement1.asText().contains("UDDISearch Portlet"));

        //Check for the UDDISubscriptionNotification Portlet
        String xpathToText2 = "id('portlets-left-column')/div[@class='portlet'][2]/div[@class='header'][1]/h2[@class='title'][1]";
        HtmlElement txtElement2 = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToText2);
        Assert.assertTrue("Element does not contain text 'UDDISubscriptionNotification Portlet'", txtElement2.asText().contains("UDDISubscriptionNotification Portlet"));
    }
}