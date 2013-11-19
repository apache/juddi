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

using org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.config;
using org.apache.juddi.v3.client.subscription;
using org.apache.juddi.v3.client.transport;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace org.apache.juddi.client.sample
{
    class SubscriptionCallbackExample : ISubscriptionCallback
    {
        public void HandleCallback(uddi.apiv3.subscriptionResultsList body)
        {
            Console.Out.WriteLine("HandleCallback received " + body.Item);
        }

        public void NotifyEndpointStopped()
        {
            Console.Out.WriteLine("NotifyEndpointStopped, Callback endpoint stopped");
        }

        public static void Run(string[] args)
        {

            UDDIClient clerkManager = null;
            Transport transport = null;
            UDDIClerk clerk = null;
            try
            {
                clerkManager = new UDDIClient("uddi.xml");
                UDDIClientContainer.addClient(clerkManager);

                transport = clerkManager.getTransport("default_non_root");

                UDDI_Security_SoapBinding security = transport.getUDDISecurityService();
                UDDI_Inquiry_SoapBinding inquiry = transport.getUDDIInquiryService();
                UDDI_Publication_SoapBinding publish = transport.getUDDIPublishService();

                clerk = clerkManager.getClerk("default_non_root");


                //this part just confirms that the keydomain exists for our new service and binding template
                String keydomain = null;
                if (clerkManager.getClientConfig().getConfiguration() != null &&
                     clerkManager.getClientConfig().getConfiguration().client != null &&
                     clerkManager.getClientConfig().getConfiguration().client.subscriptionCallbacks != null &&
                     clerkManager.getClientConfig().getConfiguration().client.subscriptionCallbacks.keyDomain != null)
                    keydomain = clerkManager.getClientConfig().getConfiguration().client.subscriptionCallbacks.keyDomain;
                else
                    keydomain = "uddi:org.apache.demos";
                tModel createKeyGenator = UDDIClerk.createKeyGenator(keydomain + ":keygenerator", "Test domain", "en");
                clerk.register(createKeyGenator);

                //we'll also create a business and a service here so that we can hook in our callback endpoint
                businessEntity be = new businessEntity();
                
                be.businessKey = keydomain + ":coolbizexample";
                be.name = new name[] { new name("Coolbiz Inc", "en") };
                be.businessServices = new businessService[] { new businessService() };
                be.businessServices[0].businessKey = keydomain + ":coolbizexample";
                be.businessServices[0].serviceKey = keydomain + ":coolbizexample-service";
                be.businessServices[0].name = new name[] { new name("Coolbiz Service", "en") };
                clerk.register(be);


                bindingTemplate start = SubscriptionCallbackListener.start(clerkManager, "default_non_root");
                Console.WriteLine("Started and waiting for inbound traffic");
                //bkeep alive
                DateTime stop = DateTime.Now.Add(new TimeSpan(0, 0, 10));
                while (DateTime.Now < stop)
                {
                    Thread.Sleep(1000);
                }
                Console.WriteLine("Stopping");
                SubscriptionCallbackListener.stop(clerkManager, "default_non_root", start.bindingKey);

                //get rid of it once we're done
                clerk.unRegisterBusiness(keydomain + ":coolbizexample");
                clerk.unRegisterTModel(createKeyGenator.tModelKey);
            }
            catch (Exception ex)
            {
                while (ex != null)
                {
                    System.Console.WriteLine("Error! " + ex.Message);
                    ex = ex.InnerException;
                }
            }
            finally
            {
                if (transport != null && transport is IDisposable)
                {
                    ((IDisposable)transport).Dispose();
                }
                if (clerk != null)
                    clerk.Dispose();
            }
        }
    }
}
