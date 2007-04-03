package nz.cri.gns.fred.export;

import java.util.Set;

import nz.cri.gns.fred.model.AgeView;
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
		AgeView lower = null, upper = null;
		boolean lowerCertain = true, upperCertain = true;
		double lowerNumeric = Double.NaN, upperNumeric = Double.NaN;
		for (Paleontology pal : lists) {
			if (pal.getStage() != null) {
				Stage stage = pal.getStage();
				AgeView thisLower = stage.getLowerAgeView();
				AgeView thisUpper = stage.getUpperAgeView();
				//Lower bound
				if (
					thisLower != null && (
						Double.isNaN(lowerNumeric) ||
						(type == Type.MINIMUM && thisLower.getAgeStart() != null && thisLower.getAgeStart().doubleValue() < lowerNumeric) ||
						(type == Type.MAXIMUM && thisLower.getAgeStart() != null && thisLower.getAgeStart().doubleValue() > lowerNumeric)
					)
				) {
					lower = thisLower;
					lowerCertain = !"?".equals(stage.getStageLowerMod());
					lowerNumeric = thisLower.getAgeStart();
				}
				//Upper bound
				if (
					thisUpper != null && (
						Double.isNaN(upperNumeric) ||
						(type == Type.MINIMUM && thisUpper.getAgeStop() != null && thisUpper.getAgeStop().doubleValue() > lowerNumeric) ||
						(type == Type.MAXIMUM && thisUpper.getAgeStop() != null && thisUpper.getAgeStop().doubleValue() < lowerNumeric)
					)
				) {
					upper = thisUpper;
					upperCertain = !"?".equals(stage.getStageUpperMod());
					upperNumeric = thisUpper.getAgeStop();
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
