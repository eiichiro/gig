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
package org.eiichiro.gig.eclipse.core.compiler;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;

/**
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class GigJavaProblem extends CategorizedProblem {

	private static final String MARKER_ID = "org.eiichiro.gig.eclipse.core.marker";
	
	private final String message;
	
	private final Severity severity;
	
	private final String file;
	
	private int start;
	
	private int end;
	
	private int number;
	
	static enum Severity {
		
		ERROR, 
		WARNING
		
	}
	
	public GigJavaProblem(Severity severity, String message, String file) {
		this.severity = severity;
		this.message = message;
		this.file = file;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getArguments()
	 */
	@Override
	public String[] getArguments() {
		return new String[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getID()
	 */
	@Override
	public int getID() {
		return IProblem.ExternalProblemFixable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getOriginatingFileName()
	 */
	@Override
	public char[] getOriginatingFileName() {
		return file.toCharArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceEnd()
	 */
	@Override
	public int getSourceEnd() {
		return end;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceLineNumber()
	 */
	@Override
	public int getSourceLineNumber() {
		return number;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceStart()
	 */
	@Override
	public int getSourceStart() {
		return start;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#isError()
	 */
	@Override
	public boolean isError() {
		return severity == Severity.ERROR;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#isWarning()
	 */
	@Override
	public boolean isWarning() {
		return severity == Severity.WARNING;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceEnd(int)
	 */
	@Override
	public void setSourceEnd(int end) {
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceLineNumber(int)
	 */
	@Override
	public void setSourceLineNumber(int number) {
		this.number = number;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceStart(int)
	 */
	@Override
	public void setSourceStart(int start) {
		this.start = start;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getCategoryID()
	 */
	@Override
	public int getCategoryID() {
		return CategorizedProblem.CAT_UNSPECIFIED;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getMarkerType()
	 */
	@Override
	public String getMarkerType() {
		return MARKER_ID;
	}

}
