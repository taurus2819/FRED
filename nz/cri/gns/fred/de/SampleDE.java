package nz.cri.gns.fred.de;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import nz.cri.gns.auth.InsufficientPrivelegesException;
import nz.cri.gns.auth.User;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.IconnedLink;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.dao.StorageAccessException;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SampleMeta;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.model.Weathering;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;

public class SampleDE extends DETemplate implements DataEntryForm {

	private User user;
	private Sample sample;
	private boolean isAllowedSubmit = false;

	private boolean outcropSample = false;

	private SampleUtil sampleUtil;
	private ContentProvider provider;
	private UserFolder workingFolder;
	

	public SampleDE(User user, Feature feature, int folderID, DAOFactory factory, ContentProvider content) throws StorageAccessException, InsufficientPrivelegesException {
		this(user, feature, folderID, factory, content, false);
	}

	public SampleDE(User user, Feature feature, int folderID, DAOFactory factory, ContentProvider content, boolean reuseFeatureAudit) throws StorageAccessException, InsufficientPrivelegesException {
		initialise((sampleUtil = new SampleUtil(factory)).createSample(feature, folderID, reuseFeatureAudit, user), folderID, user, factory, content);
	}

	public SampleDE(Sample sample, int folderId, User user, DAOFactory factory, ContentProvider provider) throws InsufficientPrivelegesException, StorageAccessException {
		sampleUtil = new SampleUtil(factory);
		initialise(sample, folderId, user, factory, provider);
	}

	private void initialise(Sample sample, int folderId, User user, DAOFactory factory, ContentProvider content) throws InsufficientPrivelegesException, StorageAccessException {
		this.sample = sample;
		this.user = user;
		this.provider = content;
		
		FolderUtil folderUtil = new FolderUtil(factory);
		
		//check status for editing
		if (!sampleUtil.isAllowedEditSample(user, sample, folderUtil.getUserFolder(folderId, user)))
			throw new InsufficientPrivelegesException("Insufficient rights to create sample");
		if (sample.getAudit().getFolder() != null)
			workingFolder = folderUtil.getUserFolder(sample.getAudit().getFolder().getFolderId().intValue(), user);
		
		isAllowedSubmit = sampleUtil.isAllowedSubmitSample(user, sample, workingFolder);
	}

	public void copyFrom(int sampleId) throws StorageAccessException  {
		Sample copySample = sampleUtil.getSample(sampleId);
		
		getFromDatabase(copySample);
	}

	private void getFromDatabase(Sample fromSample) throws StorageAccessException {
		sample.setCollectionDate(fromSample.getCollectionDate());
		sample.setCollectors(fromSample.getCollectors());
		sample.setStratUnit(fromSample.getStratUnit());
		sample.setInPlace(fromSample.getInPlace());
		sample.setSentTos(fromSample.getSentTos());
		sample.setNotCollected(fromSample.getNotCollected());
		sample.setSignificance(fromSample.getSignificance());
		sample.setInferredStage(fromSample.getInferredStage());
		sample.setKnownStage(fromSample.getKnownStage());
		sample.setRelationships(fromSample.getRelationships());
		sample.setColumnMap(fromSample.getColumnMap());
		sample.setDip(fromSample.getDip());
		sample.setDipDirection(fromSample.getDipDirection());
		sample.setStrike(fromSample.getStrike());
		sample.setFacing(fromSample.getFacing());
		sample.setPrimaryGrainSize(fromSample.getPrimaryGrainSize());
		sample.setSecondaryGrainSize(fromSample.getSecondaryGrainSize());
		sample.setComparatorUsed(fromSample.getComparatorUsed());
		sample.setBedThickness(fromSample.getBedThickness());
		sample.setPrimaryBedding(fromSample.getPrimaryBedding());
		sample.setSecondaryBedding(fromSample.getSecondaryBedding());
		sample.setWeathering(fromSample.getWeathering());
		sample.setHardness(fromSample.getHardness());
		sample.setCarbonate(fromSample.getCarbonate());
		sample.setColourModifier(fromSample.getColourModifier());
		sample.setPrimaryColour(fromSample.getPrimaryColour());
		sample.setSecondaryColour(fromSample.getSecondaryColour());
		sample.setWet(fromSample.getWet());
		HashSet<SedimentaryFeature> sedFeatures = new HashSet<SedimentaryFeature>(fromSample.getSedimentaryFeatures().size());
		for (SedimentaryFeature sedFeature : (Set<SedimentaryFeature>)fromSample.getSedimentaryFeatures()) {
			sedFeatures.add(sampleUtil.copyFor(sedFeature, sample));
		}
		sample.setSedimentaryFeatures(sedFeatures);
		sample.setDepositionEnv(fromSample.getDepositionEnv());
		sample.setRockNature(fromSample.getRockNature());
		sample.setCorrespondence(fromSample.getCorrespondence());
        //TODO not copying metas....for now ???
    }

	public void setOutcropSample(boolean isOutcropSample) {
		this.outcropSample = isOutcropSample;
	}

