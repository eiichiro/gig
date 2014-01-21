/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.eclipse.ui.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;
import org.eiichiro.gig.eclipse.core.nature.GigNature;
import org.eiichiro.gig.eclipse.ui.GigUIPlugin;

/**
 * {@code EnableNatureHandler}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class EnableNatureHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext(); ) {
			Object object = iterator.next();
			IProject project;
			
			if (object instanceof IProject) {
				project = (IProject) object;
			} else if (object instanceof IJavaProject) {
				project = ((IJavaProject) object).getProject();
			} else {
				continue;
			}
			
			final IProject proj = project;
			WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException,
						InterruptedException {
					// Enabling Gig nature.
					monitor.beginTask("Enabling Gig", 2);
					monitor.subTask("Enabling Gig nature");
					IProjectDescription description = proj.getDescription();
					String[] natures = description.getNatureIds();
					String[] newNatures = new String[natures.length + 1];
					System.arraycopy(natures, 0, newNatures, 0, natures.length);
					newNatures[natures.length] = GigNature.NATURE_ID;
					description.setNatureIds(newNatures);
					proj.setDescription(description, null);
					monitor.worked(1);
					
					// Creating Gig-generated source directory.
					monitor.subTask("Creating Gig-generated source directory");
					IFolder folder = proj.getFolder(".gig_generated");
					
					if (!folder.exists()) {
						folder.create(true, true, monitor);
						IJavaProject javaProject = JavaCore.create(proj);
						IClasspathEntry[] classpath = javaProject.getRawClasspath();
						IClasspathEntry[] entries = new IClasspathEntry[classpath.length + 1];
						System.arraycopy(classpath, 0, entries, 0, classpath.length);
						IClasspathEntry source = JavaCore.newSourceEntry(folder.getFullPath());
						entries[classpath.length] = source;
						javaProject.setRawClasspath(entries, monitor);
					}
					
					monitor.worked(1);
					monitor.done();
				}
				
			};
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			
			try {
				service.run(true, true, operation);
			} catch (InvocationTargetException e) {
				IStatus status = new Status(IStatus.ERROR,
						GigUIPlugin.PLUGIN_ID, e.getTargetException().getMessage(), e.getTargetException());
				GigUIPlugin.getDefault().getLog().log(status);
				ErrorDialog.openError(HandlerUtil.getActiveShell(event),
						"Gig", "Failed to enable Gig in "
								+ project.getName() + " project", status);
			} catch (InterruptedException e) {}
		}
		
		return null;
	}

}
