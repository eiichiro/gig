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

import java.math.BigDecimal;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractConverter;

import com.google.appengine.api.datastore.GeoPt;

/**
 * {@code GeoPtConverter} is {@code org.apache.commons.beanutils.Converter} 
 * extension to convert {@code String} value to 
 * {@code com.google.appengine.api.datastore.GeoPt}.
 * This converter is used in Web endpoint parameter construction.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GeoPtConverter extends AbstractConverter {

	/**
	 * Converts the specified value to 
	 * {@code com.google.appengine.api.datastore.GeoPt}.
	 * 
	 * @see org.apache.commons.beanutils.converters.AbstractConverter#convertToType(java.lang.Class, java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		String[] strings = value.toString().split(",");
		
		if (strings.length != 2) {
			throw new ConversionException(
					"GeoPt 'value' must be able to be splitted into 2 float values "
							+ "by ',' (latitude,longitude)");
		}
		
		try {
			float latitude = new BigDecimal(strings[0].trim()).floatValue();
			float longitude = new BigDecimal(strings[1].trim()).floatValue();
			return new GeoPt(latitude, longitude);
		} catch (Exception e) {
			throw new ConversionException(
					"Cannot parse GeoPt value into 2 float values: "
							+ "latitude [" + strings[0].trim()
							+ "], longitude [" + strings[1].trim() + "]");
		}
	}

	/**
	 * Returns {@code com.google.appengine.api.datastore.GeoPt} as the default 
	 * type this converter handles.
	 * 
	 * @see org.apache.commons.beanutils.converters.AbstractConverter#getDefaultType()
	 */
	@Override
	protected Class<GeoPt> getDefaultType() {
		return GeoPt.class;
	}

	/**
	 * Returns {@code com.google.appengine.api.datastore.GeoPt} value as string 
	 * representation.
	 */
	@Override
    protected String convertToString(Object value) throws Throwable {
		GeoPt geoPt = (GeoPt) value;
		return new BigDecimal(geoPt.getLatitude()) + "," + new BigDecimal(geoPt.getLongitude());
    }
	
}
