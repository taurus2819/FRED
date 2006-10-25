package nz.cri.gns.fred.query;

import nz.cri.gns.db.querybuilder.BasicNumberField;
import nz.cri.gns.db.querybuilder.InvalidOperatorException;
import nz.cri.gns.db.querybuilder.InvalidValueException;
import nz.cri.gns.db.querybuilder.Operator;
import nz.cri.gns.db.querybuilder.Value;
import nz.cri.gns.db.querybuilder.advanced.FilteredFactoredNumberField;
import nz.cri.gns.db.querybuilder.advanced.FilteredNumberField;
import nz.cri.gns.fred.model.FREDConstants;

public class MetricDepthField extends BasicNumberField {

	private static final long serialVersionUID = 20061026L;
	
	private FilteredNumberField metricField;
	private FilteredFactoredNumberField imperialField;
	
	public MetricDepthField(String databaseName, String humanName, String unitField) {
		super(databaseName, humanName);
		this.metricField = new FilteredNumberField(databaseName, humanName, unitField + " = 'm'");
		this.imperialField = new FilteredFactoredNumberField(databaseName, humanName, 1 / FREDConstants.FT_TO_M, unitField + " = 'ft'");
	}

	public String getJoin(Operator op, Value value) throws InvalidOperatorException, InvalidValueException {
		return "(" + metricField.getJoin(op, value) + " OR " + imperialField.getJoin(op, value) + ")";
	}

}
