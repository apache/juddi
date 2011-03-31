package org.apache.juddi.api.impl;

public interface UDDIServiceCounterMBean {
    public int getTotalQueryCount();
    public int getSuccessfulQueryCount();
    public int getFaultQueryCount();    
    public Double getProcessingTime();
            
    public void resetCounts();
}
