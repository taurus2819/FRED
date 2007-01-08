package nz.cri.gns.fred.de;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.StageUtil;
import nz.cri.gns.intranet.Template;

public class StageDEUtil {

    static void drawStageInputs(PrintWriter out, Template template, Stage stage, String comboMarkerPrefix, String parameterPrefix) throws SQLException, NamingException {
        template.loadUntil(out, "{@" + comboMarkerPrefix + "Start}");
        
        ComboDescriptor cd = new ComboDescriptor("Age_View", "Ag_ID", "Ag_Name");
        cd.name = parameterPrefix + "StageStart";
        cd.prompt = " -- Choose -- ";
        cd.selected = (stage != null && stage.getLowerAgeView() != null) ? stage.getLowerAgeView().getAgeId().toString() : null;
        cd.orderBy = "Ag_Name";
        FREDUtil.makeDropBox(out, cd);
                    
        template.loadUntil(out, "{@" + comboMarkerPrefix + "Stop}");
        cd.name = parameterPrefix + "StageStop";
        cd.selected = (stage != null && stage.getUpperAgeView() != null) ? stage.getUpperAgeView().getAgeId().toString() : null;
        FREDUtil.makeDropBox(out, cd);
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
