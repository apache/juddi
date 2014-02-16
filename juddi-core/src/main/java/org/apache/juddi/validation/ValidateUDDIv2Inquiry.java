/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.validation;

import org.apache.juddi.api.impl.UDDIv2InquiryImpl;
import org.apache.juddi.v3.client.mapping.MapUDDIv3Tov2;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.UnsupportedException;
import org.uddi.api_v2.FindBinding;
import org.uddi.api_v2.FindBusiness;
import org.uddi.api_v2.FindRelatedBusinesses;
import org.uddi.api_v2.FindService;
import org.uddi.api_v2.FindTModel;
import org.uddi.api_v2.GetBindingDetail;
import org.uddi.api_v2.GetBusinessDetail;
import org.uddi.api_v2.GetBusinessDetailExt;
import org.uddi.api_v2.GetServiceDetail;
import org.uddi.api_v2.GetTModelDetail;
import org.uddi.v2_service.DispositionReport;

/**
 *
 * @author Alex O'Ree
 */
public class ValidateUDDIv2Inquiry {

        public static final String VER = "2.0";

        public static void validateFindBinding(FindBinding body) throws DispositionReport {
                validateVersion(body.getGeneric());

        }

        public static void validateFindBusiness(FindBusiness body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateFindRelatedBusinesses(FindRelatedBusinesses body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateFindService(FindService body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateFindTModel(FindTModel body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateGetBindingDetail(GetBindingDetail body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateGetBusinessDetail(GetBusinessDetail body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateBusinessDetailExt(GetBusinessDetailExt body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateGetServiceDetail(GetServiceDetail body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        public static void validateGetTModelDetail(GetTModelDetail body) throws DispositionReport {
                validateVersion(body.getGeneric());
        }

        private static void validateVersion(String generic) throws DispositionReport {
                if (!VER.equalsIgnoreCase(generic)) {
                        throw MapUDDIv3Tov2.MapException(new UnsupportedException(new ErrorMessage("E_unrecognizedVersion", generic)), UDDIv2InquiryImpl.getNodeID());
                }
        }

}
