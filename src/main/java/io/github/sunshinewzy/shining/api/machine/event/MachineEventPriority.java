package io.github.sunshinewzy.shining.api.machine.event;

public enum MachineEventPriority {
	LOWEST(-64),
	LOW(-32),
	NORMAL(0),
	HIGH(32),
	HIGHEST(64),
	MONITOR(128);
	
	private final int level;
	
	MachineEventPriority(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
