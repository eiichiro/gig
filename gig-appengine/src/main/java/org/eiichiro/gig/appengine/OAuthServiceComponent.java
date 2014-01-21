/*
 * Copyright (C) 2011 Eiichiro Uchiumi. All Rights Reserved.
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

import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.inject.Name;

import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;

/**
 * {@code OAuthServiceComponent} is a Gig component which is representing 
 * Google App Engine {@code OAuthService}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Name("com.google.appengine.api.oauth.OAuthService")
@AppEngine
public class OAuthServiceComponent extends Component<OAuthService> {

	private OAuthService oAuthService = OAuthServiceFactory.getOAuthService();
	
	/** Returns {@code OAuthService}. */
	@Override
	public OAuthService instance() {
		return oAuthService;
	}

}
