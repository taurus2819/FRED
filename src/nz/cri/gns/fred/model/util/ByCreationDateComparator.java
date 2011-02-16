package nz.cri.gns.fred.model.util;

import java.util.Comparator;

import nz.cri.gns.fred.model.Audited;

public class ByCreationDateComparator implements Comparator<Audited> {

	public int compare(Audited o1, Audited o2) {
		return o1.getAudit().getCreatedDate().compareTo(o2.getAudit().getCreatedDate());
	}

}
