package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class FolderRight implements Serializable, nz.cri.gns.fred.model.FolderRight {

	private static final long serialVersionUID = 20050818L;
	
   /** identifier field */
    private Integer rightId;

    /** persistent field */
    private String name;

    /** persistent field */
    private String code;

    /** full constructor */
    public FolderRight(Integer rightId, String name, String code) {
        this.rightId = rightId;
        this.name = name;
        this.code = code;
    }

    /** default constructor */
    public FolderRight() {
    }

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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
