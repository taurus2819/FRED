package nz.cri.gns.fred.hibernate;

import java.io.Serializable;
import java.util.Set;


/** @author Hibernate CodeGenerator */
public class FossilGroup implements Serializable {

    /** identifier field */
    private Integer groupId;

    /** persistent field */
    private String name;

    /** persistent field */
    private Set sentTos;

    /** full constructor */
    public FossilGroup(Integer groupId, String name, Set sentTos) {
        this.groupId = groupId;
        this.name = name;
        this.sentTos = sentTos;
    }

    /** default constructor */
    public FossilGroup() {
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getSentTos() {
        return this.sentTos;
    }

    public void setSentTos(Set sentTos) {
        this.sentTos = sentTos;
    }

    public String toString() {
        return name;
    }


}
