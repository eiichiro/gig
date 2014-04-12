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
package org.eiichiro.gig.eclipse.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
@SuppressWarnings("restriction")
public class NewComponentWizardPage extends NewTypeWizardPage {

	private SelectionButtonDialogFieldGroup stubsFieldGroup;
	
	public NewComponentWizardPage() {
		super(true, "NewComponentWizardPage");
		setTitle("Component");
		setDescription("Create a new Component class.");
		String[] stubTexts = {
				"&Constructors from superclass", 
				"In&herited abstract methods"
		};
		stubsFieldGroup = new SelectionButtonDialogFieldGroup(SWT.CHECK, stubTexts, 1);
		stubsFieldGroup.setLabelText("Which method stubs would you like to create?");
	}
	
	public void init(IStructuredSelection selection) {
		IJavaElement element = getInitialJavaElement(selection);
		initContainerPage(element);
		initTypePage(element);
		updateStatus(getStatusList());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		int nColumns = 4;
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);
		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);
		createMethodStubSelectionControls(composite, nColumns);
		createCommentControls(composite, nColumns);
		enableCommentControl(true);
		setControl(composite);
		Dialog.applyDialogFont(composite);
		// TODO: Help.
	}
	
	private void createMethodStubSelectionControls(Composite composite,
			int nColumns) {
		Control labelControl = stubsFieldGroup.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);
		DialogField.createEmptySpace(composite);
		Control buttonGroup = stubsFieldGroup.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);
	}
	
	@Override
	protected String constructCUContent(ICompilationUnit cu,
			String typeContent, String lineDelimiter) throws CoreException {
		String name = cu.getType(Util.getNameWithoutJavaLikeExtension(cu.getElementName())).getFullyQualifiedName();
		return super.constructCUContent(cu, "@Name(\"" + name + "\")" + lineDelimiter + typeContent, lineDelimiter);
	}
	
	@Override
	protected void createTypeMembers(IType newType, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		imports.addImport("org.eiichiro.jaguar.inject.Name");
		boolean doConstr = isCreateConstructors();
		boolean doInherited = isCreateInherited();
		createInheritedMethods(newType, doConstr, doInherited, imports, new SubProgressMonitor(monitor, 1));
	}
	
	private boolean isCreateConstructors() {
		return stubsFieldGroup.isSelected(0);
	}

	private boolean isCreateInherited() {
		return stubsFieldGroup.isSelected(1);
	}

	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		updateStatus(getStatusList());
	}
	
	private IStatus[] getStatusList() {
		IStatus[] status = {
				fContainerStatus, 
				(isEnclosingTypeSelected()) ? fEnclosingTypeStatus : fPackageStatus, 
				fTypeNameStatus, 
				fModifierStatus, 
				fSuperClassStatus, 
				fSuperInterfacesStatus
		};
		return status;
	}

}
