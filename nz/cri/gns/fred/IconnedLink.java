package nz.cri.gns.fred;

import nz.cri.gns.db.KeyValueObject;

public class IconnedLink extends KeyValueObject {

	private static final long serialVersionUID = 20050818L;
	public String icon;

	public IconnedLink(String url, String icon, String text) {
		super(url, text);
		this.icon = icon;
	}

}
