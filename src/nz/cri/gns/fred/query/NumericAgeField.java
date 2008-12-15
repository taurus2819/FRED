package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BetweenValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.TableRequiredNumberField;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlJoin;

public class NumericAgeField extends TableRequiredNumberField {

	private static final long serialVersionUID = 20061026L;
	
	private String type;
	
	public NumericAgeField(String humanName, String[] tables, HqlJoin[] joins, String type) {
		super("sampleStageView", humanName, tables, joins);
		this.type = type;
	}

	/**
	 * Convenience constructor for when there is only one joining table
	 */
	public NumericAgeField(String humanName, String table, HqlJoin join, String type) {
		super("sampleStageView", humanName, table, join);
	}
	
	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public NumericAgeField() {
	}

	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		checkValue(op, value);
		if (op.equals(Operator.BETWEEN)) {
			Value leftValue = ((BetweenValue)value).getLeftValue();
			Value rightValue = ((BetweenValue)value).getRightValue();
			return "(" + getJoin(Operator.EQUALS, leftValue) + " OR " + getJoin(Operator.EQUALS, rightValue) + ")";
		} else if (op.equals(Operator.NULL) || op.equals(Operator.NOT_NULL))
			return "sampleStageView.baseAge " + op.getDatabaseOperator();
		else if (op.equals(Operator.EQUALS))
			return "(sampleStageView.topAge <= " + value + " AND sampleStageView.baseAge >= " + value + " AND sampleStageView.type = '" + type + "')";
		else if (op.equals(Operator.GREATER_THAN) || op.equals(Operator.GREATER_THAN_EQUAL))
			return "(sampleStageView.baseAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
		else if (op.equals(Operator.LESS_THAN) || op.equals(Operator.LESS_THAN_EQUAL))
			return "(sampleStageView.topAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
		else
			return "(sampleStageView.baseAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
	}

}