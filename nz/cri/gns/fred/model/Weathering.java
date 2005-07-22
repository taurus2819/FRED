package nz.cri.gns.fred.model;

import java.util.Set;

/**
 *
 */
public interface Weathering {
	public abstract Integer getWeatheringId();

	public abstract void setWeatheringId(Integer weatheringId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getCode();

	public abstract void setCode(String code);

	public abstract Set getSamples();

	public abstract void setSamples(Set samples);
}