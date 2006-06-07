package nz.cri.gns.fred.model;


/**
 *
 */
public interface PalListMeta extends Meta {
	public abstract PaleontologyListEntry getPalList();
	public abstract void setPalList(PaleontologyListEntry palList);
}