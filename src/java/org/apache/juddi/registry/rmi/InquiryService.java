package org.apache.juddi.registry.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InquiryService extends UnicastRemoteObject
	implements Inquiry
{
	private static final long serialVersionUID = 1L;

	public InquiryService() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Node inquire(Element request) throws RemoteException {
		Node response = null;
		org.apache.juddi.registry.local.InquiryService inquiryService =
			new org.apache.juddi.registry.local.InquiryService();
		try {
			response = inquiryService.inquire(request);
		} catch (Exception e) {
			throw new RemoteException(e.getLocalizedMessage(), e);
		}
		return response;
	}
	


}
