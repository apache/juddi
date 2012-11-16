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

import java.util.ArrayList;
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
@Table(name="j3_signed_info")
public class SignedInfo implements java.io.Serializable {
    private static final long serialVersionUID = -2233157941119408719L;
    
    private Long id;
    private CanonicalizationMethod canonicalizationMethod;
    private SignatureMethod signatureMethod;
    private List<Reference> reference = new ArrayList<Reference>(0);
    private String xmlID;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "canonicalization_method", nullable = false)
    public CanonicalizationMethod getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    public void setCanonicalizationMethod(CanonicalizationMethod canonicalizationMethod) {
        this.canonicalizationMethod = canonicalizationMethod;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "signature_method", nullable = false)
    public SignatureMethod getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(SignatureMethod signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "signedInfo")
    @OrderBy
    public List<Reference> getReference() {
        return reference;
    }

    public void setReference(List<Reference> reference) {
        this.reference = reference;
    }

    @Column(name="xml_id")
    public String getXmlID() {
        return xmlID;
    }

    public void setXmlID(String xmlID) {
        this.xmlID = xmlID;
    }
}
