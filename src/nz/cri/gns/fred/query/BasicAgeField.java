package nz.cri.gns.fred.query;

import java.util.List;

import nz.cri.gns.db.querybuilder.BasicValue;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.PossibleValueField;
import nz.cri.gns.fred.model.Age;

public class BasicAgeField extends PossibleValueField {

	private static final long serialVersionUID = 20061026L;
	
	private List<Age> ages;
	private BasicNumericAgeField numericField;
	
	public BasicAgeField(String humanName, List<Age> values, String type) {
		super("sampleStageView", humanName, values);
		this.ages = values;
		this.numericField = new BasicNumericAgeField(humanName, type);
	}

	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public BasicAgeField() {
	}

	public void checkValue(Operator operator, Value value) throws InvalidOperatorException, InvalidValueException {
		if (operator.equals(Operator.EQUALS) || operator.equals(Operator.GREATER_THAN) || operator.equals(Operator.GREATER_THAN_EQUAL) || operator.equals(Operator.LESS_THAN) || operator.equals(Operator.LESS_THAN_EQUAL)) {
			String key = value.toString();
			for (Age thisValue : ages) {
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
			return "sampleStageView.baseAge " + operator.getDatabaseOperator();
		} else {
			Age age = null;
			String key = value.toString();
			for (Age thisValue : ages) {
				if (thisValue.getUniqueIdentifier().equals(key))
					age = thisValue;
			}
			if (operator.equals(Operator.EQUALS))
				return "(" + numericField.getJoin(Operator.GREATER_THAN, new BasicValue(String.valueOf(age.getTopAge())))
					+ " AND " + numericField.getJoin(Operator.LESS_THAN, new BasicValue(String.valueOf(age.getBaseAge()))) + ")";
			else if (operator.equals(Operator.GREATER_THAN) || operator.equals(Operator.LESS_THAN_EQUAL))
				return numericField.getJoin(operator, new BasicValue(String.valueOf(age.getBaseAge())));
			else
				return numericField.getJoin(operator, new BasicValue(String.valueOf(age.getTopAge())));
		}
	}

}
