package nz.cri.gns.fred.dao;

import java.util.List;

import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.Match;
import nz.cri.gns.fred.model.StratigraphicUnit;

public interface StratLexDAO {
	
	/**
	 * Return a list of units whose names start with the given string, case
	 * insensitively
	 */
	public List<StratigraphicUnit> getMatchingUnitNames(String start, Match matchType, int maxResults) throws StorageAccessException;
}
