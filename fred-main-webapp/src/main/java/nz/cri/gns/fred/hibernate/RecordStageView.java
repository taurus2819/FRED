package nz.cri.gns.fred.hibernate;

import java.io.Serializable;

import nz.cri.gns.fred.model.Record;
import nz.cri.gns.fred.model.Stage;

public class RecordStageView implements Serializable, nz.cri.gns.fred.model.RecordStageView {

    private static final long serialVersionUID = 20050818L;

    private String id;
    private String type;
    private Double baseAge;
    private Double topAge;
    private Record record;
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

	public void setRecord(Record record) {
		this.record = record;
	}

	public Record getRecord() {
		return record;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RecordStageView && ((RecordStageView)o).getId().equals(id);
	}

	public int compareTo(nz.cri.gns.fred.model.RecordStageView arg0) {
		if (baseAge.equals(arg0.getBaseAge()))
			return topAge.compareTo(arg0.getTopAge());
		return baseAge.compareTo(arg0.getBaseAge());
	}
}