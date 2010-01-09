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
package org.apache.juddi.xlt.validators;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Common validator for most actions. This validator is simply a collection
 * of other validators that are usually run in most actions. Having such a 
 * class makes it easier to insert the usual validation in the postValidate
 * method of an action.
 * 
 * The validators included here should apply to all pages. 
 * 
 * @author Jeremi Thebeau (Xceptance Software Technologies GmbH)
 * @version $Id$
 */
public class CommonValidator
{
	/**
     * Make a stateless singleton available.
     */
    private static final CommonValidator instance = new CommonValidator();
    
    /**
     * Validates common validators when a XltHtmlPage is passed.
     * 
     * @param page
     *            the XLT html page to check
     */
    public void validate(final HtmlPage page) throws Exception
    {
        // check the response code, the singleton instance validates for 200
        HttpResponseCodeValidator.getInstance().validate(page);

        // check the content length, compare delivered content length to the content
        // length that was announced in the http response header
        ContentLengthValidator.getInstance().validate(page);

        // check for complete html
        HtmlEndTagValidator.getInstance().validate(page);

        // check for the header of the blog
        //BlogHeadlineValidator.getInstance().validate(page);

        // check for the footer of the blog
        FooterValidator.getInstance().validate(page);
    }

    /**
     * Validates common validators when a LightWeightPage is passed.
     * 
     * @param page
     *            the light weight page to check
     
    public void validate(final LightWeightPage page) throws Exception
    {
    	// check the response code, the singleton instance validates for 200
        HttpResponseCodeValidator.getInstance().validate(page);

        // check the content length, compare delivered content length to the content
        // length that was announced in the http response header
        ContentLengthValidator.getInstance().validate(page);

        // check for complete html
        HtmlEndTagValidator.getInstance().validate(page);

        // check for the header of the blog
        BlogHeadlineValidator.getInstance().validate(page);

        // check for the footer of the blog
        FooterValidator.getInstance().validate(page);
    }*/
    public static CommonValidator getInstance()
    {
        return instance;
    }

	
}
