package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BetweenValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlAliasedJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredNumberField;

public class HqlTableRequiredAgeField extends HqlTableRequiredNumberField {

	private static final long serialVersionUID = 20061026L;
	
	public HqlTableRequiredAgeField(String databaseName, String humanName, String[] tables, HqlAliasedJoin[] joins) {
		super(databaseName, humanName, tables, joins);
	}

	/**
	 * Convenience constructor for when there is only one joining table
	 */
	public HqlTableRequiredAgeField(String databaseName, String humanName, String table, HqlAliasedJoin join) {
		super(databaseName, humanName, table, join);
	}
	
	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public HqlTableRequiredAgeField() {
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
			return "((" + dbName + ".upperAgeView.ageStop <= " + value + " OR " + dbName + ".lowerAgeView.ageStop <= " + value
				+ ") AND " + dbName + ".lowerAgeView.ageStart >= " + value + ")";
		else if (op.equals(Operator.GREATER_THAN) || op.equals(Operator.GREATER_THAN_EQUAL))
			return dbName + ".lowerAgeView.ageStart " + op.getDatabaseOperator() + " " + value;
		else if (op.equals(Operator.LESS_THAN) || op.equals(Operator.LESS_THAN_EQUAL))
			return "(" + dbName + ".upperAgeView.ageStop " + op.getDatabaseOperator() + " " + value + " OR "
				+ dbName + ".lowerAgeView.ageStop " + op.getDatabaseOperator() + " " + value + ")";
		else
			return dbName + " " + op.getDatabaseOperator() + " " + value;
		
	}

}
