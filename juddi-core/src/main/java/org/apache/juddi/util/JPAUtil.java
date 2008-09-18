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

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class JPAUtil {
	public static final String PERSISTENCE_UNIT_NAME = "juddiDatabase";

	private static final EntityManagerFactory emf;
	
	static {
		try {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		catch (Throwable t) {
			System.err.println("Initial entityManagerFactory creation failed:" + t);
			throw new ExceptionInInitializerError(t);
		}
	}

	public static EntityManager getEntityManager() {
		return emf.createEntityManager();
	}
	
	public static void persistEntity(Object uddiEntity, Object entityKey) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Object existingUddiEntity = em.find(uddiEntity.getClass(), entityKey);
		if (existingUddiEntity != null)
			em.remove(existingUddiEntity);
		
		em.persist(uddiEntity);

		tx.commit();
		em.close();
	}
	
	public static Object getEntity(Class<?> entityClass, Object entityKey) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Object obj = em.find(entityClass, entityKey);
		
		tx.commit();
		em.close();
		
		return obj;
	}

	public static void deleteEntity(Class<?> entityClass, Object entityKey) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Object obj = em.find(entityClass, entityKey);
		em.remove(obj);
		
		tx.commit();
		em.close();
	}
	
}
