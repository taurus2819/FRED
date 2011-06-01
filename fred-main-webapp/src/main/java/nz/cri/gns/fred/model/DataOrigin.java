package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface DataOrigin {
	public abstract Integer getOriginId();

	public abstract void setOriginId(Integer originId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract Set<Audit> getAudits();

	public abstract void setAudits(Set<Audit> audits);
}