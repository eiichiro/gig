/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
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
package org.eiichiro.gig.shell;

import java.io.File;
import java.io.FileReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.eiichiro.ash.Colors;
import org.eiichiro.ash.Console;
import org.eiichiro.ash.Shell;
import org.eiichiro.reverb.system.Environment;

/**
 * {@code Main}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();
		shell.register(new Quit(shell));
		shell.register(new Exit(shell));
		shell.register(new Help(shell));
		shell.register(new Hint(shell));
		Console console = shell.console();
		console.println(Colors.blue("  ____ _"));
		console.println(Colors.blue(" / ___(_) __ _"));
		console.println(Colors.blue("| |  _| |/ _` |"));
		console.println(Colors.blue("| |_| | | (_| |"));
		console.println(Colors.blue(" \\____|_|\\__, |"));
		console.println(Colors.blue("         |___/"));
		console.println("");
		console.println(Colors.blue("Gig Shell " + Version.MAJOR + "."
				+ Version.MINER + "." + Version.BUILD
				+ " (Experimental Beta) http://gig.eiichiro.org/"));
		console.println("");
		console.println(Colors.blue("Welcome to Gig. Hit the TAB or press 'hint' to display available commands."));
		console.println(Colors.blue("Press 'help command' to display the detailed information for the command."));
		console.println(Colors.blue("Press 'quit' or 'exit' to exit this session. Enjoy!"));
		console.println("");
		String dir = Environment.getProperty("user.dir");
		File pom = new File(dir + File.separator + "pom.xml");
		String prompt = "gig";
		
		if (pom.exists()) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileReader(pom));
			MavenProject project = new MavenProject(model);
			String artifactId = project.getArtifactId();
			String version = project.getVersion();
			prompt = prompt + Colors.yellow(":[" + artifactId + "-" + version + "]");
			File gig = new File(dir + File.pathSeparator + ".gig");
			
			if (!gig.exists()) {
				gig.mkdir();
			}
			
			shell.register(new Scan(shell));
		}
		
		console.prompt(prompt + "> ");
		shell.start();
	}

}
