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
		long diffTime = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
		//System.out.println(name + " to " + endMeasure + " time = " + diffNano);
		message.append("(" + name + ") to (" + endMeasure + ") time = " + diffTime + " milli-sec\n");
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
		double diffTime = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS)/1000.0;
		System.out.println("total time = " + diffTime + "sec, " + elapsedTime/1000000000.0);
	}

	static private String name;
	static private long start;
	static private StringBuilder message;
	static private long firstStart;
}
