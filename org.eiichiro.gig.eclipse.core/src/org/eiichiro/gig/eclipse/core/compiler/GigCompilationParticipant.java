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
package org.eiichiro.gig.eclipse.core.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eiichiro.gig.eclipse.core.nature.GigNature;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GigCompilationParticipant extends CompilationParticipant {

	@Override
	public boolean isActive(IJavaProject project) {
		try {
			return project.getProject().hasNature(GigNature.NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}
	
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		for (BuildContext context : files) {
			IFile file = context.getFile();
			List<GigJavaProblem> problems = validate(JavaCore.createCompilationUnitFrom(file), file.getName());
			context.recordNewProblems(problems.toArray(new GigJavaProblem[problems.size()]));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<GigJavaProblem> validate(ICompilationUnit source, String file) {
		List<GigJavaProblem> problems = new ArrayList<GigJavaProblem>();
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setResolveBindings(true);
		parser.setSource(source);
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		List<AbstractTypeDeclaration> declarations = unit.types();
		
		try {
			Map<String, IType> types = new HashMap<String, IType>();
			
			for (IType type : source.getTypes()) {
				types.put(type.getFullyQualifiedName(), type);
			}
			
			for (AbstractTypeDeclaration typeDeclaration : declarations) {
				if (typeDeclaration instanceof TypeDeclaration) {
					TypeDeclaration declaration = (TypeDeclaration) typeDeclaration;
					IType type = types.get(typeDeclaration.resolveBinding().getQualifiedName());
					Validator validator = null;
					
					if (isEndpoint(declaration, type)) {
						validator = new EndpointValidator();
					} else if (isInterceptor(declaration, type)) {
						validator = new InterceptorValidator();
					} else if (isComponentWrapper(declaration, type)) {
						validator = new ComponentWrapperValidator();
					} else if (isComponent(declaration, type)) {
						validator = new ComponentValidator();
					} else {
						continue;
					}
					
					problems.addAll(validator.validate(declaration, type, unit, file));
				}
			}
			
		} catch (JavaModelException e) {}
		
		return problems;
	}
	
	private boolean isEndpoint(TypeDeclaration declaration, IType type) {
		for (IAnnotationBinding binding : declaration.resolveBinding().getAnnotations()) {
			if (binding.getAnnotationType().getQualifiedName().equals("org.eiichiro.bootleg.annotation.Endpoint")) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isInterceptor(TypeDeclaration declaration, IType type) {
		for (IAnnotationBinding binding : declaration.resolveBinding().getAnnotations()) {
			if (binding.getAnnotationType().getQualifiedName().equals("org.eiichiro.jaguar.interceptor.Interceptor")) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isComponentWrapper(TypeDeclaration declaration, IType type) throws JavaModelException {
		ITypeHierarchy hierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
		
		for (IType supertype : hierarchy.getAllClasses()) {
			if (supertype.getFullyQualifiedName().equals("org.eiichiro.jaguar.Component")) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isComponent(TypeDeclaration declaration, IType type) {
		for (IAnnotationBinding binding : declaration.resolveBinding().getAnnotations()) {
			for (IAnnotationBinding b : binding.getAnnotations()) {
				String name = b.getAnnotationType().getQualifiedName();
				
				if (name.equals("org.eiichiro.jaguar.Stereotype")
						|| name.equals("org.eiichiro.jaguar.deployment.Deployment")
						|| name.equals("org.eiichiro.jaguar.inject.Binding")
						|| name.equals("org.eiichiro.jaguar.scope.Scope")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
