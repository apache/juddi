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
package org.apache.juddi.error;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
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
   * Constructs a RegistryException instance.
   * @param msg additional error information
   */
  public RegistryException(String msg)
  {
    super(msg);

    setFaultCode(null);
    setFaultString(msg);
    setFaultActor(null);
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  public RegistryException(Exception ex)
  {
    super(ex);
    
    if (ex != null)
    {
      // Not sure why this would ever happen but 
      // just in case we are asked to create a new
      // RegistryException using values from another
      // let's be sure to grab all relevant values.
      //
      if (ex instanceof RegistryException)
      {
        RegistryException regex = (RegistryException)ex;
        setFaultCode(regex.getFaultCode());
        setFaultString(regex.getFaultString());
        setFaultActor(regex.getFaultActor());
        setDispositionReport(regex.getDispositionReport());
      }
      else // Not a RegistryException (or subclass)
      {
        setFaultString(ex.getMessage());
      }
    }
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  public RegistryException(String fCode,String fString,String fActor,DispositionReport dispRpt)
  {
    super(fString);

    setFaultCode(fCode);
    setFaultString(fString);
    setFaultActor(fActor);
    setDispositionReport(dispRpt);
  }

  /**
   * Constructs a RegistryException instance.
   * @param ex the original exception
   */
  RegistryException(String fCode,int errno,String msg)
  {
    super(buildMessage(errno,msg));

    String errCode = Result.lookupErrCode(errno);

    setFaultCode(fCode);
    setFaultString(getMessage());
    addResult(new Result(errno,errCode,getMessage()));
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
    String msg = getMessage();
    if (msg == null)
      return "";
    else
      return getMessage();
  }
  
  private static final String buildMessage(int errno,String msg)
  {
    StringBuffer buffer = new StringBuffer();
    
    String errCode = Result.lookupErrCode(errno);
    if (errCode != null)
    {
      buffer.append(errCode);
      buffer.append(" ");
    }
    
    buffer.append("(");
    buffer.append(errno);
    buffer.append(") ");
    
    String errText = Result.lookupErrText(errno);
    if (errText != null)
    {
      buffer.append(errText);
      buffer.append(" ");
    }
    
    if ((msg != null) && (msg.trim().length() > 0))
    {
      buffer.append(msg);
    }
    
    return buffer.toString();
  }

  
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws RegistryException
  {
    System.out.println(new AccountLimitExceededException("Additional error information.")); 
    System.out.println(new AssertionNotFoundException("Additional error information.")); 
    System.out.println(new AuthTokenExpiredException("Additional error information.")); 
    System.out.println(new AuthTokenRequiredException("Additional error information."));     
    System.out.println(new BusyException("Additional error information.")); 
    System.out.println(new CategorizationNotAllowedException("Additional error information.")); 
    System.out.println(new FatalErrorException("Additional error information.")); 
    System.out.println(new InvalidCategoryException("Additional error information.")); 
    System.out.println(new InvalidCompletionStatusException("Additional error information.")); 
    System.out.println(new InvalidKeyPassedException("Additional error information.")); 
    System.out.println(new InvalidProjectionException("Additional error information.")); 
    System.out.println(new InvalidTimeException("Additional error information.")); 
    System.out.println(new InvalidURLPassedException("Additional error information.")); 
    System.out.println(new InvalidValueException("Additional error information.")); 
    System.out.println(new KeyRetiredException("Additional error information.")); 
    System.out.println(new LanguageErrorException("Additional error information.")); 
    System.out.println(new MessageTooLargeException("Additional error information.")); 
    System.out.println(new NameTooLongException("Additional error information.")); 
    System.out.println(new OperatorMismatchException("Additional error information.")); 
    System.out.println(new PublisherCancelledException("Additional error information.")); 
    System.out.println(new RequestDeniedException("Additional error information.")); 
    System.out.println(new RequestTimeoutException("Additional error information.")); 
    System.out.println(new ResultSetTooLargeException("Additional error information.")); 
    System.out.println(new SecretUnknownException("Additional error information.")); 
    System.out.println(new TooManyOptionsException("Additional error information.")); 
    System.out.println(new TransferAbortedException("Additional error information.")); 
    System.out.println(new UnknownUserException("Additional error information.")); 
    System.out.println(new UnknownUserException("Additional error information.")); 
    System.out.println(new UnrecognizedVersionException("Additional error information.")); 
    System.out.println(new UnsupportedException("Additional error information.")); 
    System.out.println(new UnvalidatableException("Additional error information.")); 
    System.out.println(new UserMismatchException("Additional error information.")); 
    System.out.println(new ValueNotAllowedException("Additional error information.")); 
 
    System.out.println("\n----- RegistryException(String) -----");
    
    System.out.println(new RegistryException((String)null));
    System.out.println(new RegistryException("Additional error information."));
    
    System.out.println("\n----- RegistryException(Exception) -----");
    
    System.out.println(new RegistryException((Exception)null));
    System.out.println(new RegistryException((Exception)new Exception("Additional error information.")));
    System.out.println(new RegistryException((Exception)new RegistryException("Additional error information.")));
    System.out.println(new RegistryException((Exception)new UnknownUserException("Additional error information.")));
    
    System.out.println("\n----- RegistryException(FaultCode,errno,String) -----");
    
    System.out.println(new RegistryException(null,-1,null));
    System.out.println(new RegistryException("Server",-1,null));
    System.out.println(new RegistryException("Server",Result.E_BUSY,null));
    System.out.println(new RegistryException("Server",Result.E_BUSY,"Additional error information."));
  }
}