package org.apache.juddi.portlets.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Service implements Serializable{
	private static final long serialVersionUID = -4116192376614022055L;
	private String key;
	private String name;
	private String description;
	private Collection<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();

	public Service() {}
		
	public Service(String key, String name) {
		super();
		this.key = key;
		this.name = name;
	}
	
	public Service(String key, String name, String description) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ServiceBinding> getServiceBindings() {
		return serviceBindings;
	}

	public void setServiceBindings(Collection<ServiceBinding> serviceBindings) {
		this.serviceBindings = serviceBindings;
	}
}
