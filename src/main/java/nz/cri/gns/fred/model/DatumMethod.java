package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface DatumMethod extends Comparable<DatumMethod>, NameableAndIdentifiable {
	public Integer getMethodId();
   public void setMethodId(Integer methodId);
   	public String getMethod();
    public void setMethod(String name);
    public void setNomAccuracyXY(Double nomAccuracyXY) ;
	public Double getNomAccuracyXY();
	public void setNomAccuracyZ(Double nomAccuracyZ);
	public Double getNomAccuracyZ();
}
