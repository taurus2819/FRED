package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface Bedding {
	public abstract Integer getBeddingId();

	public abstract void setBeddingId(Integer beddingId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Set getSamplesByPrimaryBeddingId();

	public abstract void setSamplesByPrimaryBeddingId(
			Set samplesByPrimaryBeddingId);

	public abstract Set getSamplesBySecondaryBeddingId();

	public abstract void setSamplesBySecondaryBeddingId(
			Set samplesBySecondaryBeddingId);
}