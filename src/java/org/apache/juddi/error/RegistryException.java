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
package org.apache.juddi.error;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;

/**
 * Thrown to indicate that a UDDI Exception was encountered.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryException extends Exception implements RegistryObject
{
  // SOAP SOAPFault Actor
  private String faultActor;

  // SOAP SOAPFault Code
  private String faultCode;

  // SOAP SOAPFault SOAPMessage
  private String faultString;

  // UDDI DispositionReport
  private DispositionReport dispReport;

  /**
   * Constructs an empty UDDIException instance.
   */
  public RegistryException()
  {
    super();
  }

  /**
   * Constructs a UDDIException instance.
   * @param msg additional error information
   */
  public RegistryException(String msg)
  {
    super(msg);

    this.setFaultActor(null);
    this.setFaultCode(null);
    this.setFaultString(msg);
  }

  /**
   * Constructs a UDDIException instance.
   * @param ex the original exception
   */
  public RegistryException(Exception ex)
  {
    super(ex.getMessage());

    this.setFaultActor(null);
    this.setFaultCode(null);
    this.setFaultString(ex.getMessage());
  }

  /**
   * Sets the fault actor of this SOAP SOAPFault to the given value.
   * @param actor The new actor value for this SOAP SOAPFault.
   */
  public void setFaultActor(String actor)
  {
    this.faultActor = actor;
  }

  /**
   * Returns the fault actor of this SOAP SOAPFault.
   * @return The fault actor of this SOAP SOAPFault.
   */
  public String getFaultActor()
  {
    return this.faultActor;
  }

  /**
   * Sets the fault code of this SOAP SOAPFault to the given value.
   * @param code The new code number for this SOAP SOAPFault.
   */
  public void setFaultCode(String code)
  {
    this.faultCode = code;
  }

  /**
   * Returns the fault code of this SOAP SOAPFault.
   * @return The fault code of this SOAP SOAPFault.
   */
  public String getFaultCode()
  {
    return this.faultCode;
  }

  /**
   * Sets the fault string of this SOAP SOAPFault to the given value.
   * @param value The new fault string for this SOAP SOAPFault.
   */
  public void setFaultString(String value)
  {
    this.faultString = value;
  }

  /**
   * Returns the fault string of this SOAP SOAPFault.
   * @return The fault string of this SOAP SOAPFault.
   */
  public String getFaultString()
  {
    return this.faultString;
  }

  /**
   * Sets the UDDI DispositionReport value to the instance
   * specified
   * @param dispRpt The new UDDI DispositionReport instance for
   *  this SOAP Fault.
   */
  public void setDispositionReport(DispositionReport dispRpt)
  {
    this.dispReport = dispRpt;
  }

  /**
   * Returns the disposition report associated with this jUDDI exception. It
   * uses the results Vector to determine if a disposition report is present
   * and should be returned.
   * @return The disposition report associated with this jUDDI exception.
   */
  public DispositionReport getDispositionReport()
  {
    return this.dispReport;
  }

  /**
   * Adds a result instance to this Exception. Multiple result objects
   * may exist within a DispositionReport
   */
  public void addResult(Result result)
  {
    if (this.dispReport==null)
      this.dispReport = new DispositionReport();
    this.dispReport.addResult(result);
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buff = new StringBuffer(100);

    buff.append("RegistryException: "+getMessage()+"\n");
    buff.append(" SOAPFault Actor: "+getFaultActor()+"\n");
    buff.append(" SOAPFault Code: "+getFaultCode()+"\n");
    buff.append(" SOAPFault String: "+getFaultString()+"\n");

    // pull the DispositionReport out if it's present
    DispositionReport dispRpt = getDispositionReport();
    if (dispRpt != null)
    {
      buff.append(" Operator: "+dispRpt.getOperator()+"\n");

      Vector results = dispRpt.getResultVector();
      if ((results != null) && (results.size() > 0))
      {
        for (int i=0; i<results.size(); i++)
        {
          Result result = (Result)results.elementAt(i);
          buff.append(" >Errno: "+result.getErrno()+"\n");

          ErrInfo errInfo = result.getErrInfo();
          buff.append(" >Error Code: "+errInfo.getErrCode()+"\n");
          buff.append(" >Error Info Text: "+errInfo.getErrMsg()+"\n");
        }
      }
      else
        buff.append("\n >[No Results were present]");
    }
    else
      buff.append("\n [A DispositionReport was not present]");

    return buff.toString();
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws RegistryException
  {
    throw new UnsupportedException("Additional error information.");
  }
}