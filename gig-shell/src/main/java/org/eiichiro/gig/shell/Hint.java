/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.shell;

import java.io.IOException;
import java.io.PrintWriter;

import org.eiichiro.ash.Command;
import org.eiichiro.ash.Line;
import org.eiichiro.ash.Shell;
import org.eiichiro.ash.Usage;

/**
 * {@code Hint}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Hint implements Command {

	private final Shell shell;
	
	public Hint(Shell shell) {
		this.shell = shell;
	}
	
	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#name()
	 */
	@Override
	public String name() {
		return "hint";
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#run(org.eiichiro.ash.Line)
	 */
	@Override
	public void run(Line line) {
		try {
			shell.console().reader().printColumns(shell.commands().keySet());
		} catch (IOException e) {
			e.printStackTrace(new PrintWriter(shell.console().reader().getOutput()));
		}
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#usage()
	 */
	@Override
	public Usage usage() {
		return new Usage("hint");
	}

}
