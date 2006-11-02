package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlAliasedJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredNumberField;

public class FrNumberNumberField extends HqlTableRequiredNumberField {

	private static final long serialVersionUID = 20061026L;
	
	private BasicNumberField metricFrNumberField;
	private BasicNumberField imperialFrNumberField;
	private BasicNumberField metricSampleFrNumberField;
	private BasicNumberField imperialSampleFrNumberField;
	
	private static final String SAMPLE_TABLE = "Sample";
	private static final HqlAliasedJoin SAMPLE_JOIN = new HqlAliasedJoin("f", "samples", "sample");

	
	public FrNumberNumberField(String databaseName, String humanName) {
		super(databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.metricFrNumberField = new BasicNumberField("f.frNumber." + databaseName, humanName);
		this.imperialFrNumberField = new BasicNumberField("f.yardFrNumber." + databaseName, humanName);
		this.metricSampleFrNumberField = new BasicNumberField("sample.frNumber." + databaseName, humanName);
		this.imperialSampleFrNumberField = new BasicNumberField("sample.yardFrNumber." + databaseName, humanName);
	}

	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		return "(" + metricFrNumberField.getJoin(op, value) + " OR " + imperialFrNumberField.getJoin(op, value)
			+ " OR " + metricSampleFrNumberField.getJoin(op, value) + " OR " + imperialSampleFrNumberField.getJoin(op, value) + ")";
	}

}
