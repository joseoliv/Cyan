package meta;

import java.io.Serializable;

public class Tuple3<F1, F2, F3> implements Serializable {

	public Tuple3(F1 x, F2 f2, F3 f3) {
		this.f1 = x;
		this.f2 = f2;
		this.f3 = f3;
	}

	public F1 f1;
	public F2 f2;
	public F3 f3;
	public F1 getF1() { return f1; }
	public F2 getF2() { return f2; }
	public F3 getF3() { return f3; }

	private static final long serialVersionUID = 7251603854828825250L;
}

