/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */package org.apache.juddi.uuidgen;/** * A Universally Unique Identifier (UUID) is a 128 bit number generated * according to an algorithm that is garanteed to be unique in time and space * from all other UUIDs. It consists of an IEEE 802 Internet Address and * various time stamps to ensure uniqueness. For a complete specification, * see ftp://ietf.org/internet-drafts/draft-leach-uuids-guids-01.txt [leach]. * * @author Steve Viens (sviens@apache.org) */public interface UUIDGen{  /**   *   */  String uuidgen();  /**   *   */  String[] uuidgen(int nmbr);}