package saci;

import java.io.Serializable;

public class TupleTwo<F1, F2> implements Serializable {

	public TupleTwo(F1 x, F2 f2) {
		this.f1 = x;
		this.f2 = f2;
	}
	public final F1 f1;
	public final F2 f2;

	private static final long serialVersionUID = 8570222547226458119L;
}

