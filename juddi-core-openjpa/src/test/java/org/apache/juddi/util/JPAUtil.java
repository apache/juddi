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

package org.apache.juddi.util;

import javax.persistence.EntityTransaction;
import javax.persistence.EntityManager;

import javax.persistence.Query;
import java.util.List;

import org.apache.juddi.config.PersistenceManager;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * 
 * Example use:
	public void test() {
		Object object = JPAUtil.getEntity(Tmodel.class, "uddi:juddi.apache.org:joepublisher:kEYGENERATOR");
		System.out.println("object=" + object);
	}
 */
public class JPAUtil {
	//TODO Comment from Code Review: This class does not seem to be in use. Do we need it?
	
	public static void persistEntity(Object uddiEntity, Object entityKey) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			Object existingUddiEntity = em.find(uddiEntity.getClass(), entityKey);
			if (existingUddiEntity != null)
				em.remove(existingUddiEntity);
			
			em.persist(uddiEntity);
	
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	public static Object getEntity(Class<?> entityClass, Object entityKey) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			Object obj = em.find(entityClass, entityKey);
			
			tx.commit();
			return obj;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	public static void deleteEntity(Class<?> entityClass, Object entityKey) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			Object obj = em.find(entityClass, entityKey);
			em.remove(obj);
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	public static List<?> runQuery(String qry, int maxRows, int listHead) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query q = em.createQuery(qry);
			q.setMaxResults(maxRows);
			q.setFirstResult(listHead);
			List<?> ret =  q.getResultList();
			tx.commit();
			return ret;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
		
	}
	
	public static void runUpdateQuery(String qry) {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			
			Query q = em.createQuery(qry);
			q.executeUpdate();
	
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}
	
	public static void removeAuthTokens() {
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			Query qry = em.createQuery("delete from AuthToken");
			qry.executeUpdate();
			
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}

	}

}
