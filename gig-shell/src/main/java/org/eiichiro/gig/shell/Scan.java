/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javassist.CtClass;
import jline.internal.InputStreamReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.eiichiro.ash.Command;
import org.eiichiro.ash.Line;
import org.eiichiro.ash.Shell;
import org.eiichiro.ash.Usage;
import org.eiichiro.bootleg.CtClassClassResolver;
import org.eiichiro.bootleg.annotation.Endpoint;
import org.eiichiro.gig.Configuration;
import org.eiichiro.jaguar.Builtin;
import org.eiichiro.jaguar.ClasspathScanner;
import org.eiichiro.jaguar.Component;
import org.eiichiro.jaguar.Stereotype;
import org.eiichiro.jaguar.deployment.Deployment;
import org.eiichiro.jaguar.inject.Binding;
import org.eiichiro.jaguar.scope.Scope;
import org.eiichiro.reverb.lang.ClassResolver.Matcher;
import org.eiichiro.reverb.system.Environment;

/**
 * {@code Scan}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Scan implements Command {

	private final Shell shell;
	
	public Scan(Shell shell) {
		this.shell = shell;
	}
	
	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#name()
	 */
	@Override
	public String name() {
		return "scan";
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#run(org.eiichiro.ash.Line)
	 */
	@Override
	public void run(Line line) throws Exception {
		String base = Environment.getProperty("user.dir");
		File pom = new File(base + File.separator + "pom.xml");
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pom));
		MavenProject project = new MavenProject(model);
		List<URL> urls = new ArrayList<>();
		
		for (String element : project.getCompileClasspathElements()) {
			urls.add(new File(element).toURI().toURL());
		}
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		StringBuilder template = new StringBuilder();
		
		try (BufferedReader r = new BufferedReader(
				new InputStreamReader(loader.getResourceAsStream(Configuration.COMPONENTS_JS + ".template")))) {
			String l = null;
			
			while ((l = r.readLine()) != null) {
				template.append(l);
			}
		}
		
		Resource resource = project.getResources().get(0);
		File directory = new File(resource.getDirectory() + File.separator + "META-INF");
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		File file = new File(directory, "components.js");
		
		if (file.exists()) {
			file.delete();
		}
		
		Iterator<Class<?>> iterator = null;
		
		try {
			Thread.currentThread().setContextClassLoader(
					URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), loader));
			iterator = scan().iterator();
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
		
		StringBuilder components = new StringBuilder();
		shell.console().print("Writing [" + Configuration.COMPONENTS_JS + "]...");
		
		while (iterator.hasNext()) {
			components.append("\tPackages." + iterator.next().getName());
			
			if (iterator.hasNext()) {
				components.append(", \n");
			}
			
			shell.console().print(".");
		}
		
		String js = template.toString().replace("${components}", components);
		
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(js);
		}
		
		shell.console().println(" done");
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#usage()
	 */
	@Override
	public Usage usage() {
		return new Usage("scan");
	}
	
	private Collection<Class<?>> scan() throws Exception {
		ClasspathScanner scanner = new ClasspathScanner();
		CtClassClassResolver resolver = new CtClassClassResolver(scanner.paths());
		Set<CtClass> ctClasses = resolver.resolve(new Matcher<CtClass>() {

			@Override
			public boolean matches(CtClass ctClass) {
				try {
					int modifiers = ctClass.getModifiers();
					
					if (!Modifier.isPublic(modifiers)) {
						ctClass.detach();
						return false;
					}
					
					if (ctClass.isInterface()
							|| Modifier.isAbstract(modifiers)
							|| ctClass.hasAnnotation(Builtin.class)) {
						ctClass.detach();
						return false;
					}
					
					if (ctClass.getAnnotation(Endpoint.class) != null) {
						return true;
					}
					
					CtClass superclass = ctClass.getSuperclass();
					
					while (superclass != null) {
						if (superclass.getName().equals(Component.class.getName())) {
							return true;
						}
						
						superclass = superclass.getSuperclass();
					}
					
					for (Object object : ctClass.getAnnotations()) {
						Class<? extends Annotation> annotationType = ((Annotation) object).annotationType();
						
						if (annotationType.isAnnotationPresent(Stereotype.class)
								|| annotationType.isAnnotationPresent(Deployment.class)
								|| annotationType.isAnnotationPresent(Binding.class)
								|| annotationType.isAnnotationPresent(Scope.class)) {
							return true;
						}
					}
					
				} catch (Exception e) {}
				
				ctClass.detach();
				return false;
			}
			
		});
		Set<Class<?>> components = new HashSet<>();
		
		for (CtClass ctClass : ctClasses) {
			try {
				components.add(Class.forName(ctClass.getName(), true, Thread.currentThread().getContextClassLoader()));
				shell.console().println("Class [" + ctClass.getName() + "] ");
			} catch (Exception e) {}
		}
		
		return components;
	}

}
