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
package org.eiichiro.gig.appengine;

import org.eiichiro.acidhouse.Transaction;
import org.eiichiro.acidhouse.appengine.AppEngineStrongDatastoreSession;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.interceptor.Around;
import org.eiichiro.jaguar.interceptor.Interceptor;
import org.eiichiro.reverb.reflection.Invocation;

/**
 * {@code AppEngineGlobalTransactionalInterceptor} is a Gig interceptor 
 * component to intercept transactional method ({@code @GlobalTransactional} 
 * annotated component method) and to control the transaction commitment of Acid 
 * House {@code AppEngineStrongDatastoreSession}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@GlobalTransactional
@Interceptor
public class AppEngineGlobalTransactionalInterceptor {

	@Inject private AppEngineStrongDatastoreSession session;
	
	/**
	 * Controls Acid House {@code AppEngineStrongDatastoreSession}'s transaction 
	 * commitment.
	 * First, this method begins a transaction on 
	 * {@code AppEngineStrongDatastoreSession}. If the transactional method 
	 * invocation is succeeded, this method commits the transaction. If the 
	 * transactional method throws any exception, this method rolls back the 
	 * transaction and re-throws the exception.
	 * 
	 * @param invocation The encoded method invocation of transactional method.
	 * @return The invocation result.
	 * @throws Throwable If transactional method throws any exception.
	 */
	@Around
	public Object transact(Invocation<?> invocation) throws Throwable {
		Transaction transaction = null;
		
		if (!session.isTransactional()) {
			transaction = session.beginTransaction();
		}
		
		try {
			Object result = invocation.proceed();
			
			if (transaction != null) {
				transaction.commit();
			}
			
			return result;
		} catch (Throwable t) {
			if (transaction != null) {
				transaction.rollback();
			}
			
			throw t;
		}
	}
	
}