	public void makeDataEntryHTML(PrintWriter out, DAOFactory factory) throws IOException, SQLException {
        reinitialise(factory);
		ComboDescriptor cd;
		
		if (!outcropSample) {
            //This section is only relevant if this is _not_ an outcrop sample
            Template template = provider.getContent("sample.no.outcrop.de.form");
            prepareTemplate(template, provider);
            template.addSub("featureName", sample.getFeature().getFeatureName());
            template.addSub("sampleName", sample.getSampleName());
            template.addSub("featureId", sample.getFeature().getFeatureId().toString());
            template.addSub("sampleId", sample.getSampleId().toString());
            template.addSub("folderId", (workingFolder == null) ? "" : workingFolder.getFolderId().toString());
            template.addSub("drillholeDepth", SampleUtil.getDrillHoleDepthDescription(sample));
            prepareTemplate(template, provider);
            
            template.loadUntil(out, "{@sampleMeta}");
            
            Set<SampleMeta> images = sample.getSampleMetas();
            if (images != null) try {
                for (SampleMeta meta : images) {
                    out.println(FREDUtil.getMetaTitle(meta) + "<br />");
                }
            } catch (Exception e) {
            }

            template.loadAll(out);
        }
/*
        out.write("<tr><td class='heading' colspan='2'>Security Setting</td><td>");
			cd = new ComboDescriptor("security_class", "class_id", "Name");
			cd.name = "SecType";
			if (getField(SECURITY_TYPE) != null) {
				cd.selected = getFieldForHTML(SECURITY_TYPE);
			} else {
				cd.selected = "21";
			}
			cd.orderBy = "class_ID";
			HTMLUtils.makeDropBox(out, conn, cd);
			out.write("</td></tr>\n");
		}
        */

		try {
	        Template template = provider.getContent("sample.de.form");
            prepareTemplate(template, provider);
	        
	        //The basic substitutions....
	        populateTemplateSubstitutions(template, "\n", ";", "*");

			//Ages...inferred...
            StageDEUtil.drawStageInputs(out, template, sample.getInferredStage(), "inferredAge", "Inf");
	
			//...known...
			StageDEUtil.drawStageInputs(out, template, sample.getKnownStage(), "knownAge", "Knw");

			//Grain size...
			template.loadUntil(out, "{@primaryGrainSize}");
			cd = new ComboDescriptor("grain_size", "grain_size_id", "Code || ': ' || Name");
			cd.name = "GrainSizeP";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getPrimaryGrainSize() == null) ? null : sample.getPrimaryGrainSize().getGrainSizeId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			template.loadUntil(out, "{@secondaryGrainSize}");
			cd.name = "GrainSizeS";
			cd.selected = (sample.getSecondaryGrainSize() == null) ? null : sample.getSecondaryGrainSize().getGrainSizeId().toString();
			FREDUtil.makeDropBox(out, cd);

			
			template.loadUntil(out, "{@beddingThickness}");

			cd = new ComboDescriptor("bed_thickness", "thickness_id", "Code || ': ' || Name");
			cd.name = "BedThick";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getBedThickness() == null) ? null : sample.getBedThickness().getThicknessId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@primaryBedding}");
			cd = new ComboDescriptor("bedding", "bedding_ID", "Code || ': ' || Name");
			cd.name = "BeddingP";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getPrimaryBedding() == null) ? null : sample.getPrimaryBedding().getBeddingId().toString(); 
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@secondaryBedding}");
			cd.name = "BeddingS";
			cd.selected = (sample.getSecondaryBedding() == null) ? null : sample.getSecondaryBedding().getBeddingId().toString();
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@weathering}");
			cd = new ComboDescriptor("weathering", "weathering_ID", "Code || ': ' || Name");
			cd.name = "Weath";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getWeathering() == null) ? null : sample.getWeathering().getWeatheringId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@hardness}");
			cd = new ComboDescriptor("hardness", "hardness_ID", "Code || ': ' || Name");
			cd.name = "Hard";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getHardness() == null) ? null : sample.getHardness().getHardnessId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@carbonate}");
			cd = new ComboDescriptor("carbonate", "carbonate_ID", "Code || ': ' || Name");
			cd.name = "Carb";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getCarbonate() == null) ? null : sample.getCarbonate().getCarbonateId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@ColMod}");
			cd = new ComboDescriptor("colour_modifier", "modifier_ID", "Code || ': ' || Name");
			cd.name = "ColMod";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getColourModifier() == null) ? null : sample.getColourModifier().getModifierId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@primaryColour}");
			cd = new ComboDescriptor("rock_colour", "colour_id", "Code || ': ' || Name");
			cd.name = "ColourP";
			cd.prompt = " -- Choose -- ";
			cd.selected = (sample.getPrimaryColour() == null) ? null : sample.getPrimaryColour().getColourId().toString();
			cd.orderBy = "Code";
			FREDUtil.makeDropBox(out, cd);
			
			template.loadUntil(out, "{@secondaryColour}");
			cd.name = "ColourS";
			cd.selected = (sample.getSecondaryColour() == null) ? null : sample.getSecondaryColour().getColourId().toString();
			FREDUtil.makeDropBox(out, cd);
						
			template.loadAll(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!outcropSample) {
            //This section is only relevant if this is _not_ an outcrop sample
            Template template = provider.getContent("sample.no.outcrop.de.buttons");
            prepareTemplate(template, provider);
            if (isAllowedSubmit)
            	template.addSub("isAllowedSubmit", "Yes");
            
            template.loadAll(out);
		}
	}

	private void populateTemplateSubstitutions(Template template, String newLineSeparator, String semiColonSeparator, String starSeparator) throws NamingException, SQLException, StorageAccessException {
		template.addSub("CollDate", FREDUtil.formatDateForDE(sample.getCollectionDate(), sample.getDateRounding()));
		template.addSub("Coll", FREDUtil.getNames(sample.getCollectors(), newLineSeparator));
		template.addSub("StratName", sample.getStratUnit());
		
		//With in place, allow for both direct and select box approach
		template.addSub("InPlace", sample.getInPlace());
		template.addSub("is" + sample.getInPlace(), "yes");
		
		template.addSub("SentTo", getSentTos(sample.getSentTos(), newLineSeparator));
		template.addSub("NotColl", sample.getNotCollected());
		template.addSub("Sig", sample.getSignificance());
		
		//Ages
        StageDEUtil.addStageSubs(template, sample.getInferredStage(), "Inferred");
		StageDEUtil.addStageSubs(template, sample.getKnownStage(), "Known");
		
		//Previous samples....
		template.addSub("PrevSamp", getRelationshipsBrief(FREDConstants.SAMPLE, FREDConstants.NEARBY, semiColonSeparator));

		template.addSub("SampRel", getRelationshipsFull(FREDConstants.SAMPLE, new String[] {FREDConstants.ABOVE, FREDConstants.BELOW}, newLineSeparator));
		
		template.addSub("StratRel", getRelationshipsFull(FREDConstants.STRATIGRAPHIC, new String[] {
				FREDConstants.ABOVE_TOP, 
				FREDConstants.ABOVE_BASE, 
				FREDConstants.BELOW_TOP, 
				FREDConstants.BELOW_BASE
		}, newLineSeparator));

		template.addSub("ColMap", sample.getColumnMap());
        template.addSub("Dip", FREDUtil.toString(sample.getDip()));
		//Do direction as both value and select...
		template.addSub("DipDir", sample.getDipDirection());
		template.addSub("isDip" + sample.getDipDirection(), "Yes");

		template.addSub("Strike", FREDUtil.toString(sample.getStrike()));
		//Do facing as both value and select...
		template.addSub("Facing", sample.getFacing());
		template.addSub("isFacing" + sample.getFacing(), "Yes");

		//Comparator as both value and select
		template.addSub("GSComp", sample.getComparatorUsed());
		template.addSub("isComp" + sample.getComparatorUsed(), "Yes");

		//Wet as value and select
		template.addSub("Wet", sample.getWet());
		template.addSub("isWet" + sample.getWet(), "Yes");
		
		template.addSub("SedFeat", getSedimentaryFeatures(semiColonSeparator, starSeparator));
		
		//Inferred environ as value and select
		String marine = getDepositionalEnvironmentMarineOrNot();
		template.addSub("DepEnv1", marine);
		template.addSub("is" + marine, "Yes");
		//Inferred environ part II
		template.addSub("DepEnv2", getDepositionalEnvironmentFreeText());
		
		template.addSub("RockNat", sample.getRockNature());
		template.addSub("Corr", sample.getCorrespondence());
	}

    private String getSentTos(Set<SentTo> sentTos, String separator) throws NamingException, SQLException {
		if (sentTos == null)
			return "";
    	StringBuffer buffer = new StringBuffer();
		
		for (SentTo sentTo : sentTos) {
			if (sentTo.getFossilGroup() != null)
				buffer.append(sentTo.getFossilGroup().getName());
			buffer.append("*");
			if (sentTo.getPerson() != null)
				buffer.append(sentTo.getPerson().getDisplayName());
			buffer.append("*");
			if (sentTo.getLabId() != null)
				buffer.append(FREDUtil.getLabName(sentTo.getLabId()));
			buffer.append("*");
			if (sentTo.getComments() != null)
				buffer.append(sentTo.getComments());
				
			buffer.append(separator);
		}
		return buffer.toString();
	}

	private String getDepositionalEnvironmentFreeText() {
		String dep = sample.getDepositionEnv();
		if (dep == null)
			return null;
		if (dep.startsWith(FREDConstants.MARINE + ":"))
			return dep.substring(7);
		else if (dep.startsWith(FREDConstants.NON_MARINE + ":"))
			return dep.substring(11);
		return dep;
	}

	private String getDepositionalEnvironmentMarineOrNot() {
		String dep = sample.getDepositionEnv();
		if (dep == null)
			return null;
		if (dep.startsWith(FREDConstants.MARINE + ":"))
			return FREDConstants.MARINE;
		else if (dep.startsWith(FREDConstants.NON_MARINE + ":"))
			return FREDConstants.NON_MARINE;
		return null;
	}

	private String getSedimentaryFeatures(String semicolonSeparator, String starSeparator) {
        if (sample.getSedimentaryFeatures() == null)
        	return "";
		//.replaceAll(";", "#").replaceAll("\\*", "\\$")
		StringBuffer buffer = new StringBuffer();
		for (SedimentaryFeature feat : (Set<SedimentaryFeature>)sample.getSedimentaryFeatures()) {
			buffer.append(feat.getSedimentaryFeatureType().getName());
			if (feat.getAbundant() != null && feat.getAbundant().equals(FREDConstants.Y))
				buffer.append(starSeparator);
			buffer.append(semicolonSeparator);
		}
		return buffer.toString();
	}

	private String getRelationshipsBrief(String relationType, String relationshipType, String separator) throws StorageAccessException {
		if (sample.getRelationships() == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (Relationship rel : sampleUtil.getRelationships(sample, relationType, relationshipType))
			buffer.append(FeatureUtil.getFeatureIdentifyingName(rel.getFeature())).append(separator);
		return buffer.toString();
	}

	private String getRelationshipsFull(String relationType, String[] relationshipTypes, String separator) throws StorageAccessException {
		if (sample.getRelationships() == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (Relationship rel : sampleUtil.getRelationships(sample, relationType, relationshipTypes))
			buffer.append(SampleUtil.getRelationshipDescription(rel)).append(separator);
		return buffer.toString();
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException  {
        try {
	    	Template template = provider.getContent("sample.excel");
            populateTemplateSubstitutions(template, "#", "#", "$");
			if (!outcropSample) {
			    template.addSub("featureIdIfNotOutcrop", sample.getFeature().getFeatureId().toString());
			    template.addSub("folderIdIfNotOutcrop", (workingFolder == null) ? "" : workingFolder.getFolderId().toString());
			    template.addSub("featureTypeIfNotOutcrop", sample.getFeature().getFeatureType());
			    template.addSub("sampleNameIfNotOutcrop", sample.getSampleName());
			    template.addSub("sampleStatusIfNotOutcrop", sample.getAudit().getStatus());
			}
            template.addSub("sampleId", String.valueOf(sample.getSampleId()));
            
            //Add in depth information
            template.addSub("topDepth", (sample.getTopDepth() == null) ? "" : sample.getTopDepth().toString());
            template.addSub("bottomDepth", (sample.getBottomDepth() == null) ? "" : sample.getBottomDepth().toString());
            template.addSub("drillType", (sample.getDrillType() == null) ? "" : sample.getDrillType().getName());
            
			//Converts from dropdowns
			Stage stage = sample.getInferredStage();
			if (stage != null) {
				if (stage.getStageLowerId() != null)
					template.addSub("InferredAgeStart", stage.getStageLowerId().toString());
				if (stage.getStageUpperId() != null)
					template.addSub("InferredAgeStop", stage.getStageUpperId().toString());
			}
	
			stage = sample.getKnownStage();
			if (stage != null) {
				if (stage.getStageLowerId() != null)
					template.addSub("KnownAgeStart", stage.getStageLowerId().toString());
				if (stage.getStageUpperId() != null)
					template.addSub("KnownAgeStop", stage.getStageUpperId().toString());
			}
			
			if (sample.getPrimaryGrainSize() != null)
				template.addSub("GrainSizeP", sample.getPrimaryGrainSize().getGrainSizeId().toString());
			
			if (sample.getSecondaryGrainSize() != null)
				template.addSub("GrainSizeS", sample.getSecondaryGrainSize().getGrainSizeId().toString());
	
			if (sample.getBedThickness() != null)
				template.addSub("BedThick", sample.getBedThickness().getThicknessId().toString());
			
			if (sample.getPrimaryBedding() != null)
				template.addSub("BeddingP", sample.getPrimaryBedding().getBeddingId().toString());
			
			if (sample.getSecondaryBedding() != null)
				template.addSub("BeddingS", sample.getSecondaryBedding().getBeddingId().toString());
			
			if (sample.getWeathering() != null) 
				template.addSub("Weath", sample.getWeathering().getWeatheringId().toString());
			
			if (sample.getCarbonate() != null)
				template.addSub("Carb", sample.getCarbonate().getCarbonateId().toString());
			
			if (sample.getHardness() !=  null)
				template.addSub("Hard", sample.getHardness().getHardnessId().toString());
			
			if (sample.getColourModifier() != null) 
				template.addSub("ColMod", sample.getColourModifier().getModifierId().toString());
			
			if (sample.getPrimaryColour() != null)
				template.addSub("ColourP", sample.getPrimaryColour().getColourId().toString());
			
			if (sample.getSecondaryColour() != null)
				template.addSub("ColourS", sample.getSecondaryColour().getColourId().toString());
			
	    	template.loadAll(new PrintWriter(out));
            
            if (!outcropSample)
                out.write("</tr>");
            
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
	}
	
	public int save() throws StorageAccessException, InsufficientPrivelegesException {
		if (!isAllowedSubmit)
			throw new InsufficientPrivelegesException();

		if (sample.getSampleId() == null) {
			//It's an insert
			Audit audit = sample.getAudit();
			audit.setStatus(FREDConstants.WORKING);
			audit.setCreatedById(user.getDatabaseId());
			audit.setCreatedDate(new Date());
		} 
    	if (!outcropSample) {
			//Update AUDIT
			/* what comments?  what security class?
					QueryDescriptor qd = new QueryDescriptor("audit_table");
					qd.addQueryColumn("working_comments", Types.VARCHAR, fields[WORKING_COMMENTS]);
					qd.addQueryColumn("security_class_id", Types.NUMERIC, ((secClassID != null) ? secClassID : new Integer(4)));
					qd.addQueryColumn(QueryDescriptor.NOT_FOR_UPDATE, Types.NUMERIC, new Integer(auditID));
					DBUtils.doUpdate(qd, "audit_id = ?", conn);
			*/
		}
		System.out.println("Inf Stage: " + sample.getInferredStage());	
        sampleUtil.saveOrUpdate(sample);
    		
		return sample.getSampleId().intValue();
	
	}

	/*
	private QueryDescriptor getSampleQD() throws NumberFormatException, IOException, SQLException {
		QueryDescriptor qd = new QueryDescriptor("sample");
		qd.addQueryColumn("collection_date", Types.DATE ,((collDate != null) ? collDate.getDate() : null));
		qd.addQueryColumn("date_rounding", Types.VARCHAR, ((collDate != null) ? collDate.getDateRounding() : null));
		qd.addQueryColumn("strat_unit", Types.VARCHAR, getField(STRAT_NAME));
		qd.addQueryColumn("in_place", Types.VARCHAR, getField(FOSSILS_IN_PLACE));
		qd.addQueryColumn("not_collected", Types.VARCHAR, getField(NOT_COLLECTED));
		qd.addQueryColumn("significance", Types.VARCHAR, getField(SIGNIFICANCE_COMMENTS));
		String infStageID = DataEntryUtils.getStageID(getField(INF_AGE_START), getField(INF_START_MOD), getField(INF_AGE_STOP), getField(INF_STOP_MOD), state);
		qd.addQueryColumn("inferred_stage_id", Types.NUMERIC, ((infStageID != null) ? new Integer(infStageID) : null));
		String knwStageID = DataEntryUtils.getStageID(getField(KNW_AGE_START), getField(KNW_START_MOD), getField(KNW_AGE_STOP), getField(KNW_STOP_MOD), state);
		qd.addQueryColumn("known_stage_id", Types.NUMERIC, ((knwStageID != null) ? new Integer(knwStageID) : null));
		qd.addQueryColumn("column_map", Types.VARCHAR, getField(COLUMN_MAP));
		qd.addQueryColumn("dip", Types.NUMERIC, ((getField(DIP) != null) ? new Integer(getField(DIP)) : null));
		qd.addQueryColumn("dip_direction", Types.VARCHAR, getField(DIP_DIRECTION));
		qd.addQueryColumn("strike", Types.NUMERIC, ((getField(STRIKE) != null) ? new Integer(getField(STRIKE)) : null));
		qd.addQueryColumn("facing", Types.VARCHAR, getField(FACING));
		qd.addQueryColumn("primary_grainsize_id", Types.NUMERIC, ((getField(GRAIN_SIZE_P) != null) ? new Integer(getField(GRAIN_SIZE_P)) : null));
		qd.addQueryColumn("secondary_grainsize_id", Types.NUMERIC, ((getField(GRAIN_SIZE_S) != null) ? new Integer(getField(GRAIN_SIZE_S)) : null));
		qd.addQueryColumn("comparator_used", Types.VARCHAR, getField(GS_COMP));
		qd.addQueryColumn("bed_thick_id", Types.NUMERIC, ((getField(BEDDING_THICKNESS) != null) ? new Integer(getField(BEDDING_THICKNESS)) : null));
		qd.addQueryColumn("primary_bedding_id", Types.NUMERIC, ((getField(BEDDING_P) != null) ? new Integer(getField(BEDDING_P)) : null));
		qd.addQueryColumn("secondary_bedding_id", Types.NUMERIC, ((getField(BEDDING_S) != null) ? new Integer(getField(BEDDING_S)) : null));
		qd.addQueryColumn("weathering_id", Types.NUMERIC, ((getField(WEATHERING) != null) ? new Integer(getField(WEATHERING)) : null));
		qd.addQueryColumn("hardness_id", Types.NUMERIC, ((getField(HARDNESS) != null) ? new Integer(getField(HARDNESS)) : null));
		qd.addQueryColumn("carbonate_id", Types.NUMERIC, ((getField(CARBONATE) != null) ? new Integer(getField(CARBONATE)) : null));
		qd.addQueryColumn("colour_modifier_id", Types.NUMERIC, ((getField(COLOUR_MOD) != null) ? new Integer(getField(COLOUR_MOD)) : null));
		qd.addQueryColumn("primary_colour_id", Types.NUMERIC, ((getField(COLOUR_P) != null) ? new Integer(getField(COLOUR_P)) : null));
		qd.addQueryColumn("secondary_colour_id", Types.NUMERIC, ((getField(COLOUR_S) != null) ? new Integer(getField(COLOUR_S)) : null));
		qd.addQueryColumn("wet", Types.VARCHAR, getField(WET));
		qd.addQueryColumn("deposition_env", Types.VARCHAR, depEnv);
		qd.addQueryColumn("rock_nature", Types.VARCHAR, getField(ROCK_NATURE));
		qd.addQueryColumn("correspondence", Types.VARCHAR, getField(CORRESPONDENCE));
		return qd;
	}

	public int getWorkingFolderID() {
		if (workingFolder != null)
			return workingFolder.getFolderID();
		return -1;
	}

	public int getFieldCount() {
		return fields.length;
	}

	public void setField(int field, String value) throws DataInputException, TaxonomicListException {
		if (value != null && (value.equals("") || value.equals("-") || value.equals("null")))
			value = null;
		if (value != null) {
			parseField(field, value);
		} else {
			resetHiddenField(field);
		}
		fields[field] = value;
		savedFlag = false;
	}

	public void setTempField(int field, String value) {
		tempFields[field] = value;	
	}

	public String getField(int field) {
		return fields[field];
	}

	public String getTempField(int field) {
		return tempFields[field];
	}

	public void setFieldsFromTemp() throws DataInputException, TaxonomicListException {
		for (int i = 0; i < getFieldCount(); i++) {
			setField(i, tempFields[i]);
			setTempField(i, null);
		}
	}
*/
	public int submit() throws InsufficientPrivelegesException, DataInputException, StorageAccessException {
		if (!outcropSample && (!isAllowedSubmit || sample.getAudit().getStatus().equals(FREDConstants.WAITING)))
			throw new InsufficientPrivelegesException();
		if (sample.getCollectors() == null || sample.getCollectors().size() == 0
				|| sample.getCollectionDate() == null 
				|| sample.getInPlace() == null)
			throw new MandatoryFieldsMissingException();
		save();
		if (!outcropSample) {
			FREDUtil.submit(sample, user, sampleUtil, false);
		}
		
		return sample.getSampleId().intValue();
	}


	public void delete() throws InsufficientPrivelegesException, StorageAccessException {
		if (sample.getSampleId() != null && !sampleUtil.isAllowedDeleteSample(user, sample, workingFolder))
			throw new InsufficientPrivelegesException();
		sampleUtil.delete(sample);
	}
	
	public void makeNavPanelHTML(Writer out) throws IOException {
/*		out.write("<tr><td colspan='2' align='center'><img src='images/drill.gif' height='20' width='20' /></td></tr>");
		out.write("<tr><td colspan='2' align='center' class='heading'>Sample</td></tr>\n");
		out.write("<tr><td>&nbsp;</td></tr>");
		if (workingFolder != null) {
			out.write("<tr><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((sample != null) ? "&SampID=" + sample.getSampleID() : "")
				+ "&RecType=Sample'><img src='images/load.gif' height='20' width='20' border='0' alt='Copy From' /></a>&nbsp;&nbsp;</td><td><a href='load_record.jsp?FoldID=" + workingFolder.getFolderID()
				+ ((sample != null) ? "&SampID=" + sample.getSampleID() : "")
				+ "&RecType=Sample' class='boldlink'>Copy From</a></td></tr>\n");
		}
		out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();'><img src='images/save.gif' height='20' width='20' border='0' alt='Save'/></a>&nbsp;&nbsp;</td><td><a href='#' onClick='form1.SaveType.value=\"Save\";form1.submit();' class='boldlink'>Save</a></td></tr>\n");
		if (isAllowedSubmit)
			out.write("<tr><td><a href='#' onClick='form1.SaveType.value=\"Submit\";form1.submit();'><img src='images/submit.gif' height='20' width='20' border='0' alt='Submit to Database' /></a>&nbsp;&nbsp;</td><td><a href='#' class='heading' onClick='form1.SaveType.value=\"Submit\";form1.submit();' class='boldlink'>Submit</a></td></tr>\n");
		*/
	}


	public List<IconnedLink> getNavigation() {
		List<IconnedLink> links = new Vector<IconnedLink>(4);
		String args = ((workingFolder == null) ? "?q" : ("?FoldID=" + workingFolder.getFolderId())) 
			+ ((sample.getSampleId() == null) ? "" : ("&SampID=" + sample.getSampleId()))
			+ "&RecType=Sample";
			
		links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
		
		links.add(new IconnedLink("javascript:document.form1.SaveType.value='Save';document.form1.submit();", "images/save.gif", "Save"));
		if (isAllowedSubmit)
			links.add(new IconnedLink("javascript:document.form1.SaveType.value='Submit';document.form1.submit();", "images/submit.gif", "Submit"));
		
		return links;
	}

	public void makePostFormHTML(PrintWriter out) throws IOException {
		Template template = provider.getContent("calendar.script");
		template.addSub("inputField", "CollDate");
		template.loadAll(out);
	}

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory) throws DataInputException {
        reinitialise(factory);

        Vector<String[]> error = new Vector<String[]>();

		//Collection date
		try {
			String collectionDate = request.getParameter("CollDate");
			sample.setCollectionDate(FREDUtil.parseDateFromDE(collectionDate));
			sample.setDateRounding(FREDUtil.parseDateRoundingFromDE(collectionDate));
		} catch (ParseException e) {
			error.add(new String[] {"Start Date", "Badly formatted date"});
		}
		
		//Collectors
        PersonUtil personUtil = new PersonUtil(factory);
        try {
            sample.setCollectors(FREDUtil.getPersons(request.getParameter("Coll"), personUtil, "Collectors"));
        } catch (DataInputException e) {
            if (e.hasAuxiliaryData()) {
                sample.setCollectors((Set)e.getAuxiliaryData());
            }
            error.addAll(e.getError());
        }

		//Strat name
		sample.setStratUnit(request.getParameter("StratName"));
		//In place
		sample.setInPlace(request.getParameter("InPlace"));
		
		//Sent to
        String sentToParam = request.getParameter("SentTo").trim();
        if (sentToParam.length() > 0) {
    		String[] sentTos = request.getParameter("SentTo").split("\\n");
    		HashSet<SentTo> sentToSet = new HashSet<SentTo>();
    		for (String sentTo : sentTos) try {
    			String[] parts = sentTo.split("\\*");
    			FossilGroup group = (parts[0].length() == 0) ? null : sampleUtil.getFossilGroup(parts[0]);
    			if (parts[0].length() > 0 && group == null)
    				error.add(new String[] {"Sent To", "Invalid group: " + parts[0]});
    
    			Person person = (parts[1].length() == 0) ? null : personUtil.findPerson(parts[1]);
    			if (parts[1].length() > 0 && person == null)
    				error.add(new String[] {"Sent To", "Invalid person: " + parts[1]});
    			
    			Integer lab = (parts[2].length() == 0) ? null : FREDUtil.getLabId(parts[2]);
    			if (parts[2].length() > 0 && lab == null)
    				error.add(new String[] {"Sent To", "Invalid lab: " + parts[2]});
    			
    			String comments = parts[3];
    			
    			sentToSet.add(sampleUtil.findOrCreateSentTo(sample, group, person, lab, comments));
    		} catch (Exception e) {
                e.printStackTrace();
    			error.add(new String[] {"Sent To", "Database error: " + e.getMessage()});
    		}
    		sample.setSentTos(sentToSet);
        } else {
            if (sample.getSentTos() != null)
                sample.getSentTos().clear();
        }
        
        //Not collected?
        sample.setNotCollected(request.getParameter("NotColl"));
        sample.setSignificance(request.getParameter("Sig"));
        
		//Work out the inferred stage
        System.out.println("Checking Inf Stage");
        try {
            sample.setInferredStage(StageDEUtil.getStage(request, "Inf", sample.getInferredStage(), sampleUtil, "Inferred stage"));
        } catch (DataInputException e) {
        	System.out.println("Inf Stage error");
            error.addAll(e.getError());
        }
		
        //Work out the known stage
        try {
            sample.setKnownStage(StageDEUtil.getStage(request, "Knw", sample.getKnownStage(), sampleUtil, "Known stage"));
        } catch (DataInputException e) {
            error.addAll(e.getError());
        }
		
		//Relationships - previous, sample and strat
		Set<Relationship> relationships = sample.getRelationships();
		//Split into different kinds of relationships
		HashSet<Relationship> previousSample = new HashSet<Relationship>(), sampleRel = new HashSet<Relationship>(), stratRel = new HashSet<Relationship>();
		if (relationships != null) {
			for (Relationship rel : relationships) {
				if (sampleUtil.isPreviousSampleRelationship(rel))
					previousSample.add(rel);
				else if (sampleUtil.isStratigraphicRelationship(rel))
					stratRel.add(rel);
				else
					sampleRel.add(rel);
			}
		} else {
			relationships = new HashSet<Relationship>();
			sample.setRelationships(relationships);
		}
		//Now deal with previous samples
		FeatureUtil featureUtil = new FeatureUtil(factory);
		if (request.getParameter("PrevSamp").length() > 0) {
			//Go through all the new ones
			for (String previous : request.getParameter("PrevSamp").split(";")) try {
				previous = previous.trim();
				Feature feature = featureUtil.getFeatureWithIdentifyingName(previous);
				boolean found = false;
				for (Iterator<Relationship> it = previousSample.iterator(); it.hasNext(); ) {
					Relationship rel = it.next();
					if (sampleUtil.isMatchingRelationship(rel, feature, FREDConstants.SAMPLE, FREDConstants.NEARBY)) {
						//Remove it from the old set
						it.remove();
						found = true;
					}
				}
				if (!found) {
					//Wasn't in the old set, so add it 
					relationships.add(sampleUtil.createRelationship(sample, feature, FREDConstants.SAMPLE, FREDConstants.NEARBY));
				}
			} catch (Exception e) {
				error.add(new String[] {"Previous sample", e.getMessage()});
			}
		} 
		//Remove any that are still in the old set
		relationships.removeAll(previousSample);
	
		//Next up - sample relationships
		if (request.getParameter("SampRel").length() > 0) {
			//Go through each entered relationship
			for (String relationshipDesc : request.getParameter("SampRel").split("\\n")) try {
				Relationship newRelationship = sampleUtil.decodeSampleRelationshipDescription(relationshipDesc);
				newRelationship.setSample(sample);
				boolean found = false;
				for (Iterator<Relationship> it = sampleRel.iterator(); it.hasNext(); ) {
					Relationship rel = it.next();
					if (newRelationship.equals(rel)) {
						//Remove it from the old set
						it.remove();
						found = true;
					}
				}
				if (!found) {
					//Wasn't in the old set, so add it 
					relationships.add(sampleUtil.cloneRelationship(newRelationship));
				}
			} catch (Exception e) {
				error.add(new String[] {"Sample relationships", e.getMessage()});
			}
		}
		//Remove any that are still in the old set
		relationships.removeAll(sampleRel);

		//Lastly - strat relationships
		if (request.getParameter("StratRel").length() > 0) {
			//Go through each entered relationship
			for (String relationshipDesc : request.getParameter("StratRel").split("\\n")) try {
				Relationship newRelationship = sampleUtil.decodeStratigraphicRelationshipDescription(relationshipDesc);
				newRelationship.setSample(sample);
				boolean found = false;
				for (Iterator<Relationship> it = stratRel.iterator(); it.hasNext(); ) {
					Relationship rel = it.next();
					if (newRelationship.equals(rel)) {
						//Remove it from the old set
						it.remove();
						found = true;
						break;
					}
				}
				if (!found) {
					//Wasn't in the old set, so add it 
					relationships.add(sampleUtil.cloneRelationship(newRelationship));
				}
			} catch (Exception e) {
				error.add(new String[] {"Stratigraphic relationships", e.getMessage()});
			}
		}
		//Remove any that are still in the old set
		relationships.removeAll(stratRel);
		
		//Column map
		sample.setColumnMap(request.getParameter("ColMap"));
		
		//Dip...
		String dip = request.getParameter("Dip");
		if (dip.length() == 0)
			sample.setDip(null);
		else try {
			sample.setDip(new Integer(dip));
			int iDip = sample.getDip().intValue();
			if (iDip > 90 || iDip < 0) {
				error.add(new String[] {"Dip", iDip + " is not valid.  Dip must be between 0 and 90"});
			}
		} catch (Exception e) {
			error.add(new String[] {"Dip", dip + " is not valid.  Dip must be a whole number of degrees"});
		}
		
		String dipDir = request.getParameter("DipDir");
		if (dipDir.length() == 0)
			sample.setDipDirection(null);
		else
			sample.setDipDirection(dipDir);
		
		//Strike
		String strike = request.getParameter("Strike");
		if (strike.length() == 0)
			sample.setStrike(null);
		else try {
			sample.setStrike(new Integer(strike));
			int iStrike = sample.getStrike().intValue();
			if (iStrike > 360 || iStrike < 0) {
				error.add(new String[] {"Strike", iStrike + " is not valid.  Strike must be between 0 and 360"});
			}
		} catch (Exception e) {
			error.add(new String[] {"Strike", strike + " is not valid.  Strike must be a whole number of degrees"});
		}
		
		String facing = request.getParameter("Facing");
		if (facing.length() == 0)
			sample.setFacing(null);
		else 
			sample.setFacing(facing);
		
		try {
			sample.setPrimaryGrainSize(getGrainSize(request.getParameter("GrainSizeP")));
			sample.setSecondaryGrainSize(getGrainSize(request.getParameter("GrainSizeS")));
		} catch (StorageAccessException e) {
			error.add(new String[] {"Grain size", "Database problem: " + e.getMessage()});
		}
			
		String gsComp = request.getParameter("GSComp");
		if (gsComp.length() == 0)
			sample.setComparatorUsed(null);
		else
			sample.setComparatorUsed(gsComp);
		
		try {
			sample.setBedThickness(getBeddingThickness(request.getParameter("BedThick")));
			sample.setPrimaryBedding(getBedding(request.getParameter("BeddingP")));
			sample.setSecondaryBedding(getBedding(request.getParameter("BeddingS")));
			sample.setWeathering(getWeathering(request.getParameter("Weath")));
			sample.setHardness(getHardness(request.getParameter("Hard")));
			sample.setCarbonate(getCarbonate(request.getParameter("Carb")));
			sample.setColourModifier(getColourModifier(request.getParameter("ColMod")));
			sample.setPrimaryColour(getColour(request.getParameter("ColourP")));
			sample.setSecondaryColour(getColour(request.getParameter("ColourS")));
		} catch (StorageAccessException e) {
			error.add(new String[] {"Lookups", "Database problem: " + e.getMessage()});
		}
		String wet = request.getParameter("Wet");
		if (wet.length() == 0)
			sample.setWet(null);
		else
			sample.setWet(wet);
		
		//Sed features
		Set<SedimentaryFeature> sedFeatures = sample.getSedimentaryFeatures();
		if (sedFeatures == null) {
			sedFeatures = new HashSet<SedimentaryFeature>();
			sample.setSedimentaryFeatures(sedFeatures);
		}
		String sf = request.getParameter("SedFeat");
		if (sf.length() > 0) {
			HashSet<SedimentaryFeature> newFeatures = new HashSet<SedimentaryFeature>(sedFeatures.size());
			for (String sedFeature : sf.split(";")) {
				boolean isAbundant = sedFeature.indexOf("*") == sedFeature.length()-1;
				if (isAbundant)
					sedFeature = sedFeature.substring(0, sedFeature.length()-1);
				//Find it
				boolean found = false;
				for (SedimentaryFeature thisFeature : sedFeatures) {
					if (thisFeature.getSedimentaryFeatureType().getName().equals(sedFeature) && !(DBUtils.nvl(thisFeature.getAbundant()).equals(FREDConstants.Y) ^ isAbundant)) {
						//It's a match
						newFeatures.add(thisFeature);
						found = true;
						break;
					}
				}
				if (!found) try {
					//Create a new one
					SedimentaryFeature feature = sampleUtil.createSedimentaryFeature(sample, sedFeature, isAbundant);
					newFeatures.add(feature);
					sedFeatures.add(feature);
				} catch (Exception e) {
					throw new DataInputException("Additional Features", "Invalid feature: " + sedFeature);
				}
			}
			//Remove anything that's not still there.
			sedFeatures.retainAll(newFeatures);
		} else
			sedFeatures.clear();
			
		//DepositionalEnvironment
		StringBuffer depEnv = new StringBuffer();
		depEnv.append(request.getParameter("DepEnv1"));
		if (depEnv.length() > 0)
			depEnv.append(": ");
		depEnv.append(request.getParameter("DepEnv2"));
		sample.setDepositionEnv(depEnv.toString());
		
		//Rock nature
		sample.setRockNature(request.getParameter("RockNat"));
		sample.setCorrespondence(request.getParameter("Corr"));
		
        if (error.size() > 0) 
            throw new DataInputException(error);
		
	}

    /**
     * @param factory
     */
    private void reinitialise(DAOFactory factory) {
        sampleUtil = new SampleUtil(factory);
        if (sample.getSampleId() != null) try {
            sample = sampleUtil.getSample(sample.getSampleId().intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private RockColour getColour(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getRockColour(new Integer(parameter));
	}

	private ColourModifier getColourModifier(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getColourModifier(new Integer(parameter));
	}

	private Carbonate getCarbonate(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getCarbonate(new Integer(parameter));
	}
	private Hardness getHardness(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getHardness(new Integer(parameter));
	}

	private Weathering getWeathering(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getWeathering(new Integer(parameter));
	}
	
	private Bedding getBedding(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getBedding(new Integer(parameter));
	}
	
	private BedThickness getBeddingThickness(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getBeddingThickness(new Integer(parameter));
	}
	private GrainSize getGrainSize(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getGrainSize(new Integer(parameter));
	}

	public boolean usesCalendar() {
		return true;
	}

	public int getWorkingFolderID() {
		return workingFolder.getFolderId().intValue();
	}

	public String getHeading() {
		return "Edit sample";
	}

}
