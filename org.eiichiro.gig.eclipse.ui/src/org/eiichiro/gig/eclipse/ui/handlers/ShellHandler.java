/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.eclipse.ui.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eiichiro.gig.eclipse.ui.GigUIPlugin;
import org.eiichiro.gig.shell.Main;

/**
 * {@code ShellHandler}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class ShellHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext(); ) {
			Object object = iterator.next();
			IJavaProject project;
			
			if (object instanceof IProject) {
				project = JavaCore.create((IProject) object);
			} else if (object instanceof IJavaProject) {
				project = (IJavaProject) object;
			} else {
				continue;
			}
			
			final IJavaProject proj = project;
			
			try {
				// Identifying JVM for the project.
				IVMInstall install = JavaRuntime.getVMInstall(proj);
				
				if (install == null) {
					install = JavaRuntime.getDefaultVMInstall();
					
					if (install == null) {
						IStatus status = new Status(IStatus.ERROR, 
								GigUIPlugin.PLUGIN_ID, 
								"Cannot identify JVM for " + project.getElementName() + " project");
						ErrorDialog.openError(HandlerUtil.getActiveShell(event), 
								"Gig", status.getMessage(), status);
						return null;
					}
				}
				
				IVMRunner runner = install.getVMRunner(ILaunchManager.RUN_MODE);
				
				if (runner == null) {
					IStatus status = new Status(IStatus.ERROR, 
							GigUIPlugin.PLUGIN_ID, 
							"Cannot create JVM runner for " + project.getElementName() + " project");
					ErrorDialog.openError(HandlerUtil.getActiveShell(event), 
							"Gig", status.getMessage(), status);
					return null;
				}
				
				// Identifying runtime classpath for the project.
				String[] classpath = JavaRuntime.computeDefaultRuntimeClassPath(project);
				
				if (classpath == null) {
					IStatus status = new Status(IStatus.ERROR, 
							GigUIPlugin.PLUGIN_ID, 
							"Cannot compute runtime classpath for " 
									+ project.getElementName() + " project");
					ErrorDialog.openError(HandlerUtil.getActiveShell(event), 
							"Gig", status.getMessage(), status);
					return null;
				}
				
				VMRunnerConfiguration configuration = new VMRunnerConfiguration(
						Main.class.getName(), classpath);
				configuration.setVMArguments(new String[] {"-Djline.terminal=none"});
				final ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
				runner.run(configuration, launch, new NullProgressMonitor());
				// Attaching the process streams to message console.
				IProcess[] processes = launch.getProcesses();
				
				if (processes.length == 1 && processes[0] != null) {
					IProcess process = processes[0];
					MessageConsole messageConsole = null;
					IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
					String name = project.getElementName() + " - Gig Shell";
					
					for (IConsole console : manager.getConsoles()) {
						if (console instanceof MessageConsole && console.getName().equals(name)) {
							messageConsole = (MessageConsole) console;
						}
					}
					
					if (messageConsole == null) {
						messageConsole = new MessageConsole(name, null);
						manager.addConsoles(new IConsole[] {messageConsole});
					} else {
						messageConsole.clearConsole();
					}
					
					manager.showConsoleView(messageConsole);
					final MessageConsoleStream out = messageConsole.newMessageStream();
					final MessageConsoleStream err = messageConsole.newMessageStream();
					err.setActivateOnWrite(true);
					IConsoleColorProvider provider = new ConsoleColorProvider();
					out.setColor(provider.getColor("org.eclipse.debug.ui.ID_STANDARD_OUTPUT_STREAM"));
					err.setColor(provider.getColor("org.eclipse.debug.ui.ID_STANDARD_ERROR_STREAM"));
					IStreamsProxy proxy = process.getStreamsProxy();
					proxy.getOutputStreamMonitor().addListener(new IStreamListener() {
						
						@Override
						public void streamAppended(String message, IStreamMonitor monitor) {
							out.print(message);
						}

					});
					proxy.getErrorStreamMonitor().addListener(new IStreamListener() {
						
						@Override
						public void streamAppended(String message, IStreamMonitor monitor) {
							err.print(message);
						}
						
					});
				}
				
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR,
						GigUIPlugin.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(HandlerUtil.getActiveShell(event), 
						"Gig", "Failed to start shell in " 
								+ project.getElementName() + " project", status);
			}
		}
		
		return null;
	}

}
