/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.shell;

import org.eiichiro.ash.Command;
import org.eiichiro.ash.Line;
import org.eiichiro.ash.Shell;
import org.eiichiro.ash.Usage;

/**
 * {@code Quit}
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Quit implements Command {

	private final Shell shell;
	
	public Quit(Shell shell) {
		this.shell = shell;
	}
	
	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#name()
	 */
	@Override
	public String name() {
		return "quit";
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#run(org.eiichiro.ash.Line)
	 */
	@Override
	public void run(Line line) {
		shell.stop();
		shell.console().println("Bye for now.");
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#usage()
	 */
	@Override
	public Usage usage() {
		return new Usage("quit");
	}

}
