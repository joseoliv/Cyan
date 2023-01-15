package meta;

import java.io.Serializable;

public class Tuple2<F1, F2> implements Serializable {

	private static final long serialVersionUID = -7977060822683173348L;
	public F1 f1;
	public F2 f2;

	public F1 getF1() { return f1; }
	public F2 getF2() { return f2; }

	public Tuple2(F1 x, F2 f2) {
	    this.f1 = x;
	    this.f2 = f2;
	}
}
