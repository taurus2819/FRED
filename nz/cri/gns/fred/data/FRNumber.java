package nz.cri.gns.fred.data;

import java.text.DecimalFormat;

import nz.cri.gns.db.DBUtils;

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
