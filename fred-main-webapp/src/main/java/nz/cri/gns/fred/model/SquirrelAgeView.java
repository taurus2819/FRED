package nz.cri.gns.fred.model;

public interface SquirrelAgeView extends Comparable<SquirrelAgeView> {

    public Integer getSampleId();

    public void setSampleId(Integer sampleId);
    public Double getNarrowBaseAge();

    public void setNarrowBaseAge(Double narrowBaseAge);

    public Double getNarrowTopAge();

    public void setNarrowTopAge(Double narrowTopAge);

    public Double getWideBaseAge();

    public void setWideBaseAge(Double wideBaseAge);

    public Double getWideTopAge();

    public void setWideTopAge(Double wideTopAge);
    
    public boolean isDeterminedValue();
}
