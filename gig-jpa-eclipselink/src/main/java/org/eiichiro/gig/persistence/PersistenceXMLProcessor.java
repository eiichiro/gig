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
package org.eiichiro.gig.persistence;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code PersistenceXMLProcessor} is a <code>persistence.xml</code> processor 
 * to get the information of persistence units.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class PersistenceXMLProcessor {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<String> persistenceUnits = new ArrayList<String>();
	
	/**
	 * Parses persistence.xml files on the current ClassLoader's search path 
	 * entries and detects persistence unit declarations from them.
	 */
	public void process() {
		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/persistence.xml");
			XMLInputFactory factory = XMLInputFactory.newInstance();
			List<String> persistenceUnits = new ArrayList<String>();
			
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				XMLStreamReader reader = factory.createXMLStreamReader(url.openStream());
				
				while (reader.hasNext()) {
					if (reader.next() == XMLStreamConstants.START_ELEMENT 
							&& reader.getName().getLocalPart().equals("persistence-unit")) {
						for (int i = 0; i < reader.getAttributeCount(); i++) {
							if (reader.getAttributeLocalName(i).equals("name")) {
								persistenceUnits.add(reader.getAttributeValue(i));
								break;
							}
						}
					}
				}
			}
			
			this.persistenceUnits = persistenceUnits;
		} catch (Exception e) {
			logger.error("Failed to parse persistence.xml", e);
		}
	}
	
	/**
	 * Returns the persistence units declared in <code>persistence.xml</code> 
	 * files.
	 * 
	 * @return The persistence units declared in <code>persistence.xml</code> 
	 * files.
	 */
	public List<String> persistenceUnits() {
		return persistenceUnits;
	}
	
}
