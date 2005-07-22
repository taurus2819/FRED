package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface RockColour {
	public abstract Integer getColourId();

	public abstract void setColourId(Integer colourId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Set getSamplesBySecondaryColourId();

	public abstract void setSamplesBySecondaryColourId(
			Set samplesBySecondaryColourId);

	public abstract Set getSamplesByPrimaryColourId();

	public abstract void setSamplesByPrimaryColourId(
			Set samplesByPrimaryColourId);
}