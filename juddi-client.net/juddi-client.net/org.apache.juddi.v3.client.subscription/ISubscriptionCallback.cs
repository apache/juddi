/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.subscription
{
    /// <summary>
    /// This is an interface for creating asynchronous callback clientsfor the
    /// UDDI Subscription API.
    /// @since 3.2
    /// @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
    /// @see SubscriptionCallbackListener
    /// </summary>
    public interface ISubscriptionCallback
    {
        /// <summary>
        /// Called when a UDDI server notifies us that something has changed. 
     /// Implementations should never block.
        /// </summary>
        /// <param name="body"></param>
         void HandleCallback(subscriptionResultsList body);

        
        /// <summary>
        /// Called when the callback endpoint is stopped
        /// </summary>
         void NotifyEndpointStopped();
    }
}
