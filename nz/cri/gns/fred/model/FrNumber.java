package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface FrNumber extends Comparable<FrNumber> {
	public abstract Integer getFrId();

	public abstract void setFrId(Integer frId);

	public abstract String getMapSheet();

	public abstract void setMapSheet(String mapSheet);

	public abstract Integer getSerialNumber();

	public abstract void setSerialNumber(Integer serialNumber);

	public abstract String getRecollectionNumber();

	public abstract void setRecollectionNumber(String recollectionNumber);

	public abstract String getFrnumComments();

	public abstract void setFrnumComments(String frnumComments);

	public abstract String getFrNumber();

	public abstract void setFrNumber(String frNumber);

	public abstract Set getSamples();

	public abstract void setSamples(Set samples);
}