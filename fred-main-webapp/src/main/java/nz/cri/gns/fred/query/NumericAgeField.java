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
		this.type = type;
	}
	
	//Bean methods
	/**
	 * This constructor is implement to satisfy the bean requirements.  Other
	 * code should not use it.
	 */
	public NumericAgeField() {
	}

	@Override
	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		checkValue(op, value);
        // Need some special case logic for the between as the values are mya
        // million years ago. So have an implied negative value
        // we also allow from-to and to-from order.
		if (op.equals(Operator.BETWEEN)) {
			BetweenValue bv = (BetweenValue)value;
			Double left = Double.parseDouble(bv.getLeftValue().toString());
		    Double right = Double.parseDouble(bv.getRightValue().toString());
			Value to;
			Value from;
            if (left > right) {
                from = bv.getLeftValue();
                to = bv.getRightValue();
            } else {
                to = bv.getLeftValue();
                from = bv.getRightValue();
            }
		    return
                "(" + getJoin(Operator.LESS_THAN_EQUAL, from) +
                " AND " + getJoin(Operator.GREATER_THAN_EQUAL, to) + ")";
		} else if (op.equals(Operator.NULL) || op.equals(Operator.NOT_NULL))
			return "sampleStageView.baseAge " + op.getDatabaseOperator();
		else if (op.equals(Operator.EQUALS))
			return "(sampleStageView.topAge = " + value + " AND sampleStageView.baseAge = " + value + " AND sampleStageView.type = '" + type + "')";
		else if (op.equals(Operator.GREATER_THAN) || op.equals(Operator.GREATER_THAN_EQUAL))
			return "(sampleStageView.topAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
		else if (op.equals(Operator.LESS_THAN) || op.equals(Operator.LESS_THAN_EQUAL))
			return "(sampleStageView.baseAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
		else
			return "(sampleStageView.baseAge " + op.getDatabaseOperator() + " " + value + " AND sampleStageView.type = '" + type + "')";
	}

    /*
      Custom validation for Age field between operators
      as conventionally they are specified oldest to newest in units of
      mya (millions of years ago) i.e. 56-35.
      The standard checkValue enforces the property that the left value
      must be less than the right value (so would need to enter as 35-56).
      For the age field we permit either order and allow for that
      in the getJoin method

    */
	public void checkValue(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		if (op.equals(Operator.BETWEEN)) {
			try {
				BetweenValue bv = (BetweenValue)value;
			    Double.parseDouble(bv.getLeftValue().toString());
				Double.parseDouble(bv.getRightValue().toString());
			} catch (Exception e) {
				throw new InvalidValueException(value, this);
			}
		} else {
			super.checkValue(op, value);
		}
	}
}