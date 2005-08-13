package nz.cri.gns.fred.data;

import java.text.DecimalFormat;

import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dataentry.DataInputException;

public class FRNumber {

	private String mapSheet;
	private Integer serialNumber;
	private String recollectionNumber;

	public FRNumber() {
	}

	public FRNumber(String mapSheet, Integer serialNumber, String recollectionNumber) {
		this.mapSheet = mapSheet;
		this.serialNumber = serialNumber;
		this.recollectionNumber = recollectionNumber;
	}

	public static FRNumber parseFRNumber(String frNumStr) throws DataInputException {
		String mapSheet, recollectionNumber;
		Integer serialNumber;
		if (frNumStr.indexOf("/f") > 0) {
			mapSheet = frNumStr.substring(0, frNumStr.indexOf("/f"));
			String num = frNumStr.substring(frNumStr.indexOf("/f") + 2);
			try {
				serialNumber = new Integer(num);
				recollectionNumber = null;
			} catch (Exception e) {
				try {
					serialNumber = new Integer(num.substring(0, num.length() - 1));
					recollectionNumber = num.substring(num.length() - 1);
				} catch (Exception e1) {
					throw new DataInputException("FRNumber", "Badly formed FR Number");
				}
			}
			return new FRNumber(mapSheet, serialNumber, recollectionNumber);
		} else {
			throw new DataInputException("FRNumber", "Badly formed FR Number");
		}
	}
	
	public String getFRNumber() {
		if (mapSheet != null && serialNumber != null) {
			DecimalFormat sNum = new DecimalFormat("0000");
			return mapSheet + "/f" + sNum.format(serialNumber) + DBUtils.nvl(recollectionNumber);
		} else {
			return null;
		}
	}

	public void setMapSheet(String mapSheet) {
		this.mapSheet = mapSheet;
	}

	public String getMapSheet() {
		return mapSheet;
	}

	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Integer getSerialNumber() {
		return serialNumber;
	}

	public void setRecollectionNumber(String recollectionNumber) {
		this.recollectionNumber = recollectionNumber;
	}

	public String getRecollectionNumber() {
		return recollectionNumber;
	}


}
