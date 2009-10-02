package org.apache.juddi.config;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;
/**
 * From the 
 * 
 * @author kstam
 *
 */
public class PublisherFileFilter implements FileFilter {

	private Logger log = Logger.getLogger(this.getClass());
	
	public boolean accept(File file) {
		log.info("file=" + file);
		if (file.getName().endsWith(Install.FILE_PUBLISHER)) {
			return true;
		}
		return false;
	}

}
