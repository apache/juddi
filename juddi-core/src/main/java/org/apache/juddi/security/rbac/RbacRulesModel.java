/*
 * Copyright 2019 The Apache Software Foundation.
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
package org.apache.juddi.security.rbac;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.juddi.security.AccessLevel;

/**
 *
 * @author Alex O'Ree
 */
@Entity
@Table(name = "j3_rbac_rules")
public class RbacRulesModel implements Serializable {

    private String id;
    private String containerRole;
    private String level;

    /**
     * this can be a binding, business, service, tmodel, etc
     */
    private String uddiEntityId;

    @Column(name = "entity_id", nullable = false, length = 51)
    public String getUddiEntityId() {
        return uddiEntityId;
    }

    public void setUddiEntityId(String uddiEntityId) {
        this.uddiEntityId = uddiEntityId;
    }

    @Column(name = "container_role", nullable = false, length = 51)
    public String getContainerRole() {
        return containerRole;
    }

    public void setContainerRole(String containerRole) {
        this.containerRole = containerRole;
    }

    @Column(name = "access_level", nullable = false, length = 51)
    public AccessLevel getAccessLevel() {
        return AccessLevel.valueOf(level);
    }

    public void setAccessLevel(AccessLevel level) {
        this.level = level.name();
    }

    @Id
    @Column(name = "rule_id", nullable = false, length = 51)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
