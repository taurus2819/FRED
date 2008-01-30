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
    
    public static void addStage2(Template template, Stage stage, String prefix) {
    	if (stage != null) {
    		String start = stage.getLowerAgeView().getAgeName() + "(" + stage.getLowerAgeView().getAgeAbbrev() + ")";
    		if (stage.getStageLowerMod() != null)
    			start = start + "?";
    		template.addSub(prefix + "StageStart", start);
    		if (stage.getUpperAgeView() != null) {
        		String stop = stage.getUpperAgeView().getAgeName() + "(" + stage.getUpperAgeView().getAgeAbbrev() + ")";
        		if (stage.getStageUpperMod() != null)
        			stop = start + "?";
        		template.addSub(prefix + "StageStop", stop);    			
    		}
    	}    	
    }

	public static Stage getStage(HttpServletRequest request, String prefix, Stage existingStage, StageUtil stageUtil, String label) throws DataInputException {
	    String startId = null;
	    String startMod = null;
	    String stopId = null;
	    String stopMod = null;
		
	    if (request.getParameter(prefix + "StageStart") != null) {
		    startId = FREDUtil.decodeCombo(request.getParameter(prefix + "StageStart"));
		    startMod = FREDUtil.decodeCombo(request.getParameter(prefix + "StartMod"));
		    stopId = FREDUtil.decodeCombo(request.getParameter(prefix + "StageStop"));
		    stopMod = FREDUtil.decodeCombo(request.getParameter(prefix + "StopMod"));
	    } else if (request.getParameter(prefix + "StageStart2") != null) try {
	    	String start = request.getParameter(prefix + "StageStart2");
	    	if (start.indexOf("?") >= 0) {
	    		startMod = "?";
	    		start = start.replace("?", "");
	    	}
	    	start = start.substring(0, start.indexOf("(")).trim();
	    	AgeView ageStart = stageUtil.getAgeViewByName(start);
	    	if (ageStart == null)
	    		throw new DataInputException(label, "Stage start not valid: " + request.getParameter(prefix + "StageStart2"));
	    	startId = ageStart.getAgeId().toString();
	    	
	    	if (request.getParameter(prefix + "StageStop2") != null && request.getParameter(prefix + "StageStop2").length() > 0) {
		    	String stop = request.getParameter(prefix + "StageStop2");
		    	if (stop.indexOf("?") >= 0) {
		    		stopMod = "?";
		    		stop = stop.replace("?", "");
		    	}
		    	stop = stop.substring(0, stop.indexOf("(")).trim();
		    	AgeView ageStop = stageUtil.getAgeViewByName(stop);
		    	if (ageStop == null)
		    		throw new DataInputException(label, "Stage stop not valid: " + request.getParameter(prefix + "StageStop2"));
		    	stopId = ageStop.getAgeId().toString();
	    	}
	    } catch (Exception e) {
	   		e.printStackTrace();
	   		throw new DataInputException(label, e.getMessage());	    	
	    }
	    
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
