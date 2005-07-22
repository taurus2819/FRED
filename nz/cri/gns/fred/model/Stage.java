package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface Stage {
	public abstract Integer getStageId();

	public abstract void setStageId(Integer stageId);

	public abstract Integer getStageLowerId();

	public abstract void setStageLowerId(Integer stageLowerId);

	public abstract String getStageLowerMod();

	public abstract void setStageLowerMod(String stageLowerMod);

	public abstract Integer getStageUpperId();

	public abstract void setStageUpperId(Integer stageUpperId);

	public abstract String getStageUpperMod();

	public abstract void setStageUpperMod(String stageUpperMod);

	public abstract String getStageMod();

	public abstract void setStageMod(String stageMod);

	public abstract Set getSamplesByKnownStageId();

	public abstract void setSamplesByKnownStageId(Set samplesByKnownStageId);

	public abstract Set getSamplesByInferredStageId();

	public abstract void setSamplesByInferredStageId(
			Set samplesByInferredStageId);

	public abstract Set getPaleontologies();

	public abstract void setPaleontologies(Set paleontologies);

	public abstract Set getAdoptions();

	public abstract void setAdoptions(Set adoptions);
}