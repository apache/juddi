/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */
package org.apache.juddi.transport.axis;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.message.SOAPEnvelope;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AxisHandler extends BasicHandler
{
  /**
   * Init is called when the chain containing this
   * Handler object is instantiated.
   */
  public void init()
  {
    super.init();
  }

  /**
   * Cleanup is called when the chain containing this
   * Handler object is done processing the chain.
   */
  public void cleanup()
  {
    super.cleanup();
  }

  /**
   * Invoke is called to do the actual work of the
   * Handler object. If there is a fault during the
   * processing of this method it is invoke's job to
   * catch the exception and undo any partial work that
   * has been completed. Once we leave 'invoke' if a
   * fault is thrown, this classes 'onFault' method
   * will be called. Invoke should rethrow any
   * exceptions it catches, wrapped in an AxisFault.
   */
  public void invoke(MessageContext context)
  {
    Message response = new Message(new SOAPEnvelope());
    /*Message request = context.getRequestMessage();

    // Determine if this message came from through
    // the Publish, Inquiry or Admin API and handle
    // it appropriately.

    Object servlet = context.getProperty("transport.http.servlet");*/

    new AxisProcessor(response,context);

    context.setResponseMessage(response);
  }
}