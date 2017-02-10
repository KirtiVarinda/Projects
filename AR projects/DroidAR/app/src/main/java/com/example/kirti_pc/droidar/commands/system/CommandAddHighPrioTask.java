package com.example.kirti_pc.droidar.commands.system;

import system.TaskManager;

import commands.Command;

public class CommandAddHighPrioTask extends Command {

	private Command myCommandToAdd;

	public CommandAddHighPrioTask(Command commandToAdd) {
		myCommandToAdd = commandToAdd;
	}

	@Override
	public boolean execute() {
		TaskManager.getInstance().addHighPrioTask(myCommandToAdd);
		return true;
	}

}
