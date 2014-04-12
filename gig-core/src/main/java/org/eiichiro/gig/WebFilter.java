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

import static org.eiichiro.jaguar.WebFilter.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.eiichiro.bootleg.BootlegFilter;
import org.eiichiro.bootleg.Pipeline;
import org.eiichiro.bootleg.Request;
import org.eiichiro.bootleg.Response;
import org.eiichiro.bootleg.Routing;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.inject.Inject;

/**
 * {@code WebFilter} is a {@code org.eiichiro.bootleg.BootlegFilter} extension 
 * for Gig to setup Web contexts and Web endpoints from {@code org.eiichiro.gig.Configuration}.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class WebFilter extends BootlegFilter {

	@Inject private Configuration configuration;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		Jaguar.assemble(this);
		super.init(filterConfig);
	}
	
	/**
	 * Sets up Web context with {@code org.eiichiro.jaguar.WebFilter} and runs 
	 * HTTP request processing pipeline.
	 * 
	 * @param request HTTP request.
	 * @param response HTTP response.
	 * @throws IOException If any I/O error has occurred.
	 * @throws ServletException If any exception has occurred in processing the 
	 * request.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = request();
		
		try {
			request((HttpServletRequest) request);
			super.doFilter(request, response, chain);
		} finally {
			request(req);
		}
	}
	
	@Override
	protected org.eiichiro.bootleg.Configuration configuration(FilterConfig config) throws ServletException {
		return new org.eiichiro.bootleg.Configuration() {

			@Override
			public void init(ServletContext context) {}
			
			@Override
			public Collection<Class<?>> endpoints() {
				return configuration.get(Configuration.ENDPOINTS);
			}

			@Override
			public Pipeline<WebContext> pipeline() {
				return configuration.get(Configuration.PIPELINE);
			}

			@Override
			public Map<String, Class<? extends Request>> requestTypes() {
				return configuration.get(Configuration.REQUEST_TYPES);
			}

			@Override
			public Map<String, Class<? extends Response>> responseTypes() {
				return configuration.get(Configuration.RESPONSE_TYPES);
			}

			@Override
			public Routing routing() {
				return configuration.get(Configuration.ROUTING);
			}

		};
	}
}
