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
package org.eiichiro.gig;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eiichiro.jaguar.Builtin;
import org.eiichiro.jaguar.Container;
import org.eiichiro.jaguar.Descriptor;
import org.eiichiro.jaguar.inject.Inject;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.lifecycle.Activated;
import org.eiichiro.jaguar.scope.Context;
import org.eiichiro.jaguar.scope.Scope;
import org.eiichiro.jaguar.scope.Singleton;
import org.eiichiro.jaguar.validation.Required;

import com.google.common.base.Preconditions;

/**
 * {@code Namespace} provides a facility to get/set the value corresponding to 
 * the specified key in the <b>current</b> context of the specified scope.
 * This API is designed for shared-multitenancy application.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Builtin
@Name("org.eiichiro.gig.Namespace")
@Singleton
public class Namespace {

	@Inject @Required private Container container;
	
	@Activated
	/** Installs {@code Store} component. */
	void activated() {
		if (!container.installed(Store.class)) {
			container.install(Store.class);
		}
	}
	
	/**
	 * Gets the value corresponding to the specified key in the <b>current</b> 
	 * context of the specified scope.
	 * 
	 * @param scope Scope qualifier.
	 * @param key The key to value.
	 * @return The value corresponding to the specified key in the <b>current</b> 
	 * context of the specified scope.
	 */
	public String get(Class<? extends Annotation> scope, String key) {
		Preconditions.checkNotNull(scope, "Parameter 'scope' must not be [" + scope + "]");
		Preconditions.checkNotNull(key, "Parameter 'key' must not be [" + key + "]");
		Preconditions.checkArgument(scope.isAnnotationPresent(Scope.class), 
				"Parameter 'scope' must be annotated with @Scope");
		return store(scope).get(key);
	}
	
	/**
	 * Sets the specified key and value into the <b>current</b> context of the 
	 * specified scope.
	 * 
	 * @param scope Scope qualifier.
	 * @param key The key to value.
	 * @param value The value to be stored.
	 */
	public void set(Class<? extends Annotation> scope, String key, String value) {
		Preconditions.checkNotNull(scope, "Parameter 'scope' must not be [" + scope + "]");
		Preconditions.checkNotNull(key, "Parameter 'key' must not be [" + key + "]");
		Preconditions.checkNotNull(value, "Parameter 'value' must not be [" + value + "]");
		Preconditions.checkArgument(scope.isAnnotationPresent(Scope.class), 
				"Parameter 'scope' must be annotated with @Scope");
		store(scope).set(key, value);
	}
	
	@SuppressWarnings("unchecked")
	private Store store(Class<? extends Annotation> scope) {
		Map<Class<? extends Annotation>, Descriptor<? extends Context>> contexts = container.contexts();
		Descriptor<? extends Context> descriptor = contexts.get(scope);
		
		if (descriptor == null) {
			Class<? extends Context> context = scope.getAnnotation(Scope.class).value();
			container.install(context);
			descriptor = (Descriptor<? extends Context>) container.components().get(context).get(0);
			contexts.put(scope, descriptor);
		}
		
		Context context = container.component(descriptor);
		Descriptor<Store> d = (Descriptor<Store>) container.components().get(Store.class).get(0);
		Store store = context.get(d);
		
		if (store == null) {
			store = container.component(Store.class);
			context.put(d, store);
		}
		
		return store;
	}
	
	/**
	 * Namespace value store.
	 * 
	 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
	 */
	public static final class Store implements Serializable {
		
		private static final long serialVersionUID = -6951116649694904406L;
		
		private Map<String, String> map = new ConcurrentHashMap<String, String>();
		
		private String get(String key) {
			return map.get(key);
		}
		
		private void set(String key, String value) {
			map.put(key, value);
		}
		
	}
	
}
