package meta;

public class CyanMetaobjectActionFunction extends CyanMetaobject {

	public CyanMetaobjectActionFunction(String name) {
		this.name = name;
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getPackageOfType() { return null; }

	@Override
	public String getPrototypeOfType() { return null; }

	private final String name;
}
