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
package org.eiichiro.gig.eclipse.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@SuppressWarnings("restriction")
public class NewComponentWrapperWizard extends NewElementWizard {

	private NewComponentWrapperWizardPage page;
	
	public NewComponentWrapperWizard() {
		setWindowTitle("New Component Wrapper");
	}
	
	@Override
	public void addPages() {
		page = new NewComponentWrapperWizardPage();
		page.init(getSelection());
		addPage(page);
	}
	
	@Override
	protected boolean canRunForked() {
		return !page.isEnclosingTypeSelected();
	}
	
	@Override
	public boolean performFinish() {
		boolean res = super.performFinish();
		
		if (res) {
			IResource resource = page.getModifiedResource();
			
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}
		}
		
		return res;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		page.createType(monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	@Override
	public IJavaElement getCreatedElement() {
		return page.getCreatedType();
	}

}
