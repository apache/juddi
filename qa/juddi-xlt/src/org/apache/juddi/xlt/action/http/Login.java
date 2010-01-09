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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Login to Pluto. 
 *
 * 
 */
@SuppressWarnings("deprecation")
public class Login extends AbstractHtmlPageAction
{
    String username;
    String password;
    ClickableElement clickable;
    HtmlForm form;

    /**
     * The action's constructor.
     *
     * @param previousAction the previous action
     */
    public Login(AbstractHtmlPageAction previousAction, String username, String password)
    {
        super(previousAction, null);
        this.username = username;
        this.password = password;
    }

    /* (non-Javadoc)
     * @see com.xceptance.xlt.api.actions.AbstractAction#preValidate()
     */
    @Override
    public void preValidate() throws Exception
    {
        HtmlPage page = getPreviousAction().getHtmlPage();

        //Find the form
        String formXPath = "id('content')/form[1]";
        form = HtmlPageUtils.findSingleHtmlElementByXPath(page, formXPath);

        //Find the login button
        clickable = form.getElementById("j_login");

    }

    /* (non-Javadoc)
     * @see com.xceptance.xlt.api.actions.AbstractAction#execute()
     */
    @Override
    public void execute() throws Exception
    {

        HtmlInput input0 = form.getInputByName("j_username");
        input0.setValueAttribute(username);

        HtmlInput input1 = form.getInputByName("j_password");
        input1.setValueAttribute(password);

        loadPageByClick(clickable, 10000);
    }

    /* (non-Javadoc)
     * Ensure that the received page has an 'about' portelet on it.
     */
    @Override
    public void postValidate() throws Exception
    {
        HtmlPage page = getHtmlPage();

        String xpathToText0 = "id('portlets-left-column')/div[@class='portlet'][1]/div[@class='body'][1]/table[1]/tbody[1]/tr[1]/td[1]/h2[1]";
        HtmlElement txtElement0 = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToText0);
        Assert.assertTrue("Element does not contain text 'About Pluto Portal Driver'", txtElement0.asText().contains("About Pluto Portal Driver"));

        //Common validator
		CommonValidator.getInstance().validate(page);
    }
}