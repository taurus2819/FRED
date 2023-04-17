package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

public class FolderRight implements Serializable, nz.cri.gns.fred.model.FolderRight {

	private static final long serialVersionUID = 20050818L;
	
    private Integer rightId;
    private String name;
    private Integer code;

    public Integer getRightId() {
        return this.rightId;
    }

    public void setRightId(Integer rightId) {
        this.rightId = rightId;
    }

    public String getRightDescription() {
        return this.name;
    }

    public void setRightDescription(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

	@Override
	public boolean equals(Object o) {
		return o instanceof FolderRight && ((FolderRight)o).getRightId().equals(rightId);
	}
	
	@Override
	public int hashCode() {
		return 936 * rightId;
	}

	public int compareTo(nz.cri.gns.fred.model.FolderRight arg0) {
		return code.compareTo(arg0.getCode());
	}
}
