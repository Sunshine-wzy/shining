package io.github.sunshinewzy.shining.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ShiningEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	
	
	public ShiningEvent() {
		super(!Bukkit.isPrimaryThread());
	}
	
	
	@NotNull
	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		if (allowCancelled()) {
			cancelled = value;
		} else throw new IllegalStateException("Unsupported");
	}
	
	public boolean allowCancelled() {
		return true;
	}
	
	public boolean call() {
		Bukkit.getPluginManager().callEvent(this);
		return !isCancelled();
	}
	

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
