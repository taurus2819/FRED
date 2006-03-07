package nz.cri.gns.fred.hibernate;

public class StratigraphicUnit implements
		nz.cri.gns.fred.model.StratigraphicUnit {

	private Integer id;
	private String name;
	
	public StratigraphicUnit() {
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
