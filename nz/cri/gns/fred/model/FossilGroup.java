package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface FossilGroup {
	public abstract Integer getGroupId();

	public abstract void setGroupId(Integer groupId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Set getSentTos();

	public abstract void setSentTos(Set sentTos);
}