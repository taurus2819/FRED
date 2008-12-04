package nz.cri.gns.fred.export;

import java.util.Set;

import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Paleontology;
import nz.cri.gns.fred.model.Stage;

public class ListDerivedAge extends SimpleAgeRange {

	
	private Type type;

	public ListDerivedAge(Set<Paleontology> lists, Type type) {
		super();
		process(lists, type);
		this.type = type;
	}

	private void process(Set<Paleontology> lists, Type type) {
		Age lower = null, upper = null;
		boolean lowerCertain = true, upperCertain = true;
		double lowerNumeric = Double.NaN, upperNumeric = Double.NaN;
		for (Paleontology pal : lists) {
			if (pal.getStage() != null) {
				Stage stage = pal.getStage();
				Age thisLower = stage.getLowerAge();
				Age thisUpper = stage.getUpperAge();
				//Lower bound
				if (
					thisLower != null && (
						Double.isNaN(lowerNumeric) ||
						(type == Type.MINIMUM && thisLower.getBaseAge() != null && thisLower.getBaseAge().doubleValue() < lowerNumeric) ||
						(type == Type.MAXIMUM && thisLower.getBaseAge() != null && thisLower.getBaseAge().doubleValue() > lowerNumeric)
					)
				) {
					lower = thisLower;
					lowerCertain = !"?".equals(stage.getStageLowerMod());
					lowerNumeric = thisLower.getBaseAge();
				}
				//Upper bound
				if (
					thisUpper != null && (
						Double.isNaN(upperNumeric) ||
						(type == Type.MINIMUM && thisUpper.getTopAge() != null && thisUpper.getTopAge().doubleValue() > lowerNumeric) ||
						(type == Type.MAXIMUM && thisUpper.getTopAge() != null && thisUpper.getTopAge().doubleValue() < lowerNumeric)
					)
				) {
					upper = thisUpper;
					upperCertain = !"?".equals(stage.getStageUpperMod());
					upperNumeric = thisUpper.getTopAge();
				}
			}
		}
		this.lower = lower;
		this.lowerCertain = lowerCertain;
		this.upper = upper;
		this.upperCertain = upperCertain;
		this.comment = null;
	}

	public static enum Type {
		MINIMUM, MAXIMUM;
	}

	public String getAgeRangeType() {
		return (type == Type.MINIMUM ? "Minimum" : "Maximum") + " overlap (list)";
	}
}
