/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
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

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eiichiro.gig.Configuration;
import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.lifecycle.Activated;
import org.eiichiro.jaguar.lifecycle.Passivated;
import org.eiichiro.jaguar.scope.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code JPAEntityManagerFactoryComponent} is a representing JPA 2.0 
 * {@code EntityManagerFactory}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Name("org.eiichiro.gig.persistence.JPAEntityManagerFactory")
@Singleton
public class JPAEntityManagerFactoryComponent extends Component<EntityManagerFactory> {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject private Configuration configuration;
	
	private EntityManagerFactory factory;
	
	/**
	 * Configures the persistence units to be connected and makes the 
	 * {@code EntityManagerFactory} instance corresponding to the persistence 
	 * unit the current application connects to.
	 * If the persistence unit is specified in the configuration with the 
	 * 'persistenceUnit' key, this class uses it to establish the connection. 
	 * Otherwise, this class uses the first-detected persistence unit (Index 0 
	 * of the persistence unit list in the 'persistence.xml'.
	 * 
	 * <b>Note: </b>In the future, the implementation will be improved for 
	 * multitenant applications. The persistence unit set on the current thread 
	 * via something like "Namespaces API" will be used for the 
	 * {@code EntityManagerFactory} creation.
	 */
	@Activated
	public void configure() {
		String persistenceUnit = configuration.get("persistenceUnit");
		
		if (persistenceUnit == null) {
			PersistenceXMLProcessor processor = new PersistenceXMLProcessor();
			processor.process();
			List<String> persistenceUnits = processor.persistenceUnits();
			
			if (persistenceUnits.isEmpty()) {
				logger.error("Cannot access to the database service in this application; " +
						"No 'persistence-unit' is specified in 'persistence.xml'");
				return;
			}
			
			persistenceUnit = persistenceUnits.get(0);
		}
		
		factory = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	/** Lifecycle callback to close the {@code EntityManagerFactory} set. */
	@Passivated
	public void close() {
		if (factory != null) {
			factory.close();
		}
	}
	
	/**
	 * Returns the {@code EntityManagerFactory} instance corresponding to the 
	 * persistence unit the current application connects.
	 * 
	 * @return The {@code EntityManagerFactory} instance corresponding to the 
	 * persistence unit the current application connects.
	 */
	@Override
	public EntityManagerFactory instance() {
		return factory;
	}
	
}
