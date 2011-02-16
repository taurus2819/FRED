package nz.cri.gns.fred.model;

import nz.cri.gns.core.NameableAndIdentifiable;

public interface StratigraphicUnit extends Comparable<StratigraphicUnit>, NameableAndIdentifiable {
	public Integer getId();
	public String getName();
}
