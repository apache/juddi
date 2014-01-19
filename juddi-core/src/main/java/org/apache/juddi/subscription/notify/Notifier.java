package org.apache.juddi.subscription.notify;

import java.rmi.RemoteException;

import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This is an interface class used when the jUDDI server notifies subscription listeners
 * asynchronously.
 * 
 * You must implement a constructor that accepts a single parameter of type
 * "org.apache.juddi.model.BindingTemplate".
 * 
 * In addition, implementing classes must be within the same package name as this class
 * and must be named after the following pattern:<br>
 * PROTOCOLNotifier<br>
 * Example: When the binding Template's access point is http://myserver:9999/endpoint, 
 * then the class HTTPNotifier is called. Endpoints must start with protocol: in 
 * order for this to work and they will be made uppercase when searching for the 
 * class to trigger.
 * 
 */
public interface Notifier {

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) 
		throws DispositionReportFaultMessage, RemoteException;
	
}
