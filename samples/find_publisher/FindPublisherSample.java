/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.juddi.client.*;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.business.*;
import org.apache.juddi.datatype.request.*;
import org.apache.juddi.datatype.response.*;
import org.apache.juddi.error.*;
import org.apache.juddi.registry.*;

import java.util.Vector;
import java.io.File;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Andy Cutright (acutright@users.sourceforge.net)
 *
 * This class demonstrates one of the proprietary jUDDI UDDI Registry
 * extension. To enable the registry to respond, the application's
 * /admin context must be enabled. Edit web.xml, enabling this section:
 *
 *  <servlet-mapping>
 *   <servlet-name>jUDDIAdminServlet</servlet-name>
 *   <url-pattern>/admin</url-pattern>
 *  </servlet-mapping>
 *
 */
public class FindPublisherSample
{
  public static void main(String[] args)
  {
    RegistryProxy proxy = new RegistryProxy();

    try
    {
      /**
       * Retrieve all publishers listed in the registry
       */
      PublisherList publisherList = proxy.find_publisher("%","%",null,0);

      if (publisherList == null) {
        System.err.println("Unable to invoke 'find_publisher'");
        System.exit(-1);
      }
      PublisherInfos publisherInfos = publisherList.getPublisherInfos();
      if (publisherInfos == null) {
        System.err.println("Unable to retrieve 'PublisherInfos'");
        System.exit(-2);
      }
      Vector publisherInfoVector = publisherInfos.getPublisherInfoVector();
      if (publisherInfoVector == null) {
        System.out.println("No publishers found matching criteria");
        System.exit(0);
      }
      for (int index = 0; index < publisherInfoVector.size(); index++) {
        PublisherInfo publisherInfo = (PublisherInfo)publisherInfoVector.elementAt(index);
        System.out.println("Publisher ID is ["
                           + publisherInfo.getPublisherID()
                           + "], Publisher name is ["
                           + publisherInfo.getNameValue()
                           + "]");
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}