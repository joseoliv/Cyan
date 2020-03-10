package cyanruntime;

import cyan.lang._CyException;

public class ExceptionContainer__ extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExceptionContainer__(_CyException elem) {
		this.elem = elem;
	}
	public _CyException elem;
}