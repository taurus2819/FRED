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
		if (stage.getStageLowerId() != null) {
			String[] lowerAge = FREDUtil.getStageAgeName(String.valueOf(stage.getStageLowerId()));
			desc.append(lowerAge[nameType]);
			if (stage.getStageLowerMod() != null)
				desc.append(stage.getStageLowerMod());
			if (stage.getStageUpperId() != null) {
				String[] upperAge = FREDUtil.getStageAgeName(String.valueOf(stage.getStageUpperId()));
				desc.append(" - ");
				desc.append(upperAge[nameType]);
				if (stage.getStageUpperMod() != null)
					desc.append(stage.getStageUpperMod());
			}	
		}
		return desc.toString();		
	}
	
	public List<AgeView> getAges() throws StorageAccessException {
		return stageDAO.getList("FROM AgeView AS A", AgeView.class);
	}

	public int getMaxAgeId() throws StorageAccessException {
		return stageDAO.getMaxAgeId();
	}
	
}
