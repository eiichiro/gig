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

import static org.eiichiro.jaguar.Jaguar.component;

import java.io.File;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import javassist.CtClass;

import org.eiichiro.bootleg.CtClassClassResolver;
import org.eiichiro.bootleg.DefaultConfiguration;
import org.eiichiro.bootleg.Invoke;
import org.eiichiro.bootleg.Pipeline;
import org.eiichiro.bootleg.WebContext;
import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.jaguar.Builtin;
import org.eiichiro.jaguar.ClasspathScanner;
import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.Container;
import org.eiichiro.jaguar.Jaguar;
import org.eiichiro.jaguar.Stereotype;
import org.eiichiro.jaguar.deployment.Deployment;
import org.eiichiro.jaguar.inject.Binding;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.lifecycle.Activated;
import org.eiichiro.jaguar.lifecycle.Constructed;
import org.eiichiro.jaguar.scope.Application;
import org.eiichiro.jaguar.scope.Scope;
import org.eiichiro.jaguar.scope.Singleton;
import org.eiichiro.reverb.lang.ClassResolver.Matcher;
import org.eiichiro.reverb.lang.UncheckedException;
import org.eiichiro.reverb.time.Stopwatch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * {@code Configuration} is a built-in place holder for Gig application's 
 * configuration settings.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Builtin
@Name("org.eiichiro.gig.Configuration")
@Singleton(eager=true)
public class Configuration {

	public static final String ENDPOINTS = "endpoints";
	
	public static final String PIPELINE = "pipeline";
	
	public static final String REQUEST_TYPES = "requestTypes";
	
	public static final String RESPONSE_TYPES = "responseTypes";
	
	public static final String ROUTING = "routing";
	
	public static final String DEPLOYMENT = "deployment";
	
	public static final String COMPONENTS = "components";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<String, Object> values = new HashMap<String, Object>();
	
	public static final String COMPONENTS_JS = "META-INF/components.js";
	
	public static final String ROUTING_JS = "META-INF/routing.js";
	
	public static final String SETTINGS_JS = "META-INF/settings.js";
	
	private ContextFactory factory = new ContextFactory();
	
	private ScriptableObject scope;
	
	private org.eiichiro.bootleg.Configuration configuration;
	
	/**
	 * Sets up Mozilla Rhino's script scope. If the application is running on a 
	 * Servlet container, this method instantiates and initializes the Bootleg's 
	 * default configuration internally.
	 */
	@Constructed
	public void constructed() {
		Context context = factory.enterContext();
		
		try {
			scope = new ImporterTopLevel(context);
		} finally {
			Context.exit();
		}
		
		Container container = Jaguar.component(Container.class);
		Object store = container.component(container.contexts().get(Application.class)).store();
		
		if (store instanceof ServletContext) {
			org.eiichiro.bootleg.Configuration configuration = new DefaultConfiguration();
			configuration.init((ServletContext) store);
			this.configuration = configuration;
		}
	}
	
	/**
	 * Attempts to load pre-defined configuration files.
	 */
	@Activated
	public void activated() {
		logger.info("Importing core packages ['org.eiichiro.gig', 'org.eiichiro.bootleg', 'org.eiichiro.jaguar', 'org.eiichiro.jaguar.deployment'] into JavaScript context");
		Context context = factory.enterContext();
		
		try {
			context.evaluateString(scope, "importPackage(Packages.org.eiichiro.gig)", Configuration.class.getSimpleName(), 146, null);
			context.evaluateString(scope, "importPackage(Packages.org.eiichiro.bootleg)", Configuration.class.getSimpleName(), 147, null);
			context.evaluateString(scope, "importPackage(Packages.org.eiichiro.jaguar)", Configuration.class.getSimpleName(), 148, null);
			context.evaluateString(scope, "importPackage(Packages.org.eiichiro.jaguar.deployment)", Configuration.class.getSimpleName(), 149, null);
		} finally {
			Context.exit();
		}
		
		load(COMPONENTS_JS);
		load(ROUTING_JS);
		load(SETTINGS_JS);
	}
	
