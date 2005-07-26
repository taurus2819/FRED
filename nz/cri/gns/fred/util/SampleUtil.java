package nz.cri.gns.fred.util;

import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.Sample;

/**
 *
 */
public class SampleUtil implements FREDConstants {

	/**
	 * Implements
	 * 	DECODE(F.Feature_Type, 'Outcrop', NULL, DECODE(S.Top_Depth || S.Bottom_Depth || L2.Name, NULL, 'Depth Not Specified',
		DECODE(S.Top_Depth, NULL, NULL, S.Top_Depth || 'm') || DECODE(S.Bottom_Depth, NULL, NULL, ' - ' || S.Bottom_Depth || 'm')
	    || DECODE(L2.Name, NULL, NULL, ' ' || L2.Name))) AS Drillhole_Depth, 

	 * @param sample
	 * @return
	 */
	public static String getDrillHoleDepthDescription(Sample sample) {
		Feature feature = sample.getFeature();
		
		//Not relevant for outcrops
		if (feature.getFeatureType().equals(OUTCROP))
			return null;
		
		if (sample.getTopDepth() == null && sample.getBottomDepth() == null && sample.getDrillType() == null)
			return DEPTH_NOT_SPECIFIED;
		
		String desc = (sample.getTopDepth() != null) ? sample.getTopDepth() + "m" : "";
		if (sample.getBottomDepth() != null) {
			desc += " - " + sample.getBottomDepth() + "m";
		}
		if (sample.getDrillType() != null) {
			desc += " " + sample.getDrillType().getName();
		}
		
		return desc;
	}
	
	public static boolean hasDepthInformation(Sample sample) {
		return sample.getTopDepth() != null || sample.getBottomDepth() != null || sample.getDrillType() != null;
	}
}
