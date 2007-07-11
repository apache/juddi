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
 */
package org.apache.juddi.datatype.tmodel;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.RegistryObject;

/**
 * "Descriptions of specifications for services or taxonomies. Basis for
 * technical fingerprints" - XML Structure Reference
 *
 * The primary role that the tModel plays is to represent a technical
 * spec. Implementors of that spec reference the tModel with a tModelKey in
 * a bindingTemplate in the service definition.
 *
 * The other place that a tModel reference is used is in the identifierBag and
 * categoryBag structures. Used in this context the tModel reference represents
 * a relationship between the keyed name-value pairs to the namespace within
 * which they are meaningful. Eg, a tModel named "US tax codes". The tModel
 * is still a specific concept but instead of being a technical specification
 * it represents a unique namespace within which tax code IDs have meaning.
 * Once this meaning is established a business can use the tModelKey for the
 * tax code tModel as a unique reference that qualifies the entry in the
 * identifierBag. UDDI has already defined a number of tModels including US tax
 * codes, NAICS, UNSPC, and an unnamed geographic taxonomy.
 * (See also KeyedReference).
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModel implements RegistryObject
{
  /**
   * UDDI Type Taxonomy [uddi-org:types]
   * This taxonomy assists in general categorization of the tModels themselves.
   */
  public static final String TYPES_TMODEL_KEY = "UUID:C1ACF26D-9672-4404-9D70-39B756E62AB4";

  /**
   * Business Taxonomy: NAICS (1997 Release) [ntis-gov:naics:1997]
   * This tModel defines the NAICS (North American Industry Classification
   * System) 1997 Release industry taxonomy.
   */
  public static final String NAICS_TMODEL_KEY = "UUID:C0B9FE13-179F-413D-8A5B-5004DB8E5BB2";

  /**
   * Product Taxonomy: UNSPSC (Version 3.1) [unspsc-org:unspsc:3-1]
   * This tModel defines the UNSPSC (United Nations Standard Products and
   * Services Code System) version 3.1 product taxonomy.
   * <B>This taxonomy has been superceeded by the Universal Standard Products
   * and Services Classification (see {@link #UNSPSC_73_TMODEL_KEY}) taxonomy.
   */
  public static final String UNSPSC_TMODEL_KEY = "UUID:DB77450D-9FA8-45D4-A7BC-04411D14E384";

  /**
   * Product and Services Taxonomy:UNSPSC (Version 7.3) [unspsc-org:unspsc]
   * This tModel defines the UNSPSC (Universal Standard Products and Services
   * Classification) version 7.3 product and services taxonomy.
   */
  public static final String UNSPSC_73_TMODEL_KEY = "UUID:CD153257-086A-4237-B336-6BDCBDCC6634";

  /**
   * ISO 3166 Geographic Taxonomy [uddi-org:iso-ch:3166-1999]
   * This tModel defines the ISO 3166 geographic classification taxonomy.
   */
  public static final String ISO_CH_TMODEL_KEY = "UUID:4E49A8D6-D5A2-4FC2-93A0-0411D8D19E88";

  /**
   * UDDI Other Taxonomy [uddi-org:misc-taxomony]
   * This tModel defines an unidentified taxonomy.
   */
  public static final String GENERAL_KEYWORDS_TMODEL_KEY = "UUID:A035A07C-F362-44dd-8F95-E2B134BF43B4";

  /**
   * UDDI Owning Business [uddi-org:owningBusiness]
   * This tModel identifies the businessEntity that published or owns the
   * tagged information. Used with tModels to establish an 'owned'
   * relationship with a registered businessEntity.
   */
  public static final String OWNING_BUSINESS_TMODEL_KEY = "UUID:4064C064-6D14-4F35-8953-9652106476A9";

  /**
   * UDDI businessEntity relationship [uddi-org:relationships]
   * This tModel is used to describe business relationships. Used in the
   * publisher assertion messages.
   */
  public static final String RELATIONSHIPS_TMODEL_KEY = "UUID:807A2C6A-EE22-470D-ADC7-E0424A337C03";

  /**
   * UDDI Operators [uddi-org:operators]
   * This checked value set is used to identify UDDI operators.
   */
  public static final String OPERATORS_TMODEL_KEY = "UUID:327A56F0-3299-4461-BC23-5CD513E95C55";

  /**
   * D-U-N-S� Number Identifier System [dnb-com:D-U-N-S]
   * This tModel is used for the Dun & Bradstreet D-U-N-S� Number identifier.
   */
  public static final String D_U_N_S_TMODEL_KEY = "UUID:8609C81E-EE1F-4D5A-B202-3EB13AD01823";

  /**
   * Thomas Register Supplier Identifier Code System
   * [thomasregister-com:supplierID]
   * This tModel is used for the Thomas Register supplier identifier codes.
   */
  public static final String THOMAS_REGISTER_TMODEL_KEY = "UUID:B1B1BAF5-2329-43E6-AE13-BA8E97195039";

  /**
   * UDDI IsReplacedBy [uddi-org:isReplacedBy]
   * An identifier system used to point (using UDDI keys) to the tModel
   * (or businessEntity) that is the logical replacement for the one in
   * which isReplacedBy is used.
   */
  public static final String IS_REPLACED_BY_TMODEL_KEY = "UUID:E59AE320-77A5-11D5-B898-0004AC49CC1E";

  /**
   * Email based web service [uddi-org:smtp]
   * This tModel is used to describe a web service that is invoked through
   * SMTP email transmissions. These transmissions may be between people or
   * applications.
   */
  public static final String SMTP_TMODEL_KEY = "UUID:93335D49-3EFB-48A0-ACEA-EA102B60DDC6";

  /**
   * Fax based web service [uddi-org:fax]
   * This tModel is used to describe a web service that is invoked through
   * fax transmissions. These transmissions may be between people or
   * applications.
   */
  public static final String FAX_TMODEL_KEY = "UUID:1A2B00BE-6E2C-42F5-875B-56F32686E0E7";

  /**
   * FTP based web service [uddi-org:ftp]
   * This tModel is used to describe a web service that is invoked through
   * file transfers via the ftp protocol.
   */
  public static final String FTP_TMODEL_KEY = "UUID:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674";

  /**
   * Telephone based web service [uddi-org:telephone]
   * This tModel is used to describe a web service that is invoked through a
   * telephone call and interaction by voice and/or touch-tone.
   */
  public static final String TELEPHONE_TMODEL_KEY = "UUID:38E12427-5536-4260-A6F9-B5B530E63A07";

  /**
   * Web browser or HTTP based web service [uddi-org:http]
   * This tModel is used to describe a web service that is invoked through a
   * web browser and/or the HTTP protocol.
   */
  public static final String HTTP_TMODEL_KEY = "UUID:68DE9E80-AD09-469D-8A37-088422BFBC36";

  /**
   * HTTP Web Home Page URL [uddi-org:homepage]
   * This tModel is used as the bindingTemplate fingerprint for a web home
   * page reference.
   */
  public static final String HOMEPAGE_TMODEL_KEY = "UUID:4CEC1CEF-1F68-4B23-8CB7-8BAA763AEB89";

  String tModelKey;
  String authorizedName;
  String operator;
  Name name;
  Vector descVector;
  OverviewDoc overviewDoc;
  IdentifierBag identifierBag;
  CategoryBag categoryBag;

  /**
   * Construct a new initialized TModel instance.
   */
  public TModel()
  {
  }

  /**
   * Construct a brand new TModel with a given name.  Assumes this is a brand
   * new TModel instance and doesn't have a key yet - one is created in the
   * constructor.
   *
   * @param name The name of the tModel.
   */
  public TModel(String name)
  {
    setName(name);
  }

  /**
   * Construct a new tModel with a given name and key.
   *
   * @param name The name of the tModel, given as a string.
   * @param key The key of the tModel.
   */
  public TModel(String name,String key)
  {
    setName(name);
    this.tModelKey = key;
  }

  /**
   * Sets the key of this tModel to the given key.
   *
   * @param key The new key of this tModel.
   */
  public void setTModelKey(String key)
  {
    this.tModelKey = key;
  }

  /**
   * Returns the key of this tModel.
   *
   * @return The key of this tModel.
   *
   */
  public String getTModelKey()
  {
    return this.tModelKey;
  }

  /**
   * Sets the authorized name of this tModel to the given name.
   *
   * @param name The new authorized name of this tModel.
   */
  public void setAuthorizedName(String name)
  {
    this.authorizedName = name;
  }

  /**
   * Returns the authorized name of this tModel.
   *
   * @return The authorized name of this tModel, or null if this tModel
   * doesn't have an authorized name.
   */
  public String getAuthorizedName()
  {
    return this.authorizedName;
  }

  /**
   * Sets the name of this tModel to the given name.
   *
   * @param name The new name of this tModel.
   */
  public void setName(String nameValue)
  {
      if (nameValue == null) {
          this.name = null;
      } else {
          this.name = new Name(nameValue);
      }
  }

  /**
   * Returns the name of this tModel.
   *
   * @return The name of this tModel.
   */
  public Name getName()
  {
      return this.name;
  }

  /**
    * Sets the name of this tModel to the given name.
    *
    * @param name The new name of this tModel.
    */
  public void setName(Name name)
  {
      this.name = name;
  }

  /**
   * Adds the given description.
   *
   * @param descr The description to add.
   */
  public void addDescription(Description descr)
  {
    // just return if the Description parameter is null (nothing to add)
    if (descr == null)
      return;

    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(descr);
  }

  /**
   * Sets the description list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Description class.
   *
   * @param descriptions Collection of Description objects to set
   */
  public void setDescriptionVector(Vector descriptions)
  {
    this.descVector = descriptions;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions. If the aren't any descriptions, an empty
   *  enumeration is returned.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Sets the overviewDoc of this tModel to the given overviewDoc.
   *
   * @param doc The new overviewDoc of this tModel, or null if this tModel
   * doesn't have an overviewDoc anymore.
   */
  public void setOverviewDoc(OverviewDoc doc)
  {
    this.overviewDoc = doc;
  }

  /**
   * Returns the overviewDoc of this tModel.
   *
   * @return The overviewDoc of this tModel, or null if this tModel doesn't have an overviewDoc.
   */
  public OverviewDoc getOverviewDoc()
  {
    return this.overviewDoc;
  }

  /**
   * Sets the operator of this tModel to the given operator.
   *
   * @param operator The new operator of this tModel.
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   * Returns the operator of this tModel.
   *
   * @return The operator of this tModel, or null if this tModel
   * doesn't have an operator.
   */
  public String getOperator()
  {
    return this.operator;
  }

  /**
   * Add a new identifier to this tModel.
   *
   * @param k The identifier to add.
   */
  public void addIdentifier(KeyedReference k)
  {
    if (this.identifierBag == null)
      this.identifierBag = new IdentifierBag();
    this.identifierBag.addKeyedReference(k);
  }

  /**
   * Returns the identifierbag of this tModel.
   *
   * @return The identifierbag of this tModel. If this tModel doesn't
   * contain any identifiers, an empty enumeration is returned.
   */
  public IdentifierBag getIdentifierBag()
  {
    return this.identifierBag;
  }

  /**
   * Set the categorybag of this business entity to the given one.
   *
   * @param bag The new categorybag.
   */
  public void setIdentifierBag(IdentifierBag bag)
  {
    this.identifierBag = bag;
  }

  /**
   * Add a new category to this tModel.
   *
   * @param keyedRef The category to add.
   */
  public void addCategory(KeyedReference keyedRef)
  {
    if (this.categoryBag == null)
      this.categoryBag = new CategoryBag();
    this.categoryBag.addKeyedReference(keyedRef);
  }

  /**
   * Returns the categorybag of this tModel.
   *
   * @return The categorybag of this tModel. If this tModel doesn't
   * contain any categories, an empty enumeration is returned.
   */
  public CategoryBag getCategoryBag()
  {
    return this.categoryBag;
  }

  /**
   * Set the categorybag of this business entity to the given one.
   *
   * @param bag The new categorybag.
   */
  public void setCategoryBag(CategoryBag bag)
  {
    this.categoryBag = bag;
  }
}
