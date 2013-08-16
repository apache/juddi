using org.apache.juddi.v3.client.log;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.v3.client.config
{
    class BackGroundRegistration
    {
        	private UDDIClient uddiClient = null;
	private static Log log = LogFactory.getLog(typeof(BackGroundRegistration));
	
	public BackGroundRegistration(UDDIClient manager) {
		//super();
		this.uddiClient = manager;
	}

	public void run() {
		try {
			if (UDDIClientContainer.getUDDIClient(uddiClient.getName())!=null && uddiClient.getClientConfig().isRegisterOnStartup()) {
				log.debug("Starting UDDI Clerks for uddiClient " + uddiClient.getClientConfig().getClientName() + "...");
				uddiClient.saveClerkAndNodeInfo();
				uddiClient.registerAnnotatedServices();
				uddiClient.registerWSDLs();
				uddiClient.xRegister();
				log.debug("Clerks started succesfully for uddiClient " + uddiClient.getClientConfig().getClientName());
			} else {
				log.debug(uddiClient.getName() + " already registered to the UDDIClientContainer.");
			}
		} catch (Exception e) {
			log.error(e.Message,e);
		}
	}
    }
}
