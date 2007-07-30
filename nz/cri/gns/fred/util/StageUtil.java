package nz.cri.gns.fred.util;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StageDAO;
import nz.cri.gns.fred.model.AgeView;
import nz.cri.gns.fred.model.Stage;

public class StageUtil extends ModelUtil {
	
	private static String NOT_DETERMINED_STAGE = "166";
	private static String NO_FOSSILS_STAGE = "167";
	
	private StageDAO stageDAO;
	
    public StageUtil(DAOFactory factory) {
		super(factory);
		this.stageDAO = factory.getStageDAO();
	}

	/**
	 * Returns string representing a Stage
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	public static String getStageDescription(Stage stage) throws NamingException, SQLException {
		return getStageDesc(stage, 0);
	}

	/**
	 * Returns string representing a Stage (with abbrev names)
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	public static String getStageDescriptionAbbrev(Stage stage) throws NamingException, SQLException {
		return getStageDesc(stage, 1);
	}
	
	private static String getStageDesc(Stage stage, int nameType) throws NamingException, SQLException {
		StringBuffer desc = new StringBuffer();
		if (stage.getLowerAgeView() != null) {
			String[] lowerAge = getStageAgeName(stage.getLowerAgeView());
			desc.append(lowerAge[nameType]);
			if (stage.getStageLowerMod() != null)
				desc.append(stage.getStageLowerMod());
			if (stage.getUpperAgeView() != null) {
				String[] upperAge = getStageAgeName(stage.getUpperAgeView());
				desc.append(" - ");
				desc.append(upperAge[nameType]);
				if (stage.getStageUpperMod() != null)
					desc.append(stage.getStageUpperMod());
			}	
		}
		return desc.toString();		
	}
	
	public AgeView getAgeView(int ageId) throws StorageAccessException {
		return stageDAO.get(ageId, AgeView.class);
	}
	
	public List<AgeView> getAges() throws StorageAccessException {
		return stageDAO.getList("FROM AgeView AS A", AgeView.class);
	}

	public int getMaxAgeId() throws StorageAccessException {
		return stageDAO.getMaxAgeId();
	}
	
	public Stage getStage(String startAgeId, boolean startUncertain, String stopAgeId, boolean stopUncertain) throws StorageAccessException, NamingException, SQLException {
		if (startAgeId == null && stopAgeId == null && !startUncertain && !stopUncertain)
			return null;
		if (startAgeId == null)
			throw new IllegalArgumentException("Start age is null");
		
		AgeView startAge = getAgeView(Integer.parseInt(startAgeId));
		AgeView stopAge = (stopAgeId != null) ? getAgeView(Integer.parseInt(stopAgeId)) : null;

		//check start/stop ages if both entered unless "not determined" or "no fossils"
		if (stopAgeId != null
				&& !(startAgeId.equals(NOT_DETERMINED_STAGE)
				|| startAgeId.equals(NO_FOSSILS_STAGE)
				|| stopAgeId.equals(NOT_DETERMINED_STAGE)
				|| stopAgeId.equals(NO_FOSSILS_STAGE))) {

			if (startAge != null && stopAge != null) {
				if (startAge.getAgeStart().doubleValue() < stopAge.getAgeStart().doubleValue()
						|| startAge.getAgeStop().doubleValue() < stopAge.getAgeStop().doubleValue())
					throw new IllegalArgumentException("Stop age is older than start age");
			} else {
				throw new IllegalArgumentException("Invalid stage(s)");
			}
		}
		
		Stage stage = stageDAO.findStage(startAge, startUncertain, stopAge, stopUncertain);
		if (stage == null) {
			stage = stageDAO.createNewStage();
			stage.setLowerAgeView(startAge);
			stage.setStageLowerMod((startUncertain) ? "?" : null);
			stage.setUpperAgeView(stopAge);
			stage.setStageUpperMod((stopUncertain) ? "?" : null);
			stageDAO.saveOrUpdate(stage);
		}
		return stage;
	}

	/**
	 * Returns true if the given stage differs from that described by the arguments
	 */
	public boolean stageDiffers(Stage stage, String startId, boolean startUncertain, String stopId, boolean stopUncertain) {

		if (stage == null)
			return (startId != null || stopId != null);
		
		if (stage.getLowerAgeView() == null ^ startId == null)
			return true;
		
		if (stage.getUpperAgeView() == null ^ stopId == null)
			return true;
		
		if (startId != null && !new Integer(startId).equals(stage.getLowerAgeView().getAgeId()))
			return true;
		
		if (stopId != null && !new Integer(stopId).equals(stage.getUpperAgeView().getAgeId()))
			return true;
		
		//If we're still here then all the stages are the same - check uncertainties
		if (startUncertain ^ "?".equals(stage.getStageLowerMod()))
			return true;
			
		return stopUncertain ^ "?".equals(stage.getStageUpperMod());
	}
	
	/**
	 * Returns an array of Strings representing the name of the given ageId.
	 * First item is the full name and the second item is the age code
	 */
	private static String[] getStageAgeName(AgeView ageView) throws NamingException, SQLException {
		if (ageView == null)
			return null;
		return new String[] {ageView.getAgeName(), ageView.getAgeAbbrev()};
	}
	
}
