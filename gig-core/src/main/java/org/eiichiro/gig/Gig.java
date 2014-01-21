/*
 * Copyright (C) 2013 Eiichiro Uchiumi. All Rights Reserved.
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
package org.eiichiro.gig;

import static org.eiichiro.gig.Version.BUILD;
import static org.eiichiro.gig.Version.MAJOR;
import static org.eiichiro.gig.Version.MINER;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.eiichiro.jaguar.Container;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.scope.Application;
import org.eiichiro.jaguar.scope.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code Gig} provides some utility methods to bootstrap/shutdown Gig 
 * application.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public final class Gig {

	private static Logger logger = LoggerFactory.getLogger(Gig.class);
	
	static {
		Logger logger = LoggerFactory.getLogger(Version.class);
		logger.info("Gig " + MAJOR + "." + MINER + "." + BUILD);
//		logger.info("Copyright (C) 2013 Eiichiro Uchiumi. All Rights Reserved.");
	}
	
	private Gig() {}

	/** Bootstraps Gig application. */
	public static void bootstrap() {
		bootstrap(null);
	}

	@SuppressWarnings("unchecked")
	static void bootstrap(ServletContext context) {
		logger.info("Starting Gig");
		Jaguar.bootstrap();
		
		if (context != null) {
			Container container = Jaguar.component(Container.class);
			WebContext<ServletContext> c = (WebContext<ServletContext>) container.component(container.contexts().get(Application.class));
			c.associate(context);
		}
		
		Jaguar.install(Configuration.class);
		Configuration configuration = Jaguar.component(Configuration.class);
		Class<?> deployment = configuration.get(Configuration.DEPLOYMENT);
		
		if (deployment != null) {
			Jaguar.deployment(deployment);
		}
		
		Jaguar.install(Namespace.class);
		Collection<Class<?>> components = configuration.get(Configuration.COMPONENTS);
		Jaguar.install(components);
		Collection<Class<?>> endpoints = configuration.get(Configuration.ENDPOINTS);
		
		if (endpoints != null) {
			for (Class<?> endpoint : endpoints) {
				if (!Jaguar.installed(endpoint)) {
					Jaguar.install(endpoint);
				}
			}
		}
	}
	
	/** Shutdowns Gig application. */
	public static void shutdown() {
		shutdown(null);
	}
	
	@SuppressWarnings("unchecked")
	public static void shutdown(ServletContext context) {
		if (context != null) {
			Container container = Jaguar.component(Container.class);
			WebContext<ServletContext> c = (WebContext<ServletContext>) container.component(container.contexts().get(Application.class));
			c.deassociate(context);
		}
		
		Jaguar.shutdown();
		logger.info("Gig stopped");
	}
	
}
