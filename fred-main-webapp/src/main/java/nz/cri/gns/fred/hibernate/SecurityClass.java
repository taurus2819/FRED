package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class SecurityClass implements Serializable {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer classId;

    /** persistent field */
    private String name;

    /** full constructor */
    public SecurityClass(Integer classId, String name) {
        this.classId = classId;
        this.name = name;
    }

    /** default constructor */
    public SecurityClass() {
    }

    public Integer getClassId() {
        return this.classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
	public String toString() {
        return name;
    }

	@Override
	public boolean equals(Object o) {
		return o instanceof SecurityClass && ((SecurityClass)o).getClassId().equals(classId);
	}
	
	@Override
	public int hashCode() {
		return 935 * classId;
	}

}
