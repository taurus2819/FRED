/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.cri.gns.fred.hibernate;

/**
 *
 * @author sitikond
 */
import java.io.Serializable;

import nz.cri.gns.core.NameableAndIdentifiable;

public class Island implements Serializable, Comparable<Island>, NameableAndIdentifiable {

    private static final long serialVersionUID = 20091214L;

    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(Island arg0) {
		return name.compareTo(arg0.getName());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
    public boolean equals(Object obj) {
		if (obj instanceof Island)
			return ((Island)obj).getName().equals(name);
		return false;
	}
	
	@Override
    public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String getDisplayName() {
		return toString();
	}

	@Override
	public String getUniqueIdentifier() {
		return name;
	}

}

