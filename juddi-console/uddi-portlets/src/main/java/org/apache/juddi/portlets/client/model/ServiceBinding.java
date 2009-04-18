package org.apache.juddi.portlets.client.model;

import java.io.Serializable;

public class ServiceBinding implements Serializable{

	private static final long serialVersionUID = 1L;
    private String key;
	private String accessPoint;
	private String description;
	private String urlType;

	public ServiceBinding() {}
		
	public ServiceBinding(String key, String accessPoint, String description, String urlType) {
		super();
		this.key = key;
		this.accessPoint = accessPoint;
		this.description = description;
		this.urlType = urlType;
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

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
