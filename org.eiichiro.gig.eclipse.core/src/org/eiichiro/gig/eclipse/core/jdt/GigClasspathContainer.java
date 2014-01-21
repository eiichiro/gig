/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.eclipse.core.jdt;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eiichiro.gig.Activator;

/**
 * {@code GigClasspathContainer}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GigClasspathContainer implements IClasspathContainer {

	private final IPath path;
	
	public GigClasspathContainer(IPath path) {
		this.path = path;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		// TODO Auto-generated method stub
		List<IClasspathEntry> libraries = Activator.getDefault().libraries();
		return libraries.toArray(new IClasspathEntry[libraries.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return Activator.getDefault().description();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	@Override
	public int getKind() {
		// TODO Auto-generated method stub
		return IClasspathContainer.K_APPLICATION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return path;
	}

}
