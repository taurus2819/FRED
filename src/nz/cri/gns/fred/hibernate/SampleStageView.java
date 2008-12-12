package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.Stage;

public class SampleStageView implements Serializable, nz.cri.gns.fred.model.SampleStageView {

    private static final long serialVersionUID = 20050818L;

    private String id;
    private String type;
    private Double baseAge;
    private Double topAge;
    private Sample sample;
    private Stage stage;
  
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setBaseAge(Double baseAge) {
		this.baseAge = baseAge;
	}

	public Double getBaseAge() {
		return baseAge;
	}

	public void setTopAge(Double topAge) {
		this.topAge = topAge;
	}

	public Double getTopAge() {
		return topAge;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Sample getSample() {
		return sample;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	public boolean equals(Object o) {
		return o instanceof SampleStageView && ((SampleStageView)o).id.equals(id);
	}

	public int compareTo(nz.cri.gns.fred.model.SampleStageView arg0) {
		if (baseAge.equals(arg0.getBaseAge()))
			return topAge.compareTo(arg0.getTopAge());
		return baseAge.compareTo(arg0.getBaseAge());
	}
}