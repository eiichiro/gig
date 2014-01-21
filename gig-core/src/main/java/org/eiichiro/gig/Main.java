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
package org.eiichiro.gig;

import static org.eiichiro.gig.Version.*;

/**
 * {@code Main} is a command line interface to print the information about this 
 * Gig build.
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Main {

	/**
	 * Prints out the information about this Gig build.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		System.out.println("Gig " + MAJOR + "." + MINER + "." + BUILD);
//		System.out.println("Copyright (C) 2012 Eiichiro Uchiumi. All Rights Reserved.");
	}
	
}
