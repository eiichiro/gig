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

import javax.servlet.ServletContextEvent;

import org.eiichiro.jaguar.Jaguar;

/**
 * {@code WebListener} is a {@code org.eiichiro.jaguar.WebListener} extension to 
 * bootstrap/shutdown Gig application.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class WebListener extends org.eiichiro.jaguar.WebListener {

	/**
	 * Bootstraps Gig application in web environment.
	 * 
	 * @param sce {@code ServletContextEvent}.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Gig.bootstrap(sce.getServletContext());
		Jaguar.assemble(this);
	}
	
	/**
	 * Shutdowns Gig application in web environment.
	 * 
	 * @param sce {@code ServletContextEvent}.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Gig.shutdown(sce.getServletContext());
	}
	
}
