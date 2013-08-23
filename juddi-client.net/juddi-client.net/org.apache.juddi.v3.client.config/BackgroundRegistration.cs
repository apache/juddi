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
using org.apache.juddi.v3.client.log;
using System;

namespace org.apache.juddi.v3.client.config
{
    class BackGroundRegistration
    {
        private UDDIClient uddiClient = null;
        private static Log log = LogFactory.getLog(typeof(BackGroundRegistration));

        public BackGroundRegistration(UDDIClient manager)
        {
            //super();
            this.uddiClient = manager;
        }

        public void run()
        {
            try
            {
                if (UDDIClientContainer.getUDDIClient(uddiClient.getName()) != null && uddiClient.getClientConfig().isRegisterOnStartup())
                {
                    log.debug("Starting UDDI Clerks for uddiClient " + uddiClient.getClientConfig().getClientName() + "...");
                    uddiClient.saveClerkAndNodeInfo();
                    uddiClient.registerAnnotatedServices();
                    uddiClient.registerWSDLs();
                    uddiClient.xRegister();
                    log.debug("Clerks started succesfully for uddiClient " + uddiClient.getClientConfig().getClientName());
                }
                else
                {
                    log.debug(uddiClient.getName() + " already registered to the UDDIClientContainer.");
                }
            }
            catch (Exception e)
            {
                log.error(e.Message, e);
            }
        }
    }
}
