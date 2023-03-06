package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class DatumMethod implements Serializable, nz.cri.gns.fred.model.DatumMethod {

    private static final long serialVersionUID = 20050818L;

    /** identifier field */
    private Integer methodId;

    /** nullable persistent field */
    private String method;

    /** nullable persistent field */
    private Double nomAccuracyXY;
    
    /** nullable persistent field */
    private Double nomAccuracyZ;

    /** full constructor */
    public DatumMethod(Integer methodId, String method, Double nomAccuracyXY, Double nomAccuracyZ) {
        this.methodId = methodId;
        this.method = method;
        this.nomAccuracyXY = nomAccuracyXY;
        this.nomAccuracyZ = nomAccuracyZ;
    }

    /** default constructor */
    public DatumMethod() {
    }

	public Integer getMethodId() {
		return methodId;
	}
	
   public void setMethodId(Integer methodId) {
		this.methodId = methodId;
	}

   	public String getMethod() {
        return this.method;
    }

    public void setMethod(String name) {
        this.method = name;
    }

    public void setNomAccuracyXY(Double nomAccuracyXY) {
		this.nomAccuracyXY = nomAccuracyXY;
	}

	public Double getNomAccuracyXY() {
		return nomAccuracyXY;
	}

	public void setNomAccuracyZ(Double nomAccuracyZ) {
		this.nomAccuracyZ = nomAccuracyZ;
	}

	public Double getNomAccuracyZ() {
		return nomAccuracyZ;
	}

	public int compareTo(nz.cri.gns.fred.model.DatumMethod arg0) {
		return method.compareTo(arg0.getMethod());
	}

	public String getUniqueIdentifier() {
		return String.valueOf(methodId);
	}

	public String getDisplayName() {
		return method;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof DatumMethod && ((DatumMethod)o).getMethodId().equals(methodId);
	}
	
	@Override
	public int hashCode() {
		return 856 * methodId;
	}
}
