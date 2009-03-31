package org.apache.juddi.portlets.client.model;

import java.io.Serializable;

public class Service implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;

	public Service() {}
		
	public Service(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
