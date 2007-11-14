package nz.cri.gns.fred.hibernate;

import java.io.Serializable;


/** @author Hibernate CodeGenerator */
public class DefaultTimescale implements Serializable {

	private static final long serialVersionUID = 20050818L;
	
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
    

	public boolean equals(Object o) {
		return o instanceof DefaultTimescale && ((DefaultTimescale)o).timescaleId.equals(timescaleId);
	}
	
	public int hashCode() {
		return 362 * timescaleId;
	}
}
