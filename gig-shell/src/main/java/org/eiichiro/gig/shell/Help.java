/*
 * Copyright (C) 2014 Eiichiro Uchiumi. All Rights Reserved.
 */
package org.eiichiro.gig.shell;

import java.util.List;

import org.eiichiro.ash.Command;
import org.eiichiro.ash.Line;
import org.eiichiro.ash.Shell;
import org.eiichiro.ash.Usage;

/**
 * {@code Help}
 * 
 * @author <a href="mailto:eiichiro@eiichiro.org">Eiichiro Uchiumi</a>
 */
public class Help implements Command {

	private final Shell shell;
	
	public Help(Shell shell) {
		this.shell = shell;
	}
	
	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#name()
	 */
	@Override
	public String name() {
		return "help";
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#run(org.eiichiro.ash.Line)
	 */
	@Override
	public void run(Line line) {
		List<String> args = line.args();
		
		if (args.isEmpty()) {
			shell.console().println(usage().toString());
			return;
		}
		
		String command = line.args().get(0);
		Command c = shell.commands().get(command);
		
		if (c == null) {
			shell.console().println("no help topic for '" + command + "'");
			return;
		}
		
		shell.console().println(c.usage().toString());
	}

	/* (non-Javadoc)
	 * @see org.eiichiro.ash.Command#usage()
	 */
	@Override
	public Usage usage() {
		Usage usage = new Usage("help command");
		return usage;
	}

}
