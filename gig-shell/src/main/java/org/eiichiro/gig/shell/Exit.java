/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.shell;

import org.eiichiro.ash.Command;
import org.eiichiro.ash.Line;
import org.eiichiro.ash.Shell;
import org.eiichiro.ash.Usage;

/**
 * {@code Exit}
 * 
 * @author <a href="mailto:mail@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Exit implements Command {

	private final Shell shell;
	
	public Exit(Shell shell) {
		this.shell = shell;
	}
	
	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#name()
	 */
	@Override
	public String name() {
		return "exit";
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
		return new Usage("exit");
	}

}
