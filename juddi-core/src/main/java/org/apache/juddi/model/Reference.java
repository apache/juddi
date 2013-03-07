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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name="j3_reference")
public class Reference {
    private static final long serialVersionUID = -3233157941119408701L;
    
    private Long id;
    private SignedInfo signedInfo;
    private List<SignatureTransform> transforms = new ArrayList<SignatureTransform>(0);
    private String digestMethod;
    private byte[] digestValue;
    private String xmlID;
    private String uri;
    private String type;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_info_key", nullable = false)
    public SignedInfo getSignedInfo() {
        return signedInfo;
    }

    public void setSignedInfo(SignedInfo signedInfo) {
        this.signedInfo = signedInfo;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "reference")
    @OrderBy
    public List<SignatureTransform> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<SignatureTransform> transforms) {
        this.transforms = transforms;
    }

    @Column(name="digest_method")
    public String getDigestMethod() {
        return digestMethod;
    }

    public void setDigestMethod(String digestMethod) {
        this.digestMethod = digestMethod;
    }

    @Lob
    @Column(name="digest_value", length = 65636)
    public byte[] getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(byte[] digestValue) {
        this.digestValue = digestValue;
    }

    @Column(name="uri")
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Column(name="xml_id")
    public String getXmlID() {
        return xmlID;
    }

    public void setXmlID(String xmlID) {
        this.xmlID = xmlID;
    }
}
