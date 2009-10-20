/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.apache.juddi.subscription;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.uddi.v3_service.DispositionReportFaultMessage;
/**
 * 
 */
public class TypeConvertor {
	public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) throws DispositionReportFaultMessage {
		XMLGregorianCalendar result = null;
		try { 
			if (date!=null) {
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(date.getTime());
				
				DatatypeFactory df = DatatypeFactory.newInstance();
				result = df.newXMLGregorianCalendar(gc);
			}
		}
		catch(DatatypeConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}
		
		return result;
	}
	
	public static Duration convertStringToDuration(String duration) throws DispositionReportFaultMessage {
		if (duration==null) return null;
		Duration result = null;
		try { 
			
			DatatypeFactory df = DatatypeFactory.newInstance();
			result = df.newDuration(duration);
		}
		catch(DatatypeConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}

		return result;
	}
}
