/*
 * Copyright 2012 The Apache Software Foundation.
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
package org.apache.juddi.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "j3_signature")
public class Signature implements java.io.Serializable {
    private static final long serialVersionUID = -3233157941119408718L;
    
    private Long id;
    private SignedInfo signedInfo;
    private SignatureValue signatureValue;
    private KeyInfo keyInfo;
    private List<ObjectType> object;
    private BusinessEntity businessEntity;
    private BusinessService businessService;
    private Publisher publisher;
    private BindingTemplate bindingTemplate;
    private Tmodel tmodel;
    private String xmlID;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "binding_template_key", nullable = true)
    public BindingTemplate getBindingTemplate() {
        return bindingTemplate;
    }

    public void setBindingTemplate(BindingTemplate bindingTemplate) {
        this.bindingTemplate = bindingTemplate;
    }

    @ManyToOne
    @JoinColumn(name = "tmodel_key", nullable = true)
    public Tmodel getTmodel() {
        return tmodel;
    }

    public void setTmodel(Tmodel tmodel) {
        this.tmodel = tmodel;
    }
    
    @ManyToOne
    @JoinColumn(name = "publisher_key", nullable = true)
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    
    @ManyToOne
    @JoinColumn(name = "business_service_key", nullable = true)
    public BusinessService getBusinessService() {
        return businessService;
    }

    public void setBusinessService(BusinessService businessService) {
        this.businessService = businessService;
    }
    
    @ManyToOne
    @JoinColumn(name = "business_key", nullable = true)
    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "signed_info", nullable = false)
    public SignedInfo getSignedInfo() {
        return signedInfo;
    }

    public void setSignedInfo(SignedInfo signedInfo) {
        this.signedInfo = signedInfo;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "signature_value", nullable = false)
    public SignatureValue getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(SignatureValue signatureValue) {
        this.signatureValue = signatureValue;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "key_info", nullable = false)
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "signature")
    @OrderBy
    public List<ObjectType> getObject() {
        return object;
    }

    public void setObject(List<ObjectType> object) {
        this.object = object;
    }

    @Column(name="xml_id")
    public String getXmlID() {
        return xmlID;
    }

    public void setXmlID(String xmlID) {
        this.xmlID = xmlID;
    }
}
