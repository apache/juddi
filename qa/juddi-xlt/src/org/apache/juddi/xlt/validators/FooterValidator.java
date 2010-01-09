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
package org.apache.juddi.xlt.validators;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * @author Jeremi Thebeau
 *
 */
public class FooterValidator 
{
	/**
     * The instance
     */
    private final static FooterValidator instance = new FooterValidator();

	/* (non-Javadoc)
	 * @see com.xceptance.xlt.api.validators.Validator#validate(com.xceptance.xlt.api.htmlunit.XltHtmlPage)
	 */
	public void validate(HtmlPage page) throws Exception
	{
		// div #copyright
		
		String xpathToFooter = "id('footer')";
        HtmlElement footerText = HtmlPageUtils.findSingleHtmlElementByXPath(page, xpathToFooter);
        Assert.assertTrue("Element does not contain text '2003-2009 Apache Software Foundation'", footerText.asText().contains("2003-2009 Apache Software Foundation"));
		return;
		
	}
	public static FooterValidator getInstance()
    {
        return instance;
    }
}
