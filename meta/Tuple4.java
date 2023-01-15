package meta;

import java.io.Serializable;

public class Tuple4<F1, F2, F3, F4> implements Serializable {

	public Tuple4(F1 x, F2 f2, F3 f3, F4 f4) {
		this.f1 = x;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
	}
	public F1 f1;
	public F2 f2;
	public F3 f3;
	public F4 f4;
	public F1 getF1() { return f1; }
	public F2 getF2() { return f2; }
	public F3 getF3() { return f3; }
	public F4 getF4() { return f4; }

	private static final long serialVersionUID = 3853398715675404097L;
}