	/**
	 * Loads the specified configuration file written in JavaScript.
	 * 
	 * @param file Configuration file written in JavaScript.
	 */
	public void load(String file) {
		Context context = factory.enterContext();
		
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(file);
			
			if (url == null) {
				logger.debug("Configuration [" + file + "] does not exist");
				return;
			}
			
			File f = new File(url.getPath());
			
			if (f.exists()) {
				logger.info("Loading configuration [" + file + "]");
				context.evaluateReader(scope, new FileReader(f), file.substring(file.lastIndexOf("/") + 1), 1, null);
			}
			
		} catch (Exception e) {
			logger.error("Failed to load configuration [" + file + "]", e);
			throw new UncheckedException(e);
		} finally {
			Context.exit();
		}
	}
	
	/**
	 * Returns the configuration setting corresponding to the specified key.
	 * 
	 * @param key The key to the configuration setting.
	 * @return The configuration setting corresponding to the specified key.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Preconditions.checkArgument(key != null && !key.isEmpty(), "Parameter 'key' must not be [" + key + "]");
		Context context = factory.enterContext();
		
		try {
			if (values.containsKey(key)) {
				Object value = values.get(key);
				return (T) ((value instanceof Function) 
						? ((Function) value).call(context, scope, (Scriptable) value, null)
						: value);
			}
			
			synchronized (values) {
				if (values.containsKey(key)) {
					Object value = values.get(key);
					return (T) ((value instanceof Function) 
							? ((Function) value).call(context, scope, (Scriptable) value, null)
							: value);
				} else if (key.equals(ENDPOINTS)) {
					Object endpoints = scope.get(ENDPOINTS);
					
					if (endpoints == null && configuration != null) {
						endpoints = Collections2.filter((Set<Class<?>>) get(COMPONENTS), new Predicate<Class<?>>() {

							@Override
							public boolean apply(Class<?> clazz) {
								return clazz.isAnnotationPresent(Endpoint.class);
							}
							
						});
					}
					
					values.put(ENDPOINTS, endpoints);
					return (T) ((endpoints instanceof Function) 
							? ((Function) endpoints).call(context, scope, (Scriptable) endpoints, null)
							: endpoints);
				} else if (key.equals(PIPELINE)) {
					Object pipeline = scope.get(PIPELINE);
					
					if (pipeline == null && configuration != null) {
						Pipeline<WebContext> p = configuration.pipeline();
						p.remove(2);
						p.set(2, "JaguarInvoke", new Invoke() {
							
							@Override
							protected <E> E instantiate(Class<E> endpoint) throws Exception {
								return component(endpoint);
							}
							
						});
						pipeline = p;
					}
					
					values.put(PIPELINE, pipeline);
					return (T) ((pipeline instanceof Function) 
							? ((Function) pipeline).call(context, scope, (Scriptable) pipeline, null)
							: pipeline);
				} else if (key.equals(REQUEST_TYPES)) {
					Object requestTypes = scope.get(REQUEST_TYPES);
					
					if (requestTypes == null && configuration != null) {
						requestTypes = configuration.requestTypes();
					}
					
					values.put(REQUEST_TYPES, requestTypes);
					return (T) ((requestTypes instanceof Function) 
							? ((Function) requestTypes).call(context, scope, (Scriptable) requestTypes, null)
							: requestTypes);
				} else if (key.equals(RESPONSE_TYPES)) {
					Object responseTypes = scope.get(RESPONSE_TYPES);
					
					if (responseTypes == null && configuration != null) {
						responseTypes = configuration.responseTypes();
					}
					
					values.put(RESPONSE_TYPES, responseTypes);
					return (T) ((responseTypes instanceof Function) 
							? ((Function) responseTypes).call(context, scope, (Scriptable) responseTypes, null)
							: responseTypes);
				} else if (key.equals(ROUTING)) {
					Object routing = scope.get(ROUTING);
					
					if (routing == null && configuration != null) {
						routing = configuration.routing();
					}
					
					values.put(ROUTING, routing);
					return (T) ((routing instanceof Function) 
							? ((Function) routing).call(context, scope, (Scriptable) routing, null)
							: routing);
				} else if (key.equals(DEPLOYMENT)) {
					Object deployment = scope.get(DEPLOYMENT);
					values.put(DEPLOYMENT, deployment);
					return (T) ((deployment instanceof Function) 
							? ((Function) deployment).call(context, scope, (Scriptable) deployment, null)
							: deployment);
				} else if (key.equals(COMPONENTS)) {
					Object components = scope.get(COMPONENTS);
					
					if (components == null) {
						components = components();
					}
					
					values.put(COMPONENTS, components);
					return (T) ((components instanceof Function) 
							? ((Function) components).call(context, scope, (Scriptable) components, null)
							: components);
				} else {
					Object value = scope.get(key);
					
					if (value == null) {
						return null;
					}
					
					values.put(key, value);
					return (T) ((value instanceof Function) 
							? ((Function) value).call(context, scope, (Scriptable) value, null)
							: value);
				}
			}
			
		} finally {
			Context.exit();
		}
	}
	
	/**
	 * Sets the specified configuration setting with the specified key.
	 * 
	 * @param key The key to the configuration setting.
	 * @param value The value of the configuration setting.
	 */
	public <T> void set(String key, T value) {
		Preconditions.checkArgument(key != null && !key.isEmpty(), "Parameter 'key' must not be [" + key + "]");
		values.put(key, value);
		scope.put(key, scope, value);
	}
	
	private Collection<Class<?>> components() {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			ClasspathScanner scanner = new ClasspathScanner();
			CtClassClassResolver resolver = new CtClassClassResolver(scanner.paths());
			logger.info("Scanning components from runtime classpath [" + resolver.paths() + "]");
			Set<CtClass> ctClasses = resolver.resolve(new Matcher<CtClass>() {

				@Override
				public boolean matches(CtClass ctClass) {
					try {
						int modifiers = ctClass.getModifiers();
						
						if (!Modifier.isPublic(modifiers)) {
							ctClass.detach();
							return false;
						}
						
						if (ctClass.isInterface()
								|| Modifier.isAbstract(modifiers)
								|| ctClass.hasAnnotation(Builtin.class)) {
							ctClass.detach();
							return false;
						}
						
						if (ctClass.getAnnotation(Endpoint.class) != null) {
							return true;
						}
						
						CtClass superclass = ctClass.getSuperclass();
						
						while (superclass != null) {
							if (superclass.getName().equals(Component.class.getName())) {
								return true;
							}
							
							superclass = superclass.getSuperclass();
						}
						
						for (Object object : ctClass.getAnnotations()) {
							Class<? extends Annotation> annotationType = ((Annotation) object).annotationType();
							
							if (annotationType.isAnnotationPresent(Stereotype.class)
									|| annotationType.isAnnotationPresent(Deployment.class)
									|| annotationType.isAnnotationPresent(Binding.class)
									|| annotationType.isAnnotationPresent(Scope.class)) {
								return true;
							}
						}
						
					} catch (Exception e) {
						logger.debug("Failed to scan component", e);
					}
					
					ctClass.detach();
					return false;
				}
				
			});
			Set<Class<?>> components = new HashSet<>();
			
			for (CtClass ctClass : ctClasses) {
				try {
					components.add(Class.forName(ctClass.getName(), true, Thread.currentThread().getContextClassLoader()));
				} catch (Exception e) {
					logger.warn("Failed to load component class", e);
				}
			}
			
			stopwatch.stop();
			long time = stopwatch.getTime();
			logger.debug("Scanning components took [" + time + "] ms; If that's too slow, "
					+ "generate components configuration [" + COMPONENTS_JS 
					+ "] up-front with 'scan' CLI tool to make the spinup faster");
			return components;
		} catch (Exception e) {
			logger.error("Failed to load component classes", e);
			throw new UncheckedException(e);
		}
	}
	
}
