package org.apache.juddi.registry.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PublishService extends UnicastRemoteObject
	implements Publish
{

	private static final long serialVersionUID = 1L;

	public PublishService() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public Node publish(Element request) throws RemoteException {
		Node response = null;
		org.apache.juddi.registry.local.PublishService publishService = 
			new org.apache.juddi.registry.local.PublishService();
		try {
			response = publishService.publish(request);
		} catch (Exception e) {
			throw new RemoteException(e.getLocalizedMessage(), e);
		}
		return response;
	}
	


}
