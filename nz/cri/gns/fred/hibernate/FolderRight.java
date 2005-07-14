package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class FolderRight implements Serializable {

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
