package nz.cri.gns.fred.util;

import java.sql.SQLException;

import javax.naming.NamingException;

import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Stage;

public class StageUtil extends ModelUtil {

    public StageUtil(DAOFactory factory) {
		super(factory);
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
		String[] lowerAge = FREDUtil.getStageAgeName(String.valueOf(stage.getStageLowerId()));
		if (lowerAge != null) {
			desc.append(lowerAge[nameType]);
			desc.append(stage.getStageLowerMod());
			String[] upperAge = FREDUtil.getStageAgeName(String.valueOf(stage.getStageUpperId()));
			if (upperAge != null) {
				desc.append(" - ");
				desc.append(upperAge[nameType]);
				desc.append(stage.getStageUpperMod());
			}	
		}
		return desc.toString();		
	}
	

}
