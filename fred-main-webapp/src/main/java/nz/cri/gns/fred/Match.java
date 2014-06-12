package nz.cri.gns.fred;

public enum Match {

	BEGINNING("", "%"),
	END("%", ""),
	ANYWHERE("%","%"),
    EXACT("", "");
	
	private String pre;
	private String post;
	Match(String pre, String post) {
		this.pre = pre;
		this.post = post;
	}
	public String getQueryRepresentation(String str) {
		return pre + str + post;
	}
}
