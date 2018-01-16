package nz.cri.gns.fred.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.FredDAO;
import nz.cri.gns.fred.model.Age;
import nz.cri.gns.fred.model.Stage;

public class StageUtil extends ModelUtil {
	
	private static String NOT_DETERMINED_STAGE_ID = "154";
	private static String NO_FOSSILS_STAGE_ID = "153";
	private static String NOT_DETERMINED_STAGE_CODE = "nd";
	private static String NO_FOSSILS_STAGE_CODE = "nf";
	
	private FredDAO fredDAO;
	
    public StageUtil(DAOFactory factory) {
		super(factory);
		this.fredDAO = factory.getFredDAO();
	}

	public Age saveOrUpdate(Age age) throws StorageAccessException {
		return fredDAO.saveOrUpdate(age);
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
		
		if (stage.getLowerAge() != null) {
			desc.append(getStageAgeName(stage.getLowerAge())[nameType]);
			if (stage.getStageLowerMod() != null) desc.append(stage.getStageLowerMod());
			if (stage.getUpperAge() != null){
                            desc.append(" - ").append(getStageAgeName(stage.getUpperAge())[nameType]);
                            if (stage.getStageUpperMod() != null) desc.append(stage.getStageUpperMod());
                            desc.append(" (").append(stage.getLowerAge().getBaseAge()).append(" - ").append(stage.getUpperAge().getTopAge()).append(" Ma)");
                        } else {
                            // If no upper age then use lower age to provide the top age
                            desc.append(" (").append(stage.getLowerAge().getBaseAge()).append(" - ").append(stage.getLowerAge().getTopAge()).append(" Ma)");
                        }
		}
		return desc.toString();		
	}
	
	public Age getAge(int ageId) throws StorageAccessException {
		return fredDAO.get(ageId, nz.cri.gns.fred.hibernate.Age.class);
	}
	
	public Age getAgeByName(String ageName) throws StorageAccessException {
		List<Age> ages = fredDAO.getList("FROM Age AS a WHERE a.name = ?", Age.class, ageName);
		if (ages != null && ages.size() > 0)
			return ages.get(0);
		return null;
	}
	
	public List<Age> getAges() throws StorageAccessException {
		return fredDAO.getUnsortedList("FROM Age AS a  order by a.baseAge, a.topAge", Age.class);
	}

	public List<Age> getActiveAges() throws StorageAccessException {
		return fredDAO.getList("FROM Age AS a WHERE a.obsoleteFlag = ? AND a.code NOT IN (?, ?)", Age.class, false, NOT_DETERMINED_STAGE_CODE, NO_FOSSILS_STAGE_CODE);
	}
	
	public List<Age> getNonDuplicateAges() throws StorageAccessException {
		return fredDAO.getList("FROM Age AS a WHERE a.duplicateFlag = ? AND a.code NOT IN (?, ?)", Age.class, false, NOT_DETERMINED_STAGE_CODE, NO_FOSSILS_STAGE_CODE);
	}
	
	public int getMaxAgeId() throws StorageAccessException {
		return fredDAO.getMaxAgeId();
	}
	
	public Stage getStage(String startAgeId, boolean startUncertain, String stopAgeId, boolean stopUncertain) throws StorageAccessException, NamingException, SQLException {
		if (startAgeId == null && stopAgeId == null && !startUncertain && !stopUncertain)
			return null;
		if (startAgeId == null)
			throw new IllegalArgumentException("Start age is null");
		
		Age startAge = getAge(Integer.parseInt(startAgeId));
		Age stopAge = (stopAgeId != null) ? getAge(Integer.parseInt(stopAgeId)) : null;

		//check start/stop ages if both entered unless "not determined" or "no fossils"
		if (stopAgeId != null
				&& !(startAgeId.equals(NOT_DETERMINED_STAGE_ID)
				|| startAgeId.equals(NO_FOSSILS_STAGE_ID)
				|| stopAgeId.equals(NOT_DETERMINED_STAGE_ID)
				|| stopAgeId.equals(NO_FOSSILS_STAGE_ID))) {

			if (startAge != null && stopAge != null) {
				if (startAge.getBaseAge().doubleValue() < stopAge.getBaseAge().doubleValue()
						|| startAge.getTopAge().doubleValue() < stopAge.getTopAge().doubleValue())
					throw new IllegalArgumentException("Stop age is older than start age");
			} else {
				throw new IllegalArgumentException("Invalid stage(s)");
			}
		}
		
		Stage stage = fredDAO.findStage(startAge, startUncertain, stopAge, stopUncertain);
		if (stage == null) {
			stage = fredDAO.createNewStage();
			stage.setLowerAge(startAge);
			stage.setStageLowerMod((startUncertain) ? "?" : null);
			stage.setUpperAge(stopAge);
			stage.setStageUpperMod((stopUncertain) ? "?" : null);
			fredDAO.saveOrUpdate(stage);
		}
		return stage;
	}

	/**
	 * Returns true if the given stage differs from that described by the arguments
	 */
	public boolean stageDiffers(Stage stage, String startId, boolean startUncertain, String stopId, boolean stopUncertain) {

		if (stage == null)
			return (startId != null || stopId != null);
		
		if (stage.getLowerAge() == null ^ startId == null)
			return true;
		
		if (stage.getUpperAge() == null ^ stopId == null)
			return true;
		
		if (startId != null && !new Integer(startId).equals(stage.getLowerAge().getAgeId()))
			return true;
		
		if (stopId != null && !new Integer(stopId).equals(stage.getUpperAge().getAgeId()))
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
	private static String[] getStageAgeName(Age age) throws NamingException, SQLException {
		if (age == null)
			return null;
		return new String[] {age.getName(), age.getCode()};
	}
	
	public Age getAgeStart(Stage stage) {
		return stage.getLowerAge();
	}
	
	public Age getAgeStop(Stage stage) {
		if (stage.getUpperAge() != null)
			return stage.getUpperAge();
		return stage.getLowerAge();
	}
	
	public double getNumericAgeStart(Stage stage) {
		return getAgeStart(stage).getBaseAge();
	}
	
	public double getNumericAgeStop(Stage stage) {
		return getAgeStop(stage).getTopAge();
	}
	
	public List<Age> getMatchingAges(String str, int maxMatches) throws StorageAccessException {
		List<Criterion> criteria = new Vector<Criterion>();
		criteria.add(Expression.or
				(Expression.ilike("name", str, MatchMode.START)
				, Expression.ilike("code", str, MatchMode.START)));
		return fredDAO.getList(Age.class, criteria, maxMatches);
	}
	
    public Age createAge() {
        return fredDAO.createNewAge();
    }
}