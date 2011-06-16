package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class StratigraphicUnit implements Serializable, nz.cri.gns.fred.model.StratigraphicUnit {

    private static final long serialVersionUID = 20050818L;
    
	private Integer id;
	private String name;
	
	public StratigraphicUnit(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public StratigraphicUnit() {
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(nz.cri.gns.fred.model.StratigraphicUnit arg0) {
		return name.compareTo(arg0.getName());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(id);
	}

	public String getDisplayName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof StratigraphicUnit && ((StratigraphicUnit)o).name.equals(name);
	}
	
	@Override
	public int hashCode() {
		return 198 * name.hashCode();
	}
}
