/*
 * Copyright 2013 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.migration.tool;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Some common functions for the migration tool
 *
 * @author Alex O'Ree
 * @since 3.2
 */
public class Common {

    public static String GetOwner(String key, String token, UDDIInquiryPortType inquiry) {
        GetOperationalInfo goi = new GetOperationalInfo();
        goi.setAuthInfo(token);
        goi.getEntityKey().add(key);
        OperationalInfos operationalInfo = null;
        try {
            operationalInfo = inquiry.getOperationalInfo(goi);
            if (operationalInfo!=null && operationalInfo.getOperationalInfo()!=null &&
                    !operationalInfo.getOperationalInfo().isEmpty())
            return operationalInfo.getOperationalInfo().get(0).getAuthorizedName();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String GetAuthToken(String username, String password, UDDISecurityPortType sec) {
        try {
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID(username);
            getAuthTokenRoot.setCred(password);
            return sec.getAuthToken(getAuthTokenRoot).getAuthInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
