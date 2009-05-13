package org.apache.juddi.subscription;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class TypeConvertor {
	public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) throws DispositionReportFaultMessage {
		XMLGregorianCalendar result = null;
		try { 
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(date.getTime());
			
			DatatypeFactory df = DatatypeFactory.newInstance();
			result = df.newXMLGregorianCalendar(gc);
		}
		catch(DatatypeConfigurationException ce) { 
			throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));
		}
		
		return result;
	}
	
	public static Duration convertStringToDuration(String duration) throws DispositionReportFaultMessage {
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
