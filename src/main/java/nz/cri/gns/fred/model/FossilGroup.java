package nz.cri.gns.fred.model;

import java.util.Set;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface FossilGroup extends Comparable<FossilGroup>, NameableAndIdentifiable {
	public abstract Integer getGroupId();
	public abstract void setGroupId(Integer groupId);
	public abstract String getName();
	public void setName(String name);
	public Set<SentTo> getSentTos();
	public void setSentTos(Set<SentTo> sentTos);
}