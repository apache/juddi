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
@Table(name="j3_key_data_value")
public class KeyDataValue implements java.io.Serializable {
    private static final long serialVersionUID = -6353389848796421615L;
    
    private Long id;
    private KeyInfo keyInfo;
    private String keyDataType;
    private String keyDataName;
    private byte[] keyDataValueBytes;
    private String keyDataValueString;
    private KeyDataValue keyDataValue;
    private List<KeyDataValue> keyDataValueList = new ArrayList<KeyDataValue>();

    public KeyDataValue() {
    }
    
    public KeyDataValue(KeyInfo keyInfo, String keyDataType, String keyDataName, byte[] keyDataValueBytes, String keyDataValueString, KeyDataValue keyDataValue) {
        this.keyInfo = keyInfo;
        this.keyDataType = keyDataType;
        this.keyDataName = keyDataName;
        this.keyDataValueBytes = keyDataValueBytes;
        this.keyDataValueString = keyDataValueString;
        this.keyDataValue = keyDataValue;
    }
    
    
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_info_key", nullable = true)
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_data_value_key", nullable = true)
    public KeyDataValue getKeyDataValue() {
        return keyDataValue;
    }

    public void setKeyDataValue(KeyDataValue keyDataValue) {
        this.keyDataValue = keyDataValue;
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "keyDataValue")
    @OrderBy
    public List<KeyDataValue> getKeyDataValueList() {
        return keyDataValueList;
    }

    public void setKeyDataValueList(List<KeyDataValue> keyDataValueList) {
        this.keyDataValueList = keyDataValueList;
    }

    @Column(name="key_data_name")
    public String getKeyDataName() {
        return keyDataName;
    }

    public void setKeyDataName(String keyDataName) {
        this.keyDataName = keyDataName;
    }
    
    @Column(name="key_data_type")
    public String getKeyDataType() {
        return keyDataType;
    }

    public void setKeyDataType(String keyDataType) {
        this.keyDataType = keyDataType;
    }

    @Lob
    @Column(name="key_data_value", length = 65636)
    public byte[] getKeyDataValueBytes() {
        return keyDataValueBytes;
    }

    public void setKeyDataValueBytes(byte[] keyDataValueBytes) {
        this.keyDataValueBytes = keyDataValueBytes;
    }

    @Lob
    @Column(name="key_data_value_string", length = 65636)
    public String getKeyDataValueString() {
        return keyDataValueString;
    }

    public void setKeyDataValueString(String keyDataValueString) {
        this.keyDataValueString = keyDataValueString;
    }

    @Override
    public String toString() {
        return "KeyDataValue{" + "id=" + id + ", keyDataType=" + keyDataType + ", keyDataName=" + keyDataName + ", keyDataValueBytes=" + keyDataValueBytes + ", keyDataValueString=" + keyDataValueString + ", keyDataValueList=" + keyDataValueList + '}';
    }
}
