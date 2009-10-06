/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.portlets.client.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.juddi.portlets.client.model.Node;
import org.apache.juddi.portlets.client.model.Subscription;

/**
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 *
 */
public class SubscriptionResponse extends Response {
	private static final long serialVersionUID = -1729549521486172296L;
	private List<Node> nodes = new ArrayList<Node>();
	private Subscription subscription;

	public List<Node> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

}
