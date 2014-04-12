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
import javax.persistence.EntityManagerFactory;

import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.lifecycle.Activated;
import org.eiichiro.jaguar.lifecycle.Passivated;
import org.eiichiro.jaguar.scope.Request;
import org.eiichiro.jaguar.validation.Required;

/**
 * {@code JPAEntityManagerComponent} is a representing JPA 2.0 
 * {@code EntityManager} for PostgreSQL-based Heroku database service.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Name("org.eiichiro.gig.persistence.JPAEntityManager")
@Request
public class JPAEntityManagerComponent extends Component<EntityManager> {

	private EntityManager manager;
	
	@Inject @Required private EntityManagerFactory factory;
	
	/**
	 * Lifecycle callback to assemble this instance with the {@code EntityManager} 
	 * {@link JPAEntityManagerFactoryComponent} returns.
	 */
	@Activated
	public void assemble() {
		manager = factory.createEntityManager();
	}
	
	/** Lifecycle callback to close the {@code EntityManager}. */
	@Passivated
	public void close() {
		if (manager != null) {
			manager.close();
		}
	}
	
	/**
	 * Returns the {@code EntityManager} this instance manages.
	 * The {@code EntityManager} may be <code>null</code> if the 
	 * {@code JPAEntityManagerFactoryComponent} could not establish the connection 
	 * to the database service.
	 */
	@Override
	public EntityManager instance() {
		return manager;
	}

}
