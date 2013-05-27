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

package org.apache.juddi.security;

/**
 *
 * @author Alex O'Ree
 */
public enum AccessLevel {
    /**
     * No access at all
     */
    NONE,
    /**
     * Read only access, cannot make changes
     */
    READ,
    /**
     * Can view, read, make changes, and delete a specific entity
     */
    WRITE,
    /**
     * Can view, read, make changes, delete a specific entity, can initiate a custody transfer, and delegate permissions
     * to another user
     */
    OWN,
    /**
     * can create new entities
     */
    CREATE
    
}
