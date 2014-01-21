package org.eiichiro.gig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	private static final String LIB_PATH = "/lib";
	
	private static final String JAR_PATTERN = "*.jar";
	
	// The shared instance
	private static Activator plugin;
	
	private Bundle bundle;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this;
		bundle = context.getBundle();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		bundle = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public String version() {
		org.osgi.framework.Version version = bundle.getVersion();
		return version.getMajor() + "." + version.getMinor() + "." + version.getMicro();
	}
	
	public String description() {
		return "Gig Libraries";
	}
	
	public List<IClasspathEntry> libraries() {
		Enumeration<URL> libraries = bundle.findEntries(LIB_PATH, JAR_PATTERN, true);
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		
		for (URL url : Collections.list(libraries)) {
			try {
				IPath path = Path.fromOSString(FileLocator.toFileURL(url).getPath());
				// TODO: Source attachment.
				entries.add(JavaCore.newLibraryEntry(path, null, null));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return entries;
	}
	
}
