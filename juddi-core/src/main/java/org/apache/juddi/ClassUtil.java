package org.apache.juddi;

public class ClassUtil {
    public static Class forName(String name, Class caller)
    	throws ClassNotFoundException
    {
	    ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
	    if (threadClassLoader != null) {
	        try {
	            return Class.forName(name, true, threadClassLoader) ;
	        } catch (ClassNotFoundException cnfe) {
	        	if (cnfe.getException() != null) {
	        		throw cnfe;
	        	}
	        }
	    }
    
	    ClassLoader callerClassLoader = caller.getClassLoader();
	    if (callerClassLoader != null) {
	        try {
	            return Class.forName(name, true, callerClassLoader) ;
	        } catch (final ClassNotFoundException cnfe) {
	            if (cnfe.getException() != null) {
	                throw cnfe ;
	            }
	        }
	    }
	    
	    return Class.forName(name, true, ClassLoader.getSystemClassLoader()) ;
    }
}
