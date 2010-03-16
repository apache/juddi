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


package org.uddi.api_v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.uddi.JAXBContextUtil;
import org.w3c.dom.Node;


/**
 * <p>Java class for dispositionReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dispositionReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}result" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="truncated" type="{urn:uddi-org:api_v3}truncated" />
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
@XmlRootElement
public class DispositionReport implements Serializable {
	@XmlTransient
	private static final long serialVersionUID = 5852663849477002516L;
	@XmlElement(required = true)
    protected List<Result> result;
    @XmlAttribute
    protected Boolean truncated;
    
    public final static transient String E_ACCOUNT_LIMIT_EXCEEDED     = "E_accountLimitExceeded";
	public final static transient String E_ASSERTION_NOT_FOUND        = "E_assertionNotFound"; 
	public final static transient String E_AUTH_TOKEN_EXPIRED         = "E_authTokenExpired";
	public final static transient String E_AUTH_TOKEN_REQUIRED        = "E_authTokenRequired";
	public final static transient String E_BUSY                       = "E_busy";
	public final static transient String E_CATEGORIZATION_NOT_ALLOWED = "E_categorizationNotAllowed";
	public final static transient String E_FATAL_ERROR                = "E_fatalError";
	public final static transient String E_INVALID_COMBINATION        = "E_invalidCombination";
	public final static transient String E_INVALID_CATEGORY           = "E_invalidCategory";
	public final static transient String E_INVALID_COMPLETION_STATUS  = "E_invalidCompletionStatus";
	public final static transient String E_INVALID_KEY_PASSED         = "E_invalidKeyPassed";
	public final static transient String E_KEY_UNAVAILABLE         	  = "E_keyUnavailable";
	public final static transient String E_INVALID_PROJECTION         = "E_invalidProjection";
	public final static transient String E_INVALID_TIME               = "E_invalidTime";
	public final static transient String E_INVALID_URL_PASSED         = "E_invalidURLPassed";
	public final static transient String E_INVALID_VALUE              = "E_invalidValue";
	public final static transient String E_KEY_RETIRED                = "E_keyRetired";
	public final static transient String E_LANGUAGE_ERROR             = "E_languageError";
	public final static transient String E_MESSAGE_TOO_LARGE          = "E_messageTooLarge";
	public final static transient String E_NAME_TOO_LONG              = "E_nameTooLong";
	public final static transient String E_OPERATOR_MISMATCH          = "E_operatorMismatch";
	public final static transient String E_PUBLISHER_CANCELLED        = "E_publisherCancelled";
	public final static transient String E_REQUEST_DENIED             = "E_requestDenied";
	public final static transient String E_REQUEST_TIMEOUT            = "E_requestTimeout";
	public final static transient String E_RESULT_SET_TOO_LARGE       = "E_resultSetTooLarge";
	public final static transient String E_SECRET_UNKNOWN             = "E_secretUnknown";
	public final static transient String E_SUCCESS                    = "E_success";
	public final static transient String E_TOO_MANY_OPTIONS           = "E_tooManyOptions";
	public final static transient String E_TRANSFER_ABORTED           = "E_transferAborted";
	public final static transient String E_UNKNOWN_USER               = "E_unknownUser";
	public final static transient String E_UNRECOGNIZED_VERSION       = "E_unrecognizedVersion";
	public final static transient String E_UNSUPPORTED                = "E_unsupported";
	public final static transient String E_UNVALIDATABLE              = "E_unvalidatable";
	public final static transient String E_USER_MISMATCH              = "E_userMismatch";
	public final static transient String E_VALUE_NOT_ALLOWED          = "E_valueNotAllowed";
	public final static transient String E_TOKEN_ALREADY_EXISTS       = "E_tokenAlreadyExists";
	public final static transient String E_TRANSFER_NOT_ALLOWED       = "E_transferNotAllowed";

    /**
     * 
     */
    public DispositionReport() {
		super();
	}
    /**
     * 
     * @param node
     * @throws JAXBException 
     */
    public DispositionReport(Node node) throws JAXBException  {
		super();
		JAXBContextUtil.getContext(this.getClass().getPackage().getName());
		Unmarshaller u = JAXBContextUtil.getContext(
				this.getClass().getPackage().getName()).createUnmarshaller();
		JAXBElement<DispositionReport> element =  u.unmarshal(node, DispositionReport.class);
		this.result = element.getValue().getResult();
		this.truncated = element.getValue().truncated;
	}

	/**
     * Gets the value of the result property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the result property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Result }
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
     * Gets the value of the truncated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTruncated() {
        return truncated;
    }

    /**
     * Sets the value of the truncated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTruncated(Boolean value) {
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
