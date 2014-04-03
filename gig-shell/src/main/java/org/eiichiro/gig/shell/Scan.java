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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
		StringBuilder template = new StringBuilder();
		
		try (BufferedReader r = new BufferedReader(
				new InputStreamReader(
						Thread.currentThread().getContextClassLoader().getResourceAsStream(
								Configuration.COMPONENTS_JS + ".template")))) {
			String l = null;
			
			while ((l = r.readLine()) != null) {
				template.append(l + "\n");
			}
		}
		
		File directory = null;
		List<Resource> resources = project.getResources();
		
		if (resources.isEmpty()) {
			directory = new File(base + File.separator + "src" + File.separator
					+ "main" + File.separator + "resources" + File.separator
					+ "META-INF");
		} else {
			directory = new File(resources.get(0).getDirectory() + File.separator + "META-INF");
		}
		
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		File file = new File(directory, "components.js");
		
		if (file.exists()) {
			file.delete();
		}
		
		Iterator<Class<?>> iterator = scan().iterator();
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
		List<URL> paths = new ArrayList<URL>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if (loader instanceof URLClassLoader) {
			paths = Arrays.asList(((URLClassLoader) loader).getURLs());
		} else {
			for (String path : Environment.getProperty("java.class.path").split(File.pathSeparator)) {
				paths.add(new File(path).toURI().toURL());
			}
		}
		
		shell.console().println("Scanning components from classpath [" + paths + "]");
		CtClassClassResolver resolver = new CtClassClassResolver(paths);
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
		Set<Class<?>> components = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		
		for (CtClass ctClass : ctClasses) {
			try {
				components.add(Class.forName(ctClass.getName(), true, Thread.currentThread().getContextClassLoader()));
				shell.console().println("Class [" + ctClass.getName() + "] scaned");
			} catch (Exception e) {}
		}
		
		return components;
	}

}
