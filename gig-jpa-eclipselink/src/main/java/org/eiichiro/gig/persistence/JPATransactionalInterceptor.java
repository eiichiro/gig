/*
 * Copyright (C) 2012 Eiichiro Uchiumi. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eiichiro.gig.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.eiichiro.gig.Transactional;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.interceptor.Around;
import org.eiichiro.jaguar.interceptor.Interceptor;
import org.eiichiro.jaguar.validation.Required;
import org.eiichiro.reverb.reflection.Invocation;

/**
 * {@code JPATransactionalInterceptor} is a Gig interceptor component to 
 * intercept transactional method ({@code @Transactional} annotated component 
 * method) and to control the transaction commitment of JPA (Java Persistence API) 
 * {@code EntityManager}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Transactional
@Interceptor
public class JPATransactionalInterceptor {

	@Inject @Required private EntityManager manager;
	
	/**
	 * Controls JPA {@code EntityManager}'s transaction commitment.
	 * First, this method begins a transaction on {@code EntityManager}. If the 
	 * transactional method invocation is succeeded, this method commits the 
	 * transaction. If the transactional method throws any exception, this 
	 * method rolls back the transaction and re-throws the exception.
	 * 
	 * @param invocation The encoded method invocation of transactional method.
	 * @return The invocation result.
	 * @throws Throwable If transactional method throws any exception.
	 */
	@Around
	public Object transact(Invocation<?> invocation) throws Throwable {
		EntityTransaction transaction = manager.getTransaction();
		
		if (!transaction.isActive()) {
			transaction.begin();
		}
		
		try {
			Object result = invocation.proceed();
			
			if (transaction.isActive()) {
				transaction.commit();
			}
			
			return result;
		} catch (Throwable t) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			
			throw t;
		}
	}
	
}
