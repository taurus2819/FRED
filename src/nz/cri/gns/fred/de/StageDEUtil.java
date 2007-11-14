package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.model.AgeView;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.html.Attributes;
import nz.cri.gns.html.select.SelectBox;
import nz.cri.gns.intranet.Template;

public class StageDEUtil {

    static void drawStageInputs(PrintWriter out, Template template, Stage stage, String comboMarkerPrefix, String parameterPrefix, StageUtil stageUtil) throws StorageAccessException, IOException {
        template.loadUntil(out, "{@" + comboMarkerPrefix + "Start}");
		SelectBox<AgeView> selectBox = new SelectBox<AgeView>(stageUtil.getAges());
		Attributes attributes = Attributes.createNameOnlyAttributes(parameterPrefix + "StageStart");
		selectBox.writeBox(attributes, "-- Choose --", null, (stage != null) ? stage.getLowerAgeView() : null, out);
                    
        template.loadUntil(out, "{@" + comboMarkerPrefix + "Stop}");
		attributes = Attributes.createNameOnlyAttributes(parameterPrefix + "StageStop");
		selectBox.writeBox(attributes, "-- Choose --", null, (stage != null) ? stage.getUpperAgeView() : null, out);
    }

    static void addStageSubs(Template template, Stage stage, String label) {
        if (stage != null && stage.getStageLowerMod() != null)
            template.addSub("is" + label + "StartUnc", "Yes");
        if (stage != null && stage.getStageUpperMod() != null)
            template.addSub("is" + label + "StopUnc", "Yes");
    
    }

	public static Stage getStage(HttpServletRequest request, String prefix, Stage existingStage, StageUtil stageUtil, String label) throws DataInputException {
	    String startId = FREDUtil.decodeCombo(request.getParameter(prefix + "StageStart"));
	    String startMod = FREDUtil.decodeCombo(request.getParameter(prefix + "StartMod"));
	    String stopId = FREDUtil.decodeCombo(request.getParameter(prefix + "StageStop"));
	    String stopMod = FREDUtil.decodeCombo(request.getParameter(prefix + "StopMod"));
		if (stageUtil.stageDiffers(existingStage, startId, startMod != null, stopId, stopMod != null)) {
		   	try {
		   		return stageUtil.getStage(startId, startMod != null, stopId, stopMod != null);
		   	} catch (Exception e) {
		   		e.printStackTrace();
		   		throw new DataInputException(label, e.getMessage());
		   	}
		} else {
			return existingStage;
		}
	}

}
