package org.apache.juddi.v3.client.mapping;

import java.util.Set;

public interface UDDIServiceCacheMBean {

	public int getServiceCacheSize();
	public Set<String> getCacheEntries();
	public void resetCache();
}
