package io.github.sunshinewzy.shining.api.machine.event;

import io.github.sunshinewzy.shining.api.machine.IMachine;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
public class MachineHandlerList {

	/**
	 * Handler array. This field being an array is the key to this system's
	 * speed.
	 */
	private volatile MachineRegisteredListener[] handlers = null;

	/**
	 * Dynamic handler lists. These are changed using register() and
	 * unregister() and are automatically baked to the handlers array any time
	 * they have changed.
	 */
	private final EnumMap<MachineEventPriority, ArrayList<MachineRegisteredListener>> handlerslots;

	/**
	 * List of all HandlerLists which have been created, for use in bakeAll()
	 */
	private static ArrayList<MachineHandlerList> allLists = new ArrayList<>();

	/**
	 * Bake all handler lists. Best used just after all normal event
	 * registration is complete, ie just after all plugins are loaded if
	 * you're using fevents in a plugin system.
	 */
	public static void bakeAll() {
		synchronized (allLists) {
			for (MachineHandlerList h : allLists) {
				h.bake();
			}
		}
	}

	/**
	 * Unregister all listeners from all handler lists.
	 */
	public static void unregisterAll() {
		synchronized (allLists) {
			for (MachineHandlerList h : allLists) {
				synchronized (h) {
					for (List<MachineRegisteredListener> list : h.handlerslots.values()) {
						list.clear();
					}
					h.handlers = null;
				}
			}
		}
	}

	/**
	 * Unregister a specific machine's listeners from all handler lists.
	 *
	 * @param machine machine to unregister
	 */
	public static void unregisterAll(@NotNull IMachine machine) {
		synchronized (allLists) {
			for (MachineHandlerList h : allLists) {
				h.unregister(machine);
			}
		}
	}

	/**
	 * Unregister a specific listener from all handler lists.
	 *
	 * @param listener listener to unregister
	 */
	public static void unregisterAll(@NotNull MachineListener listener) {
		synchronized (allLists) {
			for (MachineHandlerList h : allLists) {
				h.unregister(listener);
			}
		}
	}

	/**
	 * Create a new handler list and initialize using MachineEventPriority.
	 * <p>
	 * The HandlerList is then added to meta-list for use in bakeAll()
	 */
	public MachineHandlerList() {
		handlerslots = new EnumMap<MachineEventPriority, ArrayList<MachineRegisteredListener>>(MachineEventPriority.class);
		for (MachineEventPriority o : MachineEventPriority.values()) {
			handlerslots.put(o, new ArrayList<MachineRegisteredListener>());
		}
		synchronized (allLists) {
			allLists.add(this);
		}
	}

	/**
	 * Register a new listener in this handler list
	 *
	 * @param listener listener to register
	 */
	public synchronized void register(@NotNull MachineRegisteredListener listener) {
		if (handlerslots.get(listener.getPriority()).contains(listener))
			throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
		handlers = null;
		handlerslots.get(listener.getPriority()).add(listener);
	}

	/**
	 * Register a collection of new listeners in this handler list
	 *
	 * @param listeners listeners to register
	 */
	public void registerAll(@NotNull Collection<MachineRegisteredListener> listeners) {
		for (MachineRegisteredListener listener : listeners) {
			register(listener);
		}
	}

	/**
	 * Remove a listener from a specific order slot
	 *
	 * @param listener listener to remove
	 */
	public synchronized void unregister(@NotNull MachineRegisteredListener listener) {
		if (handlerslots.get(listener.getPriority()).remove(listener)) {
			handlers = null;
		}
	}

	/**
	 * Remove a specific machine's listeners from this handler
	 *
	 * @param machine machine to remove
	 */
	public synchronized void unregister(@NotNull IMachine machine) {
		boolean changed = false;
		for (List<MachineRegisteredListener> list : handlerslots.values()) {
			for (ListIterator<MachineRegisteredListener> i = list.listIterator(); i.hasNext();) {
				if (i.next().getMachine().equals(machine)) {
					i.remove();
					changed = true;
				}
			}
		}
		if (changed) handlers = null;
	}

	/**
	 * Remove a specific listener from this handler
	 *
	 * @param listener listener to remove
	 */
	public synchronized void unregister(@NotNull MachineListener listener) {
		boolean changed = false;
		for (List<MachineRegisteredListener> list : handlerslots.values()) {
			for (ListIterator<MachineRegisteredListener> i = list.listIterator(); i.hasNext();) {
				if (i.next().getListener().equals(listener)) {
					i.remove();
					changed = true;
				}
			}
		}
		if (changed) handlers = null;
	}

	/**
	 * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
	 */
	public synchronized void bake() {
		if (handlers != null) return; // don't re-bake when still valid
		List<MachineRegisteredListener> entries = new ArrayList<>();
		for (Map.Entry<MachineEventPriority, ArrayList<MachineRegisteredListener>> entry : handlerslots.entrySet()) {
			entries.addAll(entry.getValue());
		}
		handlers = entries.toArray(new MachineRegisteredListener[entries.size()]);
	}

	/**
	 * Get the baked registered listeners associated with this handler list
	 *
	 * @return the array of registered listeners
	 */
	@NotNull
	public MachineRegisteredListener[] getRegisteredListeners() {
		MachineRegisteredListener[] handlers;
		while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null
		return handlers;
	}

	/**
	 * Get a specific machine's registered listeners associated with this
	 * handler list
	 *
	 * @param machine the machine to get the listeners of
	 * @return the list of registered listeners
	 */
	@NotNull
	public static ArrayList<MachineRegisteredListener> getRegisteredListeners(@NotNull IMachine machine) {
		ArrayList<MachineRegisteredListener> listeners = new ArrayList<MachineRegisteredListener>();
		synchronized (allLists) {
			for (MachineHandlerList h : allLists) {
				synchronized (h) {
					for (List<MachineRegisteredListener> list : h.handlerslots.values()) {
						for (MachineRegisteredListener listener : list) {
							if (listener.getMachine().equals(machine)) {
								listeners.add(listener);
							}
						}
					}
				}
			}
		}
		return listeners;
	}

	/**
	 * Get a list of all handler lists for every event type
	 *
	 * @return the list of all handler lists
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public static ArrayList<MachineHandlerList> getHandlerLists() {
		synchronized (allLists) {
			return (ArrayList<MachineHandlerList>) allLists.clone();
		}
	}
}
