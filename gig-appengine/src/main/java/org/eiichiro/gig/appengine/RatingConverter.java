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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;

import com.google.appengine.api.datastore.Rating;

/**
 * {@code RatingConverter} is {@code org.apache.commons.beanutils.Converter} 
 * extension to convert {@code String} value to 
 * {@code com.google.appengine.api.datastore.Rating}.
 * This converter is used in Web endpoint parameter construction.
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class RatingConverter extends AbstractConverter {

	/**
	 * Converts the specified value to 
	 * {@code com.google.appengine.api.datastore.Rating}.
	 * 
	 * @see org.apache.commons.beanutils.converters.AbstractConverter#convertToType(java.lang.Class, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		try {
			return new Rating(Integer.parseInt(value.toString()));
		} catch (Exception e) {
			throw new ConversionException(
					"Cannot parse Rating value into integer [" + value + "]");
		}
	}

	/**
	 * Returns {@code com.google.appengine.api.datastore.Rating} as the default 
	 * type this converter handles.
	 * 
	 * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
	 */
	@Override
	protected Class<Rating> getDefaultType() {
		return Rating.class;
	}
	
	/**
	 * Returns {@code com.google.appengine.api.datastore.Rating} value as string 
	 * representation.
	 */
	@Override
    protected String convertToString(Object value) throws Throwable {
		Rating rating = (Rating) value;
		return String.valueOf(rating.getRating());
    }

}
