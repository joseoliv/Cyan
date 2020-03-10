package saci;

import java.io.Serializable;

public class Tuple7<F1, F2, F3, F4, F5, F6, F7> implements Serializable {

	public Tuple7(F1 x, F2 f2, F3 f3, F4 f4, F5 f5, F6 f6, F7 f7) {
		this.f1 = x;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
		this.f5 = f5;
		this.f6 = f6;
		this.f7 = f7;
	}

	private static final long serialVersionUID = -4642151143604931478L;
	public F1 f1;
	public F2 f2;
	public F3 f3;
	public F4 f4;
	public F5 f5;
	public F6 f6;
	public F7 f7;
}

