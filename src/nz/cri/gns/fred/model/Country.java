package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface Country extends Comparable<Country>, NameableAndIdentifiable {
	public String getCountryCode();
	public void setCountryCode(String countryCode);
	public String getName();
	public void setName(String name);
	public void setDialCode(Integer dialCode);
	public Integer getDialCode();
}
