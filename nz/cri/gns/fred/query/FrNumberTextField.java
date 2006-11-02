package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicTextField;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlAliasedJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredTextField;

public class FrNumberTextField extends HqlTableRequiredTextField {

	private static final long serialVersionUID = 20061026L;
	
	private BasicTextField metricFrNumberField;
	private BasicTextField imperialFrNumberField;
	private BasicTextField metricSampleFrNumberField;
	private BasicTextField imperialSampleFrNumberField;
	
	private static final String SAMPLE_TABLE = "Sample";
	private static final HqlAliasedJoin SAMPLE_JOIN = new HqlAliasedJoin("f", "samples", "sample");

	
	public FrNumberTextField(String databaseName, String humanName) {
		super(databaseName, humanName, SAMPLE_TABLE, SAMPLE_JOIN);
		this.metricFrNumberField = new BasicTextField("f.frNumber." + databaseName, humanName);
		this.imperialFrNumberField = new BasicTextField("f.yardFrNumber." + databaseName, humanName);
		this.metricSampleFrNumberField = new BasicTextField("sample.frNumber." + databaseName, humanName);
		this.imperialSampleFrNumberField = new BasicTextField("sample.yardFrNumber." + databaseName, humanName);
	}

	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		return "(" + metricFrNumberField.getJoin(op, value) + " OR " + imperialFrNumberField.getJoin(op, value)
			+ " OR " + metricSampleFrNumberField.getJoin(op, value) + " OR " + imperialSampleFrNumberField.getJoin(op, value) + ")";
	}

}
