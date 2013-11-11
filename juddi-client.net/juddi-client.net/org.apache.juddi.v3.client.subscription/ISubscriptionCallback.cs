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
