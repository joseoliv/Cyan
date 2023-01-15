package meta;

import java.io.Serializable;

public class Tuple5<F1, F2, F3, F4, F5> implements Serializable {

	public Tuple5(F1 x, F2 f2, F3 f3, F4 f4, F5 f5) {
		this.f1 = x;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
		this.f5 = f5;
	}

	private static final long serialVersionUID = -5023739118017072063L;
	public final F1 f1;
	public final F2 f2;
	public final F3 f3;
	public final F4 f4;
	public final F5 f5;

	@SuppressWarnings("unchecked")
	@Override
	public Tuple5<F1, F2, F3, F4, F5> clone() {
		try {
			return (Tuple5<F1, F2, F3, F4, F5>) super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}
}

