package org.apache.juddi.registry.rmi;

import java.rmi.Remote;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface Publish extends Remote
{
	Node publish (Element request) throws java.rmi.RemoteException;
}
