/*
 * Created on Jan 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.juddi.handler;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author sviens
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class HandlerTestCase extends TestCase
{
	public HandlerTestCase(String arg0)
	{
		super(arg0);
	}
	
	protected final String getXMLString(Element element)
	{
		StringWriter writer = new StringWriter();
        
		XMLUtils.writeXML(element,writer);

		String xmlString = writer.toString();

		try
		{
			writer.close();
		}
		catch(IOException exp)
		{
		}

		return xmlString;
	}
}
