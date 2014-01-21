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

import org.eiichiro.acidhouse.appengine.AppEngineStrongDatastoreSession;
import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.scope.Request;

/**
 * {@code AppEngineStrongDatastoreSessionComponent} is a Gig component which is 
 * representing Acid House {@code AppEngineStrongDatastoreSession}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Name("org.eiichiro.acidhouse.appengine.AppEngineStrongDatastoreSession")
@Strong
@Request
@AppEngine
public class AppEngineStrongDatastoreSessionComponent extends Component<AppEngineStrongDatastoreSession> {

	private AppEngineStrongDatastoreSession session = new AppEngineStrongDatastoreSession();
	
	@Override
	public AppEngineStrongDatastoreSession instance() {
		return session;
	}

}
