package org.uddi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class JAXBContextUtil {

	private static Log log = LogFactory.getLog(JAXBContextUtil.class);
	private static final Map<String, JAXBContext> JAXBContexts = new HashMap<String, JAXBContext>();

	public static JAXBContext getContext(String packageName) throws JAXBException {
		if (!JAXBContexts.containsKey(packageName)) {
			log.info("Creating JAXB Context for " + packageName);
			JAXBContexts.put(packageName, JAXBContext.newInstance(packageName));
		}
		return JAXBContexts.get(packageName);
	}
	
}
