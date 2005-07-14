package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class DefaultTimescale implements Serializable {

    /** identifier field */
    private Integer timescaleId;

    /** full constructor */
    public DefaultTimescale(Integer timescaleId) {
        this.timescaleId = timescaleId;
    }

    /** default constructor */
    public DefaultTimescale() {
    }

    public Integer getTimescaleId() {
        return this.timescaleId;
    }

    public void setTimescaleId(Integer timescaleId) {
        this.timescaleId = timescaleId;
    }
}
