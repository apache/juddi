/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */
package org.uddi.api_v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.uddi.JAXBContextUtil;
import org.w3c.dom.Node;

/**
 * <p>
 * Java class for dispositionReport complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="dispositionReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v2}result" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="generic" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="operator" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="truncated" type="{urn:uddi-org:api_v2}truncated" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dispositionReport", propOrder = {
     "result"
})
public class DispositionReport {

     

     @XmlElement(required = true)
     protected List<Result> result;
     @XmlAttribute(name = "generic", required = true)
     protected String generic;
     @XmlAttribute(name = "operator", required = true)
     protected String operator;
     @XmlAttribute(name = "truncated")
     protected Truncated truncated;

     /**
      * (30000) Signifies that a particular publisher assertion (consisting of
      * two businessKey values, and a keyed reference with three components)
      * cannot be identified in a save or delete operation.
      */
     public final static transient String E_ASSERTION_NOT_FOUND = "E_assertionNotFound";
     /**
      * E_authTokenExpired: (10110) Signifies that the authentication token
      * information has timed out.
      */
     public final static transient String E_AUTH_TOKEN_EXPIRED = "E_authTokenExpired";
     /**
      * E_authTokenRequired: (10120) Signifies that an invalid authentication
      * token was passed to an API call that requires authentication.
      */
     public final static transient String E_AUTH_TOKEN_REQUIRED = "E_authTokenRequired";
     /**
      * E_accountLimitExceeded: (10160) Signifies that a save request exceeded
      * the quantity limits for a given data type. See "Structure Limits" in
      * Appendix D for details.
      */
     public final static transient String E_ACCOUNT_LIMIT_EXCEEDED = "E_accountLimitExceeded";
     /**
      * E_busy: (10400) Signifies that the request cannot be processed at the
      * current time.
      */
     public final static transient String E_BUSY = "E_busy";
     /**
      * E_categorizationNotAllowed: (20100) RETIRED. Used for UDDI Version 1.0
      * compatibility. Replaced by E_valueNotAllowed in 2 and higher.
      * Restrictions have been placed on the types of information that can be
      * categorized within a specific taxonomy. The data provided does not
      * conform to the restrictions placed on the category used. Used with
      * categorization only.
      */
     public final static transient String E_CATEGORIZATION_NOT_ALLOWED = "E_categorizationNotAllowed";
     /**
      * E_fatalError: (10500) Signifies that a serious technical error has
      * occurred while processing the request.
      */
     public final static transient String E_FATAL_ERROR = "E_fatalError";
     /**
      * E_invalidCategory (20000): RETIRED. Used for UDDI Version 1.0
      * compatibility only. Replaced by E_invalidValue in version 2 and higher.
      * Signifies that the given keyValue did not correspond to a category
      * within the taxonomy identified by the tModelKey. Used with
      * categorization only.
      */
     @Deprecated
     public final static transient String E_INVALID_CATEGORY = "E_invalidCategory";
     /**
      * E_invalidCompletionStatus: (30100) signifies that one of the assertion
      * status values passed is unrecognized. The completion status that caused
      * the problem will be clearly indicated in the error text.
      */
     public final static transient String E_INVALID_COMPLETION_STATUS = "E_invalidCompletionStatus";
     /**
      * E_invalidKeyPassed: (10210) Signifies that the uuid_key value passed did
      * not match with any known key values. The details on the invalid key will
      * be included in the dispositionReport element.
      */
     public final static transient String E_INVALID_KEY_PASSED = "E_invalidKeyPassed";
     /**
      * E_invalidProjection: (20230) Signifies that an attempt was made to save
      * a businessEntity containing a service projection that does not match the
      * businessService being projected. The serviceKey of at least one such
      * businessService will be included in the dispositionReport.
      */
     public final static transient String E_INVALID_PROJECTION = "E_invalidProjection";
     /**
      * E_invalidURLPassed: (10220) DO NOT USE. Signifies that an error occurred
      * during processing of a save function involving accessing data from a
      * remote URL. The details of the HTTP Get report will be included in the
      * dispositionReport element. Not used in V1 or V2.
      */
     public final static transient String E_INVALID_URL_PASSED = "E_invalidURLPassed";
     /**
      * E_invalidValue: (20200) A value that was passed in a keyValue attribute
      * did not pass validation. This applies to checked categorizations,
      * identifiers and other validated code lists. The error text will clearly
      * indicate the key and value combination that failed validation.
      */
     public final static transient String E_INVALID_VALUE = "E_invalidValue";
     /**
      * E_keyRetired: (10310) DO NOT USE. Signifies that a uuid_key value passed
      * has been removed from the registry. While the key was once valid as an
      * accessor, and is still possibly valid, the publisher has removed the
      * information referenced by the uuid_key passed. V1 errata – not used.
      * Included here for historical code-set completion.
      */
     public final static transient String E_KEY_RETIRED = "E_keyRetired";
     /**
      * E_languageError: (10060) Signifies that an error was detected while
      * processing elements that were annotated with xml:lang qualifiers.
      * Presently, only the description and name elements support xml:lang
      * qualifications.
      */
     public final static transient String E_LANGUAGE_ERROR = "E_languageError";
     /**
      * E_messageTooLarge: (30110) Signifies that the message is too large. The
      * upper limit will be clearly indicated in the error text.
      */
     public final static transient String E_MESSAGE_TOO_LARGE = "E_messageTooLarge";
     /**
      * E_nameTooLong: (10020) RETIRED. Used for UDDI Version 1.0 compatibility
      * only. Signifies that the partial name value passed exceeds the maximum
      * name length designated by the policy of an implementation or Operator
      * Site.
      */
     public final static transient String E_NAME_TOO_LONG = "E_nameTooLong";
     /**
      * E_operatorMismatch: (10130) DO NOT USE. Signifies that an attempt was
      * made to use the publishing API to change data that is mastered at
      * another Operator Site. This error is only relevant to the public
      * Operator Sites and does not apply to other UDDI compatible registries.
      * V1 defined this in error – caused precedence problems with
      * E_unknownUser. Included here for historical code set completeness.
      * Retired.
      */
     public final static transient String E_OPERATOR_MISMATCH = "E_operatorMismatch";
     /**
      * E_publisherCancelled: (30220) The target publisher cancelled the custody
      * transfer operation.
      */
     public final static transient String E_PUBLISHER_CANCELLED = "E_publisherCancelled";
     /**
      * E_requestDenied: (30210) A custody transfer request has been refused.
      */
     public final static transient String E_REQUEST_DENIED = "E_requestDenied";
     /**
      * E_requestTimeout: (20240) Signifies that the request could not be
      * carried out because a needed web service, such as validate_values, did
      * not respond in a reasonable amount of time. Details identifying the
      * failing service will be included in the dispositionReport element.
      */
     public final static transient String E_REQUEST_TIMEOUT = "E_requestTimeout";
     /**
      * E_secretUnknown: (30230) The target publisher was unable to match the
      * shared secret and the five (5) attempt limit was exhausted. The target
      * operator automatically cancelled the transfer operation.
      */
     public final static transient String E_SECRET_UNKNOWN = "E_secretUnknown";
     /**
      * E_success: (0) Signifies no failure occurred. This return code is used
      * with the dispositionReport for reporting results from requests with no
      * natural response document.
      */
     public final static transient String E_SUCCESS = "E_success";
     /**
      * E_tooManyOptions: (10030) Signifies that too many or incompatible
      * arguments were passed. The error text will clearly indicate the nature
      * of the problem.
      */
     public final static transient String E_TOO_MANY_OPTIONS = "E_tooManyOptions";
     /**
      * E_transferAborted: (30200) Signifies that a custody transfer request
      * will not succeed.
      */
     public final static transient String E_TRANSFER_ABORTED = "E_transferAborted";
     /**
      * E_unknownUser: (10150) Signifies that the user ID and password pair
      * passed in a get_authToken message is not known to the Operator Site or
      * is not valid.
      */
     public final static transient String E_UNKNOWN_USER = "E_unknownUser";
     /**
      * E_unrecognizedVersion: (10040) Signifies that the value of the generic
      * attribute passed is unsupported by the Operator Instance being queried.
      */
     public final static transient String E_UNRECOGNIZED_VERSION = "E_unrecognizedVersion";
     /**
      * E_unsupported: (10050) Signifies that the implementer does not support a
      * feature or API.
      */
     public final static transient String E_UNSUPPORTED = "E_unsupported";
     /**
      * E_unvalidatable: (20220) Signifies that an attempt was made to reference
      * a taxonomy or identifier system in a keyedReference whose tModel is
      * categorized with the unvalidatable categorization.
      */
     public final static transient String E_UNVALIDATABLE = "E_unvalidatable";
     /**
      * E_userMismatch: (10140) Signifies that an attempt was made to use the
      * publishing API to change data that is controlled by another party.
      */
     public final static transient String E_USER_MISMATCH = "E_userMismatch";
     /**
      * E_valueNotAllowed: (20210) Signifies that a value did not pass
      * validation because of contextual issues. The value may be valid in some
      * contexts, but not in the context used. The error text may contain
      * information about the contextual problem.
      */
     public final static transient String E_VALUE_NOT_ALLOWED = "E_valueNotAllowed";

     public DispositionReport(Node firstChild) throws JAXBException{
          super();
		JAXBContextUtil.getContext(this.getClass().getPackage().getName());
		Unmarshaller u = JAXBContextUtil.getContext(
				this.getClass().getPackage().getName()).createUnmarshaller();
		JAXBElement<DispositionReport> element =  u.unmarshal(firstChild, DispositionReport.class);
		this.result = element.getValue().getResult();
		this.truncated = element.getValue().truncated;
     }

     public DispositionReport() {
          throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     /**
      * Gets the value of the result property.
      *
      * <p>
      * This accessor method returns a reference to the live list, not a
      * snapshot. Therefore any modification you make to the returned list will
      * be present inside the JAXB object. This is why there is not a
      * <CODE>set</CODE> method for the result property.
      *
      * <p>
      * For example, to add a new item, do as follows:
      * <pre>
      *    getResult().add(newItem);
      * </pre>
      *
      *
      * <p>
      * Objects of the following type(s) are allowed in the list {@link Result }
      *
      *
      */
     public List<Result> getResult() {
          if (result == null) {
               result = new ArrayList<Result>();
          }
          return this.result;
     }

     /**
      * Gets the value of the generic property.
      *
      * @return possible object is {@link String }
      *
      */
     public String getGeneric() {
          return generic;
     }

     /**
      * Sets the value of the generic property.
      *
      * @param value allowed object is {@link String }
      *
      */
     public void setGeneric(String value) {
          this.generic = value;
     }

     /**
      * Gets the value of the operator property.
      *
      * @return possible object is {@link String }
      *
      */
     public String getOperator() {
          return operator;
     }

     /**
      * Sets the value of the operator property.
      *
      * @param value allowed object is {@link String }
      *
      */
     public void setOperator(String value) {
          this.operator = value;
     }

     /**
      * Gets the value of the truncated property.
      *
      * @return possible object is {@link Truncated }
      *
      */
     public Truncated getTruncated() {
          return truncated;
     }

     /**
      * Sets the value of the truncated property.
      *
      * @param value allowed object is {@link Truncated }
      *
      */
     public void setTruncated(Truncated value) {
          this.truncated = value;
     }

     /**
     * Determines if one of the Results in the this DispositionReport has a Error Code
     * that matches the errCodeKey passed in. The errCodeKey should be one
     * of the 
     * 
     * @param errCodeKey
     * @return true if the errCodeKey matches with a code in the Results.
     */
    public boolean countainsErrorCode(final String errCodeKey) {
    	boolean isKeyMatch = false;
		for (Result result : getResult()) {
			String errCode = result.getErrInfo().getErrCode();
			if (errCodeKey.equals(errCode)) {
				isKeyMatch = true;
				break;
			}
		}
    	return isKeyMatch;
    }
    
}
