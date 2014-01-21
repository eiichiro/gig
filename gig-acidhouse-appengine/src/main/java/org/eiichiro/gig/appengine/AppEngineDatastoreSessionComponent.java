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

import org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession;
import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.inject.Default;
import org.eiichiro.jaguar.inject.Name;
import org.eiichiro.jaguar.scope.Request;

/**
 * {@code AppEngineDatastoreSessionComponent} is a Gig component which is 
 * representing Acid House {@code AppEngineDatastoreSession}.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Name("org.eiichiro.acidhouse.appengine.AppEngineDatastoreSession")
@Default
@Request
@AppEngine
public class AppEngineDatastoreSessionComponent extends Component<AppEngineDatastoreSession> {

	private AppEngineDatastoreSession session = new AppEngineDatastoreSession();
	
	@Override
	public AppEngineDatastoreSession instance() {
		return session;
	}

}
