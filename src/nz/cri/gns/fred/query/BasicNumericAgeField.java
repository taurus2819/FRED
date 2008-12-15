package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BetweenValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;

public class BasicNumericAgeField extends BasicNumberField {

	private static final long serialVersionUID = 20061026L;
	
	private String type;
	
	public BasicNumericAgeField(String humanName, String type) {
		super("sampleStageView", humanName);
		this.type = type;
	}

	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public BasicNumericAgeField() {
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