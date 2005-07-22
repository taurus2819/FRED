package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface GrainSize {
	public abstract Integer getGrainSizeId();

	public abstract void setGrainSizeId(Integer grainSizeId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Set getSamplesByPrimaryGrainsizeId();

	public abstract void setSamplesByPrimaryGrainsizeId(
			Set samplesByPrimaryGrainsizeId);

	public abstract Set getSamplesBySecondaryGrainsizeId();

	public abstract void setSamplesBySecondaryGrainsizeId(
			Set samplesBySecondaryGrainsizeId);
}