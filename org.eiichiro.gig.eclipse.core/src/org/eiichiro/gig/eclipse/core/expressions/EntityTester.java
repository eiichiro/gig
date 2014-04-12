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
package org.eiichiro.gig.eclipse.core.expressions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class EntityTester extends PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object selection, String property, Object[] args, Object expected) {
		if (property.equals("isEntity")) {
			if (selection instanceof ICompilationUnit) {
				ICompilationUnit source = (ICompilationUnit) selection;
				IType type = source.findPrimaryType();
				ASTParser parser = ASTParser.newParser(AST.JLS4);
				parser.setResolveBindings(true);
				parser.setSource(source);
				CompilationUnit unit = (CompilationUnit) parser.createAST(null);
				
				for (Object object : unit.types()) {
					if (object instanceof TypeDeclaration) {
						TypeDeclaration declaration = (TypeDeclaration) object;
						ITypeBinding binding = declaration.resolveBinding();
						
						if (type.getFullyQualifiedName().equals(binding.getQualifiedName())) {
							for (IAnnotationBinding annotation : binding.getAnnotations()) {
								String name = annotation.getAnnotationType().getQualifiedName();
								if (name.equals("org.eiichiro.acidhouse.Entity") || name.equals("javax.persistence.Entity")) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}

}
