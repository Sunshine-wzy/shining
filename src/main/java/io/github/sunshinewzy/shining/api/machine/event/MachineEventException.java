package io.github.sunshinewzy.shining.api.machine.event;

public class MachineEventException extends Exception {
	private static final long serialVersionUID = 3532808232324183999L;
	private final Throwable cause;

	/**
	 * Constructs a new MachineEventException based on the given Exception
	 *
	 * @param throwable Exception that triggered this Exception
	 */
	public MachineEventException(Throwable throwable) {
		cause = throwable;
	}

	/**
	 * Constructs a new MachineEventException
	 */
	public MachineEventException() {
		cause = null;
	}

	/**
	 * Constructs a new MachineEventException with the given message
	 *
	 * @param cause The exception that caused this
	 * @param message The message
	 */
	public MachineEventException(Throwable cause, String message) {
		super(message);
		this.cause = cause;
	}

	/**
	 * Constructs a new MachineEventException with the given message
	 *
	 * @param message The message
	 */
	public MachineEventException(String message) {
		super(message);
		cause = null;
	}

	/**
	 * If applicable, returns the Exception that triggered this Exception
	 *
	 * @return Inner exception, or null if one does not exist
	 */
	@Override
	public Throwable getCause() {
		return cause;
	}
}