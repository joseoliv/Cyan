
package saci;

import java.util.concurrent.TimeUnit;

public class TM {

	static {
		message = new StringBuilder();
	}

	static public void startTime(String nameMeasure) {
		name = nameMeasure;
		start = System.nanoTime();
	}

	static public void endTime(String endMeasure) {
		long elapsedTime = System.nanoTime() - start;
		long diffTime = TimeUnit.MILLISECONDS.convert(elapsedTime,
				TimeUnit.NANOSECONDS);
		// System.out.println(name + " to " + endMeasure + " time = " +
		// diffNano);
		message.append("(" + name + ") to (" + endMeasure + ") time = "
				+ diffTime + " milli-sec\n");
		start = System.nanoTime();
		name = endMeasure;
	}

	static public void printMessage() {
		System.out.println(message);
	}

	static public void firstStartTime() {
		firstStart = System.nanoTime();
	}

	static public void printEndTime() {
		long elapsedTime = System.nanoTime() - firstStart;
		double diffTime = TimeUnit.MILLISECONDS.convert(elapsedTime,
				TimeUnit.NANOSECONDS) / 1000.0;
		System.err.println("total time = " + diffTime + "sec, "
				+ elapsedTime / 1000000000.0);
		System.err.println("Num. threads created = " + Saci.numThreadsMO);
		System.err.println("Average time each compilation = "
				+ diffTime / Saci.numCompilerCalls);
		// @empty :
		// 4.84 5.3 5.14 5.1 4.4 5.3 5.5 5.45 (no @empty) avg = 5.12
		// 7.3 6.8 7.6 7.7 7.8 7.6 8.1 7.1 (@empty, no time limit) avg = 7.5
		// 12.6 13.2 13.6 13.4 (@empty, with time limit)
		// @checkEmpty:
		// 4.6 4.4 5.3 5.03 5.25 5.13 5.7 5.0 => 5.05 (@checkEmpty, no time
		// limit)
		//
	}

	static private String			name;
	static private long				start;
	static private StringBuilder	message;
	static private long				firstStart;
}
