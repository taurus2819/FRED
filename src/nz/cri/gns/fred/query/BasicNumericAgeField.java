package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.BetweenValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;

public class BasicNumericAgeField extends BasicNumberField {

	private static final long serialVersionUID = 20061026L;
	
	public BasicNumericAgeField(String databaseName, String humanName) {
		super(databaseName, humanName);
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
			return dbName + " " + op.getDatabaseOperator();
		else if (op.equals(Operator.EQUALS))
			return "((" + dbName + ".upperAge.topAge <= " + value + " OR " + dbName + ".lowerAge.topAge <= " + value
				+ ") AND " + dbName + ".lowerAge.baseAge >= " + value + ")";
		else if (op.equals(Operator.GREATER_THAN) || op.equals(Operator.GREATER_THAN_EQUAL))
			return dbName + ".lowerAge.baseAge " + op.getDatabaseOperator() + " " + value;
		else if (op.equals(Operator.LESS_THAN) || op.equals(Operator.LESS_THAN_EQUAL))
			return "(" + dbName + ".upperAge.topAge " + op.getDatabaseOperator() + " " + value + " OR "
				+ dbName + ".lowerAge.topAge " + op.getDatabaseOperator() + " " + value + ")";
		else
			return dbName + " " + op.getDatabaseOperator() + " " + value;
	}

}
