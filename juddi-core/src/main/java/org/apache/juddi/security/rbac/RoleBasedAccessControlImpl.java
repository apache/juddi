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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessLevel;
import org.apache.juddi.api_v3.Action;
import org.apache.juddi.api_v3.EntityType;
import org.apache.juddi.api_v3.GetPermissionsMessageRequest;
import org.apache.juddi.api_v3.GetPermissionsMessageResponse;
import org.apache.juddi.api_v3.Permission;
import org.apache.juddi.api_v3.SetPermissionsMessageRequest;
import org.apache.juddi.api_v3.SetPermissionsMessageResponse;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.ResourceConfig;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.security.IAccessControl;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OperationalInfo;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.RelatedBusinessInfo;
import org.uddi.api_v3.RelatedBusinessInfos;
import org.uddi.api_v3.ServiceInfo;
import org.uddi.api_v3.ServiceInfos;
import org.uddi.api_v3.SharedRelationships;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInfos;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * RBAC functionality, to be pulled from J2EE container, such as Tomcat or
 * Jboss, for obtaining user roles.<br><br>
 * Permissions are then calculated based on an inheritance model, similar to
 * most systems. I.e.<br><br>
 * If a permission doesn't exist for a binding, the service's permission set
 * then applies. If the service's permission set is not defined, then the
 * business's permission set applies.
 * <br><br>
 * If the requestor does not have permission for the entity, all content is
 * nulled out and replaced with a 'redacted' string. This will preserve
 * functionality for pagination operations, limits, offsets, etc.
 *
 * @author Alex O'Ree
 * @since 3.4
 */
public class RoleBasedAccessControlImpl implements IAccessControl {

    private static final Log log = LogFactory.getLog(RoleBasedAccessControlImpl.class);
    private static final String REDACTED = ResourceConfig.getGlobalMessage("rbac.redacted");
    public static final String EVERYONE = "everyone";

    private void redact(BusinessService bs) {
        bs.setBusinessKey(REDACTED);
        bs.setServiceKey(REDACTED);
        bs.setBindingTemplates(null);
        bs.setCategoryBag(null);
        bs.getDescription().clear();
        bs.getSignature().clear();
        bs.getName().clear();
        bs.getName().add(new Name(REDACTED, "en"));

    }

    private boolean hasReadAccess(WebServiceContext ctx, List<RbacRulesModel> rules, String username) {
        for (RbacRulesModel r : rules) {
            if (r.getContainerRole().equalsIgnoreCase(EVERYONE)) {
                if (r.getAccessLevelAsEnum() == AccessLevel.NONE) //explicit deny
                {
                    return false;
                }
            }
            if (ctx.isUserInRole(r.getContainerRole())) {
                if (r.getAccessLevelAsEnum() == AccessLevel.NONE) //explicit deny
                {
                    return false;
                }
                return true;
            }
            if (ctx.getUserPrincipal() != null && ctx.getUserPrincipal().getName().equals(username)) {
                if (r.getAccessLevelAsEnum() == AccessLevel.NONE) //explicit deny
                {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean has(WebServiceContext ctx, List<RbacRulesModel> rules, AccessLevel requiredLevel) {
        for (RbacRulesModel r : rules) {
            if (r.getContainerRole().equalsIgnoreCase(EVERYONE)) {
                if (r.getAccessLevelAsEnum().getLevel() >= requiredLevel.getLevel()) {
                    return true;
                }
            }
            if (ctx.isUserInRole(r.getContainerRole())) {
                if (r.getAccessLevelAsEnum().getLevel() >= requiredLevel.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<RbacRulesModel> getPermissionSet(String serviceKey) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        List<RbacRulesModel> set = new ArrayList<>();
        try {
            tx.begin();

            Query createQuery = em.createQuery("select c from RbacRulesModel c where c.uddiEntityId=:id");
            createQuery.setParameter("id", serviceKey);
            set = createQuery.getResultList();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        return set;

    }

    private UddiEntity loadEntity(String serviceKey, Class clazz) {

        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            try {
                return (UddiEntity) em.find(clazz, serviceKey);
            } catch (ClassCastException e) {
            }

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        return null;
    }

    private void redact(BindingTemplate bt) {
        bt.setBindingKey(REDACTED);
        bt.getDescription().clear();
        bt.getSignature().clear();
        bt.setHostingRedirector(null);
        bt.setAccessPoint(null);
        bt.setTModelInstanceDetails(null);
        bt.setAccessPoint(null);
        bt.setCategoryBag(null);
    }

    @Override
    public List<BusinessService> filterServices(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessService> items) {

        //load access rules from database
        for (BusinessService bs : items) {
            //get the permission for this entity.
            UddiEntity ue = loadEntity(bs.getServiceKey(), org.apache.juddi.model.BusinessService.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getServiceKey());
            if (rules.isEmpty()) {
                //get the rules for the parent business
                rules = getPermissionSet(bs.getBusinessKey());

            }
            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
                continue;
            }
            filterBindingTemplates(ctx, username, bs.getBindingTemplates().getBindingTemplate());

        }
        return new ArrayList(items);
    }

    @Override
    public List<BusinessEntity> filterBusinesses(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessEntity> items) {

        //load access rules from database
        for (BusinessEntity bs : items) {
            //get the permission for this entity.
            UddiEntity ue = loadEntity(bs.getBusinessKey(), org.apache.juddi.model.BusinessEntity.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getBusinessKey());
            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
                continue;
            }
            filterServices(ctx, username, bs.getBusinessServices().getBusinessService());

        }
        return new ArrayList(items);

    }

    @Override
    public List<BusinessInfo> filterBusinessInfo(WebServiceContext ctx, UddiEntityPublisher username, List<BusinessInfo> items) {
        //load access rules from database
        for (BusinessInfo bs : items) {
            //get the permission for this entity.
            UddiEntity ue = loadEntity(bs.getBusinessKey(), org.apache.juddi.model.BusinessService.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getBusinessKey());
            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
                continue;
            }
            if (bs.getServiceInfos() != null) {
                filterServiceInfo(ctx, username, bs.getServiceInfos());
            }

        }
        return new ArrayList(items);

    }

    @Override
    public List<TModel> filterTModels(WebServiceContext ctx, UddiEntityPublisher username, List<TModel> items) {
        //load access rules from database
        for (TModel bs : items) {
            //get the permission for this entity.
            UddiEntity ue = loadEntity(bs.getTModelKey(), org.apache.juddi.model.Tmodel.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getTModelKey());
            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
                continue;
            }

        }
        return new ArrayList(items);

    }

    @Override
    public List<BindingTemplate> filterBindingTemplates(WebServiceContext ctx, UddiEntityPublisher username, List<BindingTemplate> items) {

        //load access rules from database
        for (BindingTemplate bs : items) {
            //get the permission for this entity.
            UddiEntity ue = loadEntity(bs.getBindingKey(), org.apache.juddi.model.BindingTemplate.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getBindingKey());
            if (rules.isEmpty()) {
                rules = getPermissionSet(bs.getServiceKey());
            }

            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
            }

        }
        return new ArrayList(items);

    }

    @Override
    public RelatedBusinessInfos filtedRelatedBusinessInfos(WebServiceContext ctx, UddiEntityPublisher username, RelatedBusinessInfos items) {
        //TODO
        if (items == null) {
            return null;
        }
        for (RelatedBusinessInfo bs : items.getRelatedBusinessInfo()) {
            UddiEntity ue = loadEntity(bs.getBusinessKey(), org.apache.juddi.model.BusinessService.class);
            if (ue == null) {
                redact(bs);
                continue;   //access denied
            }
            if (username == null) {
                redact(bs);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(bs.getBusinessKey());
            if (rules.isEmpty()) {
                redact(bs);
                continue;   //access denied
            }
            if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                redact(bs); //also access denied, either no matching role or an explicit deny
                continue;
            }
            if (bs.getSharedRelationships() != null) {

                for (SharedRelationships si : bs.getSharedRelationships()) {
                    boolean redact = false;
                    for (PublisherAssertion pa : si.getPublisherAssertion()) {
                        UddiEntity ue2 = loadEntity(pa.getFromKey(), org.apache.juddi.model.BusinessEntity.class);
                        if (ue2 == null) {
                            redact = true;
                            break;
                        }
                        if (username == null) {
                            redact = true;
                            break;   //access denied

                        }
                        if (username.isOwner(ue)) {
                            //keep it
                            continue;
                        }

                        List<RbacRulesModel> rules2 = getPermissionSet(pa.getFromKey());

                        if (rules2.isEmpty()) {
                            redact = true;
                            break;  //access denied
                        }
                        if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                            redact = true; //also access denied, either no matching role or an explicit deny
                            break;
                        }

                        ue2 = loadEntity(pa.getToKey(), org.apache.juddi.model.BusinessEntity.class);
                        if (ue2 == null) {
                            redact = true;
                            break;
                        }

                        if (username.isOwner(ue2)) {
                            //keep it
                            continue;
                        }

                        rules2 = getPermissionSet(pa.getToKey());

                        if (rules2.isEmpty()) {
                            redact = true;
                            break; //access denied
                        }
                        if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                            redact = true; //also access denied, either no matching role or an explicit deny
                            break;
                        }
                    }

                    if (redact) {

                    }

                }

            }
        }

        return items;
    }

    @Override
    public ServiceInfos filterServiceInfo(WebServiceContext ctx, UddiEntityPublisher username, ServiceInfos items) {

        if (items == null) {
            return null;
        }
        for (ServiceInfo si : items.getServiceInfo()) {
            UddiEntity ue = loadEntity(si.getServiceKey(), org.apache.juddi.model.BusinessService.class);
            if (ue == null) {
                si.setServiceKey(REDACTED);
                continue;   //access denied
            }
            if (username == null) {
                si.setServiceKey(REDACTED);
                continue;   //access denied

            }
            if (username.isOwner(ue)) {
                //keep it
                continue;
            }

            List<RbacRulesModel> rules = getPermissionSet(si.getServiceKey());

            if (!rules.isEmpty() && !hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                si.setServiceKey(REDACTED);
            }
            if (rules.isEmpty()) {
                rules = getPermissionSet(si.getBusinessKey());
                if (rules.isEmpty()) {
                    si.setBusinessKey(REDACTED);
                } else {
                    if (!hasReadAccess(ctx, rules, username.getAuthorizedName())) {
                        si.setBusinessKey(REDACTED);
                    }
                }
            }

        }
        return items;
    }

    @Override
    public TModelInfos filterTModelInfo(WebServiceContext ctx, UddiEntityPublisher username, TModelInfos items) {
        //TODO
        return (items);
    }

    @Override
    public List<OperationalInfo> filterOperationalInfo(WebServiceContext ctx, UddiEntityPublisher username, List<OperationalInfo> items) {
        //TODO
        return new ArrayList(items);
    }

    private void redact(BusinessEntity bs) {
        bs.setBusinessKey(REDACTED);
        bs.setBusinessServices(null);
        bs.setCategoryBag(null);
        bs.setContacts(null);
        bs.setDiscoveryURLs(null);
        bs.setIdentifierBag(null);
        bs.getDescription().clear();
        bs.getSignature().clear();
        bs.getName().clear();
        bs.getName().add(new Name(REDACTED, "en"));
    }

    @Override
    public GetPermissionsMessageResponse getPermissions(GetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException {

        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        List<RbacRulesModel> set = new ArrayList<>();
        try {
            tx.begin();
            Query createQuery = null;
            if (arg0.getEntityId() != null && arg0.getEntityId().length() > 0) {
                createQuery = em.createQuery("select c from RbacRulesModel c where c.uddiEntityId=:id", RbacRulesModel.class);
                createQuery.setParameter("id", arg0.getEntityId());
            } else {
                createQuery = em.createQuery("select c from RbacRulesModel c", RbacRulesModel.class);
            }

            set = createQuery.getResultList();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        GetPermissionsMessageResponse response = new GetPermissionsMessageResponse();
        for (RbacRulesModel item : set) {
            Permission permission = new Permission();
            permission.setEntityId(item.getUddiEntityId());
            permission.setLevel((item.getAccessLevelAsEnum()));
            permission.setAction(Action.NOOP);
            permission.setTarget(item.getContainerRole());
            //TODO permission.setType(item.);
            response.getLevel().add(permission);
        }

        return response;
    }

    @Override
    public SetPermissionsMessageResponse setPermissions(SetPermissionsMessageRequest arg0) throws DispositionReportFaultMessage, RemoteException {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            for (Permission perm : arg0.getLevel()) {
                if (perm.getAction() != Action.NOOP) {
                    Query createQuery = null;
                    createQuery = em.createQuery("delete from RbacRulesModel e where e.uddiEntityId=:id and e.containerRole=:user");
                    createQuery.setParameter("id", perm.getEntityId());
                    createQuery.setParameter("user", perm.getTarget());
                    createQuery.executeUpdate();
                }

                if (perm.getAction() == Action.ADD) {
                    RbacRulesModel r = new RbacRulesModel();
                    r.setAccessLevel(perm.getLevel().name());

                    r.setContainerRole(perm.getTarget());
                    r.setUddiEntityId(perm.getEntityId());
                    r.setId(UUID.randomUUID().toString());
                    em.persist(r);
                }
            }
            tx.commit();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
        SetPermissionsMessageResponse response = new SetPermissionsMessageResponse();
        return response;
    }

    private void redact(BusinessInfo bs) {

        bs.setBusinessKey(REDACTED);
        bs.getDescription().clear();
        bs.setServiceInfos(null);
        bs.getName().clear();
        bs.getName().add(new Name(REDACTED, "en"));
    }

    private void redact(TModel bs) {

        bs.setTModelKey(REDACTED);
        bs.getDescription().clear();
        bs.setCategoryBag(null);

        bs.setName(new Name(REDACTED, "en"));
        bs.getDescription().clear();
        bs.getOverviewDoc().clear();
        bs.getSignature().clear();
        bs.setIdentifierBag(null);
    }

    private void redact(RelatedBusinessInfo bs) {
        bs.setBusinessKey(REDACTED);
        bs.getDescription().clear();
        bs.getName().clear();
        bs.getName().add(new Name(REDACTED, "en"));
        bs.getSharedRelationships().clear();
    }

    @Override
    public boolean hasPermission(AccessLevel level, WebServiceContext ctx, UddiEntityPublisher actor, String entityid, EntityType type) {

        UddiEntity ue = null;

        switch (type) {
            case BINDING:
                ue = loadEntity(entityid, org.apache.juddi.model.BindingTemplate.class);
                break;
            case BUSINESS:
                ue = loadEntity(entityid, org.apache.juddi.model.BusinessEntity.class);
                break;
            case SERVICE:
                ue = loadEntity(entityid, org.apache.juddi.model.BusinessService.class);
                break;
            case TMODEL:
                ue = loadEntity(entityid, org.apache.juddi.model.Tmodel.class);
                break;
            default:
                log.warn("umhandled case for " + type);
        }

        if (ue == null) {
            return false;
        }
        if (actor == null) {
            return false;

        }
        if (actor.isOwner(ue)) {
            return true;
        }

        List<RbacRulesModel> rules = getPermissionSet(entityid);

        if (rules.isEmpty()) {
            return false;
        }

        return has(ctx, rules, level);
    }

}
