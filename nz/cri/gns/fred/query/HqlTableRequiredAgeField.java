package nz.cri.gns.fred.query;

import java.util.List;

import nz.cri.gns.db.querybuilder.BasicValue;
import nz.cri.gns.db.querybuilder.BetweenValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlAliasedJoin;
import nz.cri.gns.db.querybuilder.advanced.hql.HqlTableRequiredPossibleValueField;
import nz.cri.gns.fred.model.AgeView;

public class HqlTableRequiredAgeField extends HqlTableRequiredPossibleValueField {

	private static final long serialVersionUID = 20061026L;
	
	private List<AgeView> ages;
	private HqlTableRequiredNumericAgeField numericField;
	
	public HqlTableRequiredAgeField(String databaseName, String humanName, List<AgeView> values, String[] tables, HqlAliasedJoin[] joins) {
		super(databaseName, humanName, values, tables, joins);
		this.ages = values;
		this.numericField = new HqlTableRequiredNumericAgeField(databaseName, humanName, tables, joins);
	}

	/**
	 * Convenience constructor for when there is only one joining table
	 */
	public HqlTableRequiredAgeField(String databaseName, String humanName, List<AgeView> values, String table, HqlAliasedJoin join) {
		super(databaseName, humanName, values, table, join);
		this.ages = values;
		this.numericField = new HqlTableRequiredNumericAgeField(databaseName, humanName, table, join);
	}
	
	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public HqlTableRequiredAgeField() {
	}

	public void checkValue(Operator operator, Value value) throws InvalidOperatorException, InvalidValueException {
		if (operator.equals(Operator.EQUALS) || operator.equals(Operator.GREATER_THAN) || operator.equals(Operator.GREATER_THAN_EQUAL) || operator.equals(Operator.LESS_THAN) || operator.equals(Operator.LESS_THAN_EQUAL)) {
			String key = value.toString();
			for (AgeView thisValue : ages) {
				if (thisValue.getUniqueIdentifier().equals(key))
					return;
			}
			throw new InvalidValueException(value, this);
		} else if (!operator.equals(Operator.NULL) && !operator.equals(Operator.NOT_NULL)){
			throw new InvalidOperatorException(operator, this);
		}
	}
	
	public String getJoin(Operator operator, Value value) throws InvalidOperatorException, InvalidValueException {
		checkValue(operator, value);
		if (operator.equals(Operator.NULL) || operator.equals(Operator.NOT_NULL)) {
			return dbName + " " + operator.getDatabaseOperator();
		} else {
			AgeView age = null;
			String key = value.toString();
			for (AgeView thisValue : ages) {
				if (thisValue.getUniqueIdentifier().equals(key))
					age = thisValue;
			}
			if (operator.equals(Operator.EQUALS))
				return numericField.getJoin(Operator.BETWEEN, new BetweenValue(age.getAgeStop(), age.getAgeStart()));
			else if (operator.equals(Operator.GREATER_THAN) || operator.equals(Operator.LESS_THAN_EQUAL))
				return numericField.getJoin(operator, new BasicValue(String.valueOf(age.getAgeStart())));
			else
				return numericField.getJoin(operator, new BasicValue(String.valueOf(age.getAgeStop())));
		}
	}

}
