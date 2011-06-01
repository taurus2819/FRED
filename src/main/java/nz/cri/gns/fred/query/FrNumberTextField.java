package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredTextField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlJoin;

public class FrNumberTextField extends TableRequiredTextField {

	private static final long serialVersionUID = 20061026L;
	
	private BasicTextField metricFrNumberField;
	private BasicTextField imperialFrNumberField;
	private TableRequiredTextField metricSampleFrNumberField;
	private TableRequiredTextField imperialSampleFrNumberField;
	
	private static final String SAMPLE_TABLE = "f.samples";
	private static final HqlJoin SAMPLE_JOIN = new HqlJoin(false, "sample");

	
	public FrNumberTextField(String databaseName, String humanName) {
		super(databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.metricFrNumberField = new BasicTextField("f.frNumber." + databaseName, humanName);
		this.imperialFrNumberField = new BasicTextField("f.yardFrNumber." + databaseName, humanName);
		this.metricSampleFrNumberField = new TableRequiredTextField("sample.frNumber." + databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.imperialSampleFrNumberField = new TableRequiredTextField("sample.yardFrNumber." + databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
	}

	@Override
	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		return "(" + metricFrNumberField.getJoin(op, value) + " OR " + imperialFrNumberField.getJoin(op, value)
			+ " OR " + metricSampleFrNumberField.getJoin(op, value) + " OR " + imperialSampleFrNumberField.getJoin(op, value) + ")";
	}

}
