package fr.groupez.api.zcore;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class ElapsedTime {

	private long start;
	private long end;
	private final String name;

	/**
	 * Constructs an ElapsedTime object with a specified name.
	 *
	 * @param name the name of the timer.
	 */
	public ElapsedTime(String name) {
		super();
		this.name = name;
	}

	/**
	 * Starts the timer by recording the current time in nanoseconds.
	 */
	public void start() {
		this.start = System.nanoTime();
	}

	/**
	 * Stops the timer by recording the current time in nanoseconds.
	 */
	public void end() {
		this.end = System.nanoTime();
	}

	/**
	 * Gets the start time recorded by the timer.
	 *
	 * @return the start time in nanoseconds.
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Gets the end time recorded by the timer.
	 *
	 * @return the end time in nanoseconds.
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * Calculates the elapsed time between the start and end points.
	 *
	 * @return the elapsed time in nanoseconds.
	 */
	public long getElapsedTime() {
		return this.end - this.start;
	}

	/**
	 * Stops the timer and prints the elapsed time to the console if debugging is enabled.
	 */
	public void endDisplay() {
		this.end();
		if (Configuration.get(MainConfiguration.class).isDebug()) {
			System.out.println("[ElapsedTime] " + name + " -> " + this.format(this.getElapsedTime()));
		}
	}

	public String format(long l) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(l);
	}
}
