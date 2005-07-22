package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface LabSection {
	public abstract Integer getLabSectionId();

	public abstract void setLabSectionId(Integer labSectionId);

	public abstract double getLabId();

	public abstract void setLabId(double labId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract String getClosed();

	public abstract void setClosed(String closed);

	public abstract Set getPaleontologies();

	public abstract void setPaleontologies(Set paleontologies);
}