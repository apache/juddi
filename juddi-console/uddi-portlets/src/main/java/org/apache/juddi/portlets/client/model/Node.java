package org.apache.juddi.portlets.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private String status;
	private String clerkName;
	private List<Subscription> subscriptions = new ArrayList<Subscription>();

	public Node() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClerkName() {
		return clerkName;
	}

	public void setClerkName(String clerkName) {
		this.clerkName = clerkName;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
		
	
}
