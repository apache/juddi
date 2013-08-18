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
using juddi_dotnet.org.apache.juddi.v3.client;
using org.apache.juddi.v3.client.log;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace org.apache.juddi.v3.client
{
    public class UDDIClientContainer
    {
        private static Log log = LogFactory.getLog(typeof(UDDIClientContainer));
        private static Dictionary<String, UDDIClient> clients = new Dictionary<String, UDDIClient>();

        public static UDDIClient getUDDIClient(String clientName)
        {

            if (clientName != null)
            {
                if (clients.ContainsKey(clientName))
                {
                    return (clients[clientName]);
                }
                else
                {
                    throw new ConfigurationException("No client by name " + clientName + " was found. " +
                            " Please check your client uddi.xml files, and make sure this client was started");
                }
            }
            else if (clients.Count == 1 && clientName == null)
            {
                log.warn("Deprecated, please specify a client name");
                Dictionary<string, UDDIClient>.ValueCollection.Enumerator it = clients.Values.GetEnumerator();
                it.MoveNext();
                return it.Current;
                //return clients.Values.GetEnumerator().MoveNext()..iterator().next();
            }
            else
            {
                log.warn("Deprecated, please specify a client name");
                UDDIClient client = new UDDIClient(null);
                addClient(client);
                client.start();
                return client;
            }
        }

        public static bool addClient(UDDIClient manager)
        {
            if (!clients.ContainsKey(manager.getClientConfig().getClientName()))
            {
                clients.Add(manager.getClientConfig().getClientName(), manager);
                return true;
            }
            else
            {
                return false;
            }
        }

        public static void removeClerkManager(String clientName)
        {
            if (clients.ContainsKey(clientName))
            {
                clients.Remove(clientName);
            }
            else if (clients.Count == 1 && clientName == null)
            {
                Dictionary<string, UDDIClient>.KeyCollection.Enumerator it = clients.Keys.GetEnumerator();
                it.MoveNext();
                String name = it.Current;
                log.info("Removing " + name + " from UDDIClient.");
                clients.Remove(name);
            }
            else
            {
                throw new ConfigurationException("Could not remove UDDIClient for name " + clientName);
            }
        }

    }
}
