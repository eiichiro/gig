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

import org.eiichiro.jaguar.lifecycle.Constructed;
import org.eiichiro.jaguar.scope.Singleton;
import org.eiichiro.bootleg.Types;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.Text;

/**
 * {@code CoreValueTypeRegistration} is a built-in singleton component to 
 * register Google App Engine specific core value types and the corresponding 
 * {@code Converter}s into the Gig's type system utility on the Web tier.
 * The developers can declare Web endpoint parameters as the Google App Engine 
 * specific core value type in Google App Engine environment by this component.
 * The registration is processed at once while the application's bootstrap.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@Singleton(eager=true)
public class CoreValueTypeRegistration {

	@Constructed
	public void register() {
		Types.addCoreValueType(Text.class, new TextConverter());
		Types.addCoreValueType(Key.class, new KeyConverter());
		Types.addCoreValueType(Category.class, new CategoryConverter());
		Types.addCoreValueType(Email.class, new EmailConverter());
		Types.addCoreValueType(GeoPt.class, new GeoPtConverter());
		Types.addCoreValueType(Link.class, new LinkConverter());
		Types.addCoreValueType(PhoneNumber.class, new PhoneNumberConverter());
		Types.addCoreValueType(PostalAddress.class, new PostalAddressConverter());
		Types.addCoreValueType(Rating.class, new RatingConverter());
	}
	
}
