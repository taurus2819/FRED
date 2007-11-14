package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class Country implements Serializable, nz.cri.gns.fred.model.Country {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private String countryCode;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private Integer dialCode;

    /** full constructor */
    public Country(String countryCode, String name, Integer dialCode) {
        this.countryCode = countryCode;
        this.name = name;
        this.dialCode = dialCode;
    }

    /** default constructor */
    public Country() {
    }

	public String getCountryCode() {
		return countryCode;
	}
	
   public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

   	public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDialCode(Integer dialCode) {
		this.dialCode = dialCode;
	}

	public Integer getDialCode() {
		return dialCode;
	}

	public int compareTo(nz.cri.gns.fred.model.Country arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return countryCode;
	}

	public String getDisplayName() {
		return name;
	}
	
	public boolean equals(Object o) {
		return o instanceof Country && ((Country)o).countryCode.equals(countryCode);
	}
	
	public int hashCode() {
		return countryCode.hashCode();
	}
}
