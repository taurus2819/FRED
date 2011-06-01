package nz.cri.gns.fred.model;

/**
 *
 */
public interface FREDConstants {

	public static double FT_TO_M = 0.3048;
	
    //Status types
	public static final String WAITING = "waiting";
	public static final String WORKING = "working";
	public static final String APPROVED = "approved";
	public static final String REJECTED = "rejected";
	
    //Taxonomic name status extra requirements
    public static final String OBSOLETE = "obsolete";
	public static final String PROVISIONAL = "provisional";
	
    //Locality types
	public static final String OUTCROP = "Outcrop";
	public static final String DRILLHOLE = "Drillhole";
	public static final String VERTICAL_SECTION = "Vertical Section";
    
    //Relation types (sample used elsewhere also)
	public static final String SAMPLE = "Sample";
	public static final String STRATIGRAPHIC = "Stratigraphic";

	//Drillhole depths
	public static final String FEET_UNIT = "ft";
	public static final String METRES_UNIT = "m";
	public static final String DEPTH_NOT_SPECIFIED = "Depth not specified";

    //Relationship types
	public static final String NEARBY = "nearby";
	public static final String ABOVE = "above";
	public static final String BELOW = "below";
	public static final String ABOVE_TOP = "above top";
	public static final String ABOVE_BASE = "above base";
	public static final String BELOW_TOP = "below top";
	public static final String BELOW_BASE = "below base";
	
    //Booleans
	public static final String Y = "Y";
	public static final String N = "N";
	
    //Depositional environment types
	public static final String MARINE = "Marine";
	public static final String NON_MARINE = "Non-marine";

    //Record types
    public static final String PALEONTOLOGICAL = "Paleontological";
    public static final String ADOPTION = "Adoption";
    
    //BacklogStatus
    public static final String BACKLOG_PROCESSING = "processing";
    public static final String BACKLOG_COMPLETE = "complete";
    public static final String BACKLOG_NOT_STARTED = "not started";
    public static final String BACKLOG_EMPTY = "no locality";
    public static final String BACKLOG_NEW = "new locality";

    //Data Origin
    public static final int DATA_ORIGIN_ONLINE = 908;
    public static final int DATA_ORIGIN_EXCEL = 909;
}
