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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;

/**
 * Used in Result in response DispositionReport.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ErrInfo implements RegistryObject
{
  String errCode;
  String errMsg;

  /**
   *
   */
  public ErrInfo()
  {
  }

  /**
   *
   */
  public ErrInfo(String code)
  {
    setErrCode(code);
  }

  /**
   *
   */
  public ErrInfo(String code,String msg)
  {
    setErrCode(code);
    setErrMsg(msg);
  }

  /**
   * Sets the exception code of this ErrInfo to the given value.
   * @param code The new code number for this ErrInfo.
   */
  public void setErrCode(String code)
  {
    this.errCode = code;
  }

  /**
   * Returns the exception code of this ErrInfo.
   * @return The exception code of this ErrInfo.
   */
  public String getErrCode()
  {
    return this.errCode;
  }

  /**
   * Sets the exception message of this ErrInfo to the given value.
   * @param msg The new exception message for this ErrInfo.
   */
  public void setErrMsg(String msg)
  {
    this.errMsg = msg;
  }

  /**
   * Returns the exception message of this ErrInfo.
   * @return The exception message of this ErrInfo.
   */
  public String getErrMsg()
  {
    return this.errMsg;
  }
}
