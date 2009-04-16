package org.apache.juddi.portlets.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Service implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String key;
	private String description;
	private Collection<ServiceBinding> serviceBindings = new ArrayList<ServiceBinding>();

	public Service() {}
		
	public Service(String name, String key) {
		super();
		this.name = name;
		this.key = key;
	}
	
	public Service(String name, String key, String description) {
		super();
		this.name = name;
		this.key = key;
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
