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
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eiichiro.gig.eclipse.core.compiler.GigJavaProblem.Severity;

/**
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class ComponentValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.eiichiro.gig.eclipse.core.compiler.Validator#validate(org.eclipse.jdt.core.dom.TypeDeclaration, org.eclipse.jdt.core.IType, org.eclipse.jdt.core.dom.CompilationUnit, java.lang.String)
	 */
	@Override
	public List<GigJavaProblem> validate(TypeDeclaration declaration,
			IType type, CompilationUnit unit, String file)
			throws JavaModelException {
		List<GigJavaProblem> problems = new ArrayList<GigJavaProblem>();
		int flags = type.getFlags();
		
		if (!Flags.isAbstract(flags)) {
			if (!Flags.isPublic(flags)) {
				GigJavaProblem problem = new GigJavaProblem(
						Severity.ERROR, "Concrete Component class must be public", file);
				ISourceRange range = type.getNameRange();
				int offset = range.getOffset();
				problem.setSourceStart(offset);
				problem.setSourceEnd(offset + range.getLength() - 1);
				problem.setSourceLineNumber(unit.getLineNumber(offset));
				problems.add(problem);
				return problems;
			}
		}
		
		return problems;
	}

}
