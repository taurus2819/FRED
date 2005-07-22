package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface DrillType {
	public abstract Integer getDrillTypeId();

	public abstract void setDrillTypeId(Integer drillTypeId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Set getSamples();

	public abstract void setSamples(Set samples);
}