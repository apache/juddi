package org.apache.juddi.portlets.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Business implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String key;
	private String name;
	private String description;
	private List<Service> services = new ArrayList<Service>();
	public Business() {}
		
	public Business(String key, String name, String description) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
	}

	

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
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
}
