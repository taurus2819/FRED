package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredNumberField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlJoin;

public class FrNumberNumberField extends TableRequiredNumberField {

	private static final long serialVersionUID = 20061026L;
	
	private BasicNumberField metricFrNumberField;
	private BasicNumberField imperialFrNumberField;
	private TableRequiredNumberField metricSampleFrNumberField;
	private TableRequiredNumberField imperialSampleFrNumberField;
	
	private static final String SAMPLE_TABLE = "f.samples";
	private static final HqlJoin SAMPLE_JOIN = new HqlJoin(false, "sample");

	
	public FrNumberNumberField(String databaseName, String humanName) {
		super(databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.metricFrNumberField = new BasicNumberField("f.frNumber." + databaseName, humanName);
		this.imperialFrNumberField = new BasicNumberField("f.yardFrNumber." + databaseName, humanName);
		this.metricSampleFrNumberField = new TableRequiredNumberField("sample.frNumber." + databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.imperialSampleFrNumberField = new TableRequiredNumberField("sample.yardFrNumber." + databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
	}

	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		return "(" + metricFrNumberField.getJoin(op, value) + " OR " + imperialFrNumberField.getJoin(op, value)
			+ " OR " + metricSampleFrNumberField.getJoin(op, value) + " OR " + imperialSampleFrNumberField.getJoin(op, value) + ")";
	}

}
