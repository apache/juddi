package org.apache.juddi.portlets.client.model;

import java.io.Serializable;

public class ServiceBinding implements Serializable{

	private static final long serialVersionUID = 1L;

	private String accessPoint;
	private String description;

	public ServiceBinding() {}
		
	public ServiceBinding(String accessPoint, String description) {
		super();
		this.accessPoint = accessPoint;
		this.description = description;
	}

	public String getAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(String accessPoint) {
		this.accessPoint = accessPoint;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
