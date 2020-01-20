/*
 * Copyright 2013 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.client.subscription;

import org.uddi.sub_v3.SubscriptionResultsList;

/**
 * This is an interface for creating asynchronous callback clients for the
 * UDDI Subscription API.
 * 
 * Change notice, since 3.3.5, methods were renamed for the most standard convention (lowercase)
 * @since 3.2
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 * @see SubscriptionCallbackListener
 */
public interface ISubscriptionCallback {

    /**
     * Called when a UDDI server notifies us that something has changed. 
     * Implementations should never block.
     * @param body 
     */
    public void handleCallback(SubscriptionResultsList body);

    /**
     * Called when the callback endpoint is stopped
     */
    public void notifyEndpointStopped();
}
