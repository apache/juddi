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

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Click on 'An Apache jUDDI node' to browse it. 
 */
@SuppressWarnings("deprecation")
public class BrowseJuddiNode extends AbstractHtmlPageAction
{
    HtmlElement clickable;

    /**
     * The action's constructor.
     *
     * @param previousAction the previous action
     */
    public BrowseJuddiNode(AbstractHtmlPageAction previousAction)
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

        String clickableXPath = "id('browser')/table/tbody/tr[2]/td[1]/div/div[2]/table/tbody/tr/td[1]";
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
     * @see com.xceptance.xlt.api.actions.AbstractAction#postValidate()
     */
    @Override
    public void postValidate() throws Exception
    {
        HtmlPage page = getHtmlPage();

        String xpathToNodeDescription = "id('gwt-uid-2')";
        HtmlElement txtElement0 = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToNodeDescription);
        Assert.assertTrue("Element does not contain text 'Services owned by this business'", txtElement0.asText().contains("An Apache jUDDI Node"));

        
    }
}