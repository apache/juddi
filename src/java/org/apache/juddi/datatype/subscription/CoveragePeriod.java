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
package org.apache.juddi.datatype.subscription;

import java.util.Date;

import org.apache.juddi.datatype.RegistryObject;

/**
 * Example:
 * 
 *   <coveragePeriod>
 *     <startPoint>20020727T00:00:00</startPoint>
 *     <endPoint>20020728T00:00:00</endPoint>
 *   </coveragePeriod>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class CoveragePeriod implements RegistryObject
{
	Date startPoint;
	Date endPoint;
	
  /**
   * default constructor
   */
  public CoveragePeriod()
  {
  }
  
  /**
   * constructor
   */
  public CoveragePeriod(Date startPoint,Date endPoint)
  {
  }
  
	/**
	 * @return Returns the endPoint.
	 */
	public Date getEndPoint() 
	{
		return endPoint;
	}
	
	/**
	 * @param endPoint The endPoint to set.
	 */
	public void setEndPoint(Date endPoint) 
	{
		this.endPoint = endPoint;
	}
	
	/**
	 * @return Returns the startPoint.
	 */
	public Date getStartPoint() 
	{
		return startPoint;
	}
	
	/**
	 * @param startPoint The startPoint to set.
	 */
	public void setStartPoint(Date startPoint) 
	{
		this.startPoint = startPoint;
	}
}