package meta;

public class TimeBomb {

	private Thread newThread;

	public TimeBomb(int milliseconds, String metaobjectMethodName,
			CyanMetaobject metaobject) {
		this.newThread = new Thread( () -> {
			try {
				Thread.sleep(milliseconds);
				System.out.println("Metaobject method '" + metaobjectMethodName + "' of metaobject " +
						metaobject.getName() + " of line " + metaobject.getAnnotation().getFirstSymbol().getLineNumber() +
						" of file " + metaobject.getAnnotation().getFirstSymbol().getCompilationUnit().getFullFileNamePath()
						+ " took too long to finish (more than " +
						milliseconds + " milliseconds). " +
						"Since this could result in a nontermination compilation, the compiler was exited. If "
						+ "you expect that this metaobject method take a long time to finish its execution, "
						+ "you can change the timeout "
						+ "settings by changing the value of '" + MetaHelper.timeoutMillisecondsMetaobjectsStr +
						"' using @options which should be attached either to the package or the program");
				System.exit(1);
			}
			catch (InterruptedException e) {
			}
		} );
		newThread.start();
	}

	public void stop() {
		newThread.interrupt();
	}

}
