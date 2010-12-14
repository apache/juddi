package org.apache.juddi.config;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * From the 
 * 
 * @author kstam
 *
 */
public class PublisherFileFilter implements FileFilter {

	private Log log = LogFactory.getLog(this.getClass());
	
	public boolean accept(File file) {
		log.debug("file=" + file);
		if (file.getName().endsWith(Install.FILE_PUBLISHER)) {
			return true;
		}
		return false;
	}

}
