package org.apache.juddi.v3.client.embed;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.ClassUtil;

public class JUDDIRegistry implements EmbeddedRegistry {

	private Log logger = LogFactory.getLog(this.getClass());
	public void start() {
		try {
			Class<?> juddiRegistry = ClassUtil.forName("org.apache.juddi.Registry", JUDDIRegistry.class);
			Object jUDDI =  juddiRegistry.newInstance();
			Method startMethod = juddiRegistry.getDeclaredMethod("start");
			startMethod.invoke(jUDDI);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	public void stop() {
		try {
			Class<?> juddiRegistry = ClassUtil.forName("org.apache.juddi.Registry", JUDDIRegistry.class);
			Object jUDDI =  juddiRegistry.newInstance();
			Method startMethod = juddiRegistry.getDeclaredMethod("stop");
			startMethod.invoke(jUDDI);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}
