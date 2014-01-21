/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.eclipse.core.jdt;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * {@code GigClasspathContainerInitializer}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GigClasspathContainerInitializer extends
		ClasspathContainerInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
	 */
	@Override
	public void initialize(IPath path, IJavaProject project) throws CoreException {
		// TODO Auto-generated method stub
		IClasspathContainer container = new GigClasspathContainer(path);
		JavaCore.setClasspathContainer(path, new IJavaProject[] { project },
				new IClasspathContainer[] { container }, null);
	}

}
