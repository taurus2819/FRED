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
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.db.ComboDescriptor;
import nz.cri.gns.db.DBUtils;
import nz.cri.gns.fred.dao.DAOFactory;
import nz.cri.gns.fred.model.Audit;
import nz.cri.gns.fred.model.BedThickness;
import nz.cri.gns.fred.model.Bedding;
import nz.cri.gns.fred.model.Carbonate;
import nz.cri.gns.fred.model.ColourModifier;
import nz.cri.gns.fred.model.DrillType;
import nz.cri.gns.fred.model.FREDConstants;
import nz.cri.gns.fred.model.Feature;
import nz.cri.gns.fred.model.FossilGroup;
import nz.cri.gns.fred.model.GrainSize;
import nz.cri.gns.fred.model.Hardness;
import nz.cri.gns.fred.model.Person;
import nz.cri.gns.fred.model.Relationship;
import nz.cri.gns.fred.model.RockColour;
import nz.cri.gns.fred.model.Sample;
import nz.cri.gns.fred.model.SedimentaryFeature;
import nz.cri.gns.fred.model.SentTo;
import nz.cri.gns.fred.model.Stage;
import nz.cri.gns.fred.model.UserFolder;
import nz.cri.gns.fred.model.Weathering;
import nz.cri.gns.fred.util.AuditUtil;
import nz.cri.gns.fred.util.FREDUtil;
import nz.cri.gns.fred.util.FeatureUtil;
import nz.cri.gns.fred.util.FolderUtil;
import nz.cri.gns.fred.util.PersonUtil;
import nz.cri.gns.fred.util.SampleUtil;
import nz.cri.gns.fred.website.ContentProvider;
import nz.cri.gns.intranet.Template;
import nz.cri.gns.jsp.IconnedLink;

public class SampleDE extends DETemplate implements DataEntryForm {

	private User user;
	private Sample sample;
	private Sample copySample;
	
	private boolean isAllowedSave = false;
	private boolean isAllowedSubmit = false;

	private boolean outcropSample = false;

	private SampleUtil sampleUtil;
	private DAOFactory factory;
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
		this.factory = factory;
		
		FolderUtil folderUtil = new FolderUtil(factory);
		
		//check status
		if (outcropSample || !sampleUtil.isAllowedReadSample(user, sample))
			throw new InsufficientPrivelegesException("Insufficient rights to view sample");
		if (sample.getAudit().getFolder() != null)
			workingFolder = folderUtil.getUserFolder(sample.getAudit().getFolder().getFolderId().intValue(), user);
		
		try {
			isAllowedSave = outcropSample || sampleUtil.isAllowedEditSample(user, sample, workingFolder);
			isAllowedSubmit = outcropSample || sampleUtil.isAllowedSubmitSample(user, sample, workingFolder);
		} catch (Exception e) {}
	}

	public void copyFrom(int sampleId) throws StorageAccessException  {
		Sample fromSample = sampleUtil.getSample(sampleId);
		
		this.copySample = fromSample;
	}

	private void getFromDatabase(Sample fromSample) throws StorageAccessException {
		sample.setCollectionDate(fromSample.getCollectionDate());
		
        //Collectors
        Set<Person> collectors = sample.getCollectors(); 
        if (collectors == null) {
            collectors = new HashSet<Person>();
            sample.setCollectors(collectors);
        } else {
            collectors.clear();
        }
        if (fromSample.getCollectors() != null)
            collectors.addAll(fromSample.getCollectors());
		//sample.setCollectors(fromSample.getCollectors());
        
		sample.setStratUnit(fromSample.getStratUnit());
		sample.setInPlace(fromSample.getInPlace());
		
		//Sent to
        Set<SentTo> sentTos = sample.getSentTos(); 
        if (sentTos == null) {
            sentTos = new HashSet<SentTo>();
            sample.setSentTos(sentTos);
        } else {
            sentTos.clear();
        }
        if (fromSample.getSentTos() != null)
            sentTos.addAll(fromSample.getSentTos());
		//sample.setSentTos(fromSample.getSentTos());
		
		sample.setNotCollected(fromSample.getNotCollected());
		sample.setSignificance(fromSample.getSignificance());
		sample.setInferredStage(fromSample.getInferredStage());
		sample.setKnownStage(fromSample.getKnownStage());
		
		//Relationships
        Set<Relationship> relationships = sample.getRelationships(); 
        if (relationships == null) {
        	relationships = new HashSet<Relationship>();
            sample.setRelationships(relationships);
        } else {
        	relationships.clear();
        }
        if (fromSample.getRelationships() != null) {
        	relationships.addAll(fromSample.getRelationships());
		//sample.setRelationships(fromSample.getRelationships());
        }
        
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
            template.addSub("topDepth", ((sample.getTopDepth() != null) ? String.valueOf(sample.getTopDepth()) : ""));
            template.addSub("bottomDepth", ((sample.getBottomDepth() != null) ? String.valueOf(sample.getBottomDepth()) : ""));
            if ("ft".equals(sample.getDepthUnit()))
            	template.addSub("depthft", "checked ");
            else
            	template.addSub("depthm", "checked ");
            
			if (sample.getFeature().getFeatureType().equals(FREDConstants.DRILLHOLE)) {
				template.addSub("isDrillhole", "yes");
				try {
					//drill type combo box
					template.loadUntil(out, "{@drillTypeCombo}");
					cd = new ComboDescriptor("drill_type", "drill_type_id", "Name");
					cd.name = "DrillType";
					cd.orderBy = "drill_type_id";
					cd.prompt = " -- Choose -- ";
					cd.selected = (sample.getDrillType() == null) ? null : String.valueOf(sample.getDrillType().getDrillTypeId());
					FREDUtil.makeDropBox(out, cd);
				} catch (Exception e) {}
			}
			//comments
			if (sample.getSampleId() != null)
				template.addSub("sampleId", sample.getSampleId().toString());
            template.addSub("folderId", (workingFolder == null) ? "" : workingFolder.getFolderId().toString());
            prepareTemplate(template, provider);

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

	        //Collectors
	        template.loadUntil(out, "{@Coll}");
	        Set<Person> collectors = sample.getCollectors();
	        if (collectors != null) {
		        for (Person collector : collectors) {
		        	out.println("collArray[collArray.length] = '" + collector.getDisplayName() + "';");
		        }
	        }
	        
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
		String marine = getDepositionalEnvironmentMarineOrNot(sample.getDepositionEnv());
		template.addSub("DepEnv1", marine);
		template.addSub("is" + marine, "Yes");
		//Inferred environ part II
		template.addSub("DepEnv2", getDepositionalEnvironmentFreeText(sample.getDepositionEnv()));
		
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

	private String getDepositionalEnvironmentFreeText(String dep) {
		if (dep == null)
			return null;
		dep = dep.trim();
		if (dep.toUpperCase().startsWith(FREDConstants.MARINE.toUpperCase()))
			dep = dep.substring(6).trim();
		else if (dep.toUpperCase().startsWith(FREDConstants.NON_MARINE.toUpperCase()))
			dep = dep.substring(10).trim();
		if (dep.startsWith(":") || dep.startsWith(".") || dep.startsWith(","))
			return dep.substring(1).trim();
		return dep;
	}

	private String getDepositionalEnvironmentMarineOrNot(String dep) {
		if (dep == null)
			return null;
		dep = dep.trim();
		if (dep.toUpperCase().startsWith(FREDConstants.MARINE.toUpperCase()))
			return FREDConstants.MARINE;
		else if (dep.toUpperCase().startsWith(FREDConstants.NON_MARINE.toUpperCase()))
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
		for (Relationship rel : sample.getRelationships()) {
			if (rel.getRelationType().getName().equals(relationType) && rel.getRelationshipType().getName().equals(relationshipType))
				buffer.append(FeatureUtil.getFeatureIdentifyingName(rel.getFeature())).append(separator);
		}
		return buffer.toString();
	}

	private String getRelationshipsFull(String relationType, String[] relationshipTypes, String separator) throws StorageAccessException {
		if (sample.getRelationships() == null)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (Relationship rel : sample.getRelationships()) {
			for (int i = 0; i < relationshipTypes.length; i++) {
				if (rel.getRelationType().getName().equals(relationType) && rel.getRelationshipType().getName().equals(relationshipTypes[i])) {
					buffer.append(SampleUtil.getRelationshipDescription(rel)).append(separator);
					break;
				}
			}
		}
		return buffer.toString();
	}

	public void makeExcelImportHTML(Writer out) throws IOException, SQLException  {
        try {
	    	Template template = provider.getContent("sample.de.excel");
            populateTemplateSubstitutions(template, "#", "#", "$");
            template.addSub("Coll", FREDUtil.getNames(sample.getCollectors(), "#"));
    		
			if (!outcropSample) {
			    template.addSub("featureIdIfNotOutcrop", sample.getFeature().getFeatureId().toString());
			    template.addSub("folderIdIfNotOutcrop", (workingFolder == null) ? "" : workingFolder.getFolderId().toString());
			    template.addSub("featureTypeIfNotOutcrop", sample.getFeature().getFeatureType());
			    template.addSub("sampleNameIfNotOutcrop", sample.getFeature().getFeatureName());
			    template.addSub("sampleStatusIfNotOutcrop", sample.getAudit().getStatus());
			    template.addSub("curatorCommentsIfNotOutcrop", sample.getAudit().getCuratorComments());
			    template.addSub("frNumberIfNotOutcrop", (sample.getFeature().getFrNumber() != null) ? sample.getFeature().getFrNumber().getFrNumber() : "");
			    template.addSub("workingCommentsIfNotOutcrop", sample.getAudit().getWorkingComments());
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
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
	}
	
	public int save(int dataOriginId) throws StorageAccessException, InsufficientPrivelegesException {
		if (!isAllowedSave)
			throw new InsufficientPrivelegesException("Insufficient rights to save this sample");

		if (sample.getSampleId() == null && !outcropSample) {
			//It's an insert
			Audit audit = sample.getAudit();
			audit.setStatus(FREDConstants.WORKING);
			audit.setCreatedById(user.getPersonId());
			audit.setCreatedDate(new Date());
			audit.setDataOrigin((new AuditUtil(factory)).getDataOrigin(new Integer(dataOriginId)));
		} 

        sampleUtil.saveOrUpdate(sample);
    		
		return sample.getSampleId().intValue();
	
	}

	public int submit(int dataOriginId) throws InsufficientPrivelegesException, DataInputException, StorageAccessException {
		save(dataOriginId);
		sampleUtil.submitSample(sample, workingFolder, user);
		return sample.getSampleId().intValue();
	}

	public List<IconnedLink> getNavigation() {
		List<IconnedLink> links = new Vector<IconnedLink>(4);
		String args = ((workingFolder == null) ? "?q" : ("?FoldID=" + workingFolder.getFolderId())) 
			+ ((sample.getSampleId() == null) ? "" : ("&SampID=" + sample.getSampleId()))
			+ "&RecType=Sample";
		links.add(new IconnedLink("load_record.jsp" + args, "images/load.gif", "Copy From"));
		links.add(new IconnedLink("javascript:submitForm('Save');", "images/save.gif", "Save"));
		if (isAllowedSubmit)
			links.add(new IconnedLink("javascript:submitForm('Submit');", "images/submit.gif", "Submit"));
		
		return links;
	}

	public void makePostFormHTML(PrintWriter out) throws IOException {
		Template template = provider.getContent("calendar.script");
		template.addSub("inputField", "CollDate");
		template.addSub("button", "CollDateCal");
		template.loadAll(out);
		template = provider.getContent("sample.postform");
		template.loadAll(out);
	}

	public void updateFromRequest(HttpServletRequest request, DAOFactory factory, boolean addIfNew) throws DataInputException {
        reinitialise(factory);

        Vector<String[]> error = new Vector<String[]>();
        
        //Drillhole/Vert Section depths
        if (!sample.getFeature().getFeatureType().equals(FREDConstants.OUTCROP)) {
        	Double topDepth = null;
        	Double bottomDepth = null;
        	if (request.getParameter("TopDepth").length() > 0) try {
		        topDepth = new Double(request.getParameter("TopDepth"));
        	} catch (Exception e) {
        		error.add(new String[] {"Top Depth/Height", "Non-numeric value"});
        	}
        	if (request.getParameter("BottomDepth").length() > 0) {
        		try {
        			bottomDepth = new Double(request.getParameter("BottomDepth"));
        		} catch (Exception e) {
        			error.add(new String[] {"Bottom Depth/Height", "Non-numeric value"});
        		}
	        	if ("Bottom".equals(sample.getFeature().getDatumType())) {
	        		if (topDepth.doubleValue() < bottomDepth.doubleValue())
	        			error.add(new String[] {"Depths/Heights", "Top depth/height < bottom depth/height and datum = Bottom"});
	        	} else {
	        		if (topDepth.doubleValue() > bottomDepth.doubleValue()) {
	        			error.add(new String[] {"Depths", "Top depth > bottom depth"});
	        		}
	        	}
        	}
        	sample.setTopDepth(topDepth);
        	sample.setBottomDepth(bottomDepth);
        	String depthUnit = request.getParameter("DepthUnit");
        	if ("ft".equals(depthUnit))
        		sample.setDepthUnit("ft");
        	else
        		sample.setDepthUnit("m");
    		String drillType = request.getParameter("DrillType");
    		if (drillType != null) { //catch no drilltype for vert sects
    			try {
    				sample.setDrillType(getDrillType(request.getParameter("DrillType")));
    			} catch (Exception e) {
    				error.add(new String[] {"Drill Type", "Invalid Drill Type"});
    			}
    		}
		}
        
		//Collection date
		try {
			String collectionDate = request.getParameter("CollDate");
			sample.setCollectionDate(FREDUtil.parseDateFromDE(collectionDate));
			sample.setDateRounding(FREDUtil.parseDateRoundingFromDE(collectionDate));
		} catch (ParseException e) {
			error.add(new String[] {"Collection Date", "Badly formatted date"});
		}
		
		//Collectors
        PersonUtil personUtil = new PersonUtil(factory);
        try {
        	String[] collectors = request.getParameterValues("Coll");
        	//This is to support the spreadsheet
        	if (collectors.length <= 1)
        		sample.setCollectors(FREDUtil.getPersons(request.getParameter("Coll"), personUtil, "Collectors", addIfNew));
        	else 
        		sample.setCollectors(FREDUtil.getPersons(collectors, personUtil, "Collectors", false));
        } catch (DataInputException e) {
            if (e.hasAuxiliaryData()) {
                sample.setCollectors((Set<Person>)e.getAuxiliaryData());
            }
            error.addAll(e.getError());
        }

		//Strat name
		sample.setStratUnit(request.getParameter("StratName"));
		
		//In place
		String inPlace = request.getParameter("InPlace");
		if (inPlace.length() == 0)
			sample.setInPlace(null);
		else {
			if (inPlace.equals("Yes") || inPlace.equals("No") || inPlace.equals("Almost") || inPlace.equals("Unknown"))
				sample.setInPlace(inPlace);
			else
				error.add(new String[] {"In Place", inPlace + " is not valid. In Place must be either 'Yes', 'No', 'Almost' or 'Unknown'"});
		}
		
		//Sent to
        String sentToParam = request.getParameter("SentTo").trim();
        HashSet<SentTo> sentToSet = new HashSet<SentTo>();
        if (sentToParam.length() > 0) {
    		String[] sentTos = request.getParameter("SentTo").split("\\n");
    		for (String sentTo : sentTos) try {
    			String[] parts = sentTo.split("\\*");
    			FossilGroup group = (parts[0].length() == 0) ? null : sampleUtil.getFossilGroup(parts[0]);
    			if (parts[0].length() > 0 && group == null)
    				error.add(new String[] {"Sent To", "Invalid group: " + parts[0]});
    			Person person = (parts.length >= 2 && parts[1].length() != 0) ? (addIfNew ? personUtil.findOrCreatePerson(parts[1]) : personUtil.findPerson(parts[1])) : null;
    			if (parts.length >= 2 && parts[1].length() > 0 && person == null)
    				error.add(new String[] {"Sent To", "Invalid person: " + parts[1]});
    			Integer lab = (parts.length >= 3 && parts[2].length() != 0) ? FREDUtil.getLabId(parts[2]) : null;
    			if (parts.length >= 3 && parts[2].length() > 0 && lab == null)
    				error.add(new String[] {"Sent To", "Invalid lab: " + parts[2]});
    			String comments = ((parts.length >= 4) ? parts[3].replaceAll(String.valueOf((char)13), "") : null);
    			if (comments != null && comments.length() == 0)
    				comments = null;
    			sentToSet.add(sampleUtil.findOrCreateSentTo(sample, group, person, lab, comments));
    		} catch (Exception e) {
                e.printStackTrace();
    			error.add(new String[] {"Sent To", "Database error: " + e.getMessage()});
    		}
        }
		Set<SentTo> oldSentTos = sample.getSentTos();
		sample.setSentTos(sentToSet);
		if (oldSentTos != null) {
			oldSentTos.removeAll(sentToSet);	
    		for (SentTo sentTo : oldSentTos) try {
				sampleUtil.delete(sentTo);
			} catch (StorageAccessException e) {
				e.printStackTrace();
			}
    	}
        
        //Not collected?
        sample.setNotCollected(request.getParameter("NotColl"));
        sample.setSignificance(request.getParameter("Sig"));
        
		//Work out the inferred stage
        try {
            sample.setInferredStage(StageDEUtil.getStage(request, "Inf", sample.getInferredStage(), sampleUtil, "Inferred stage"));
        } catch (DataInputException e) {
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
				e.printStackTrace();
				error.add(new String[] {"Previous sample", e.getMessage()});
			}
		} 
		//Remove any that are still in the old set
		if (previousSample != null) {
			relationships.removeAll(previousSample);
			for (Relationship rel : previousSample) try {
				sampleUtil.delete(rel);
			} catch (StorageAccessException e) {
				e.printStackTrace();
			}
		}
	
		//Next up - sample relationships
		if (request.getParameter("SampRel").length() > 0) {
			//Go through each entered relationship
			for (String relationshipDesc : request.getParameter("SampRel").split("\\n")) try {
				Relationship newRelationship = sampleUtil.decodeSampleRelationshipDescription(relationshipDesc);
				newRelationship.setSample(sample);
				boolean found = false;
				for (Iterator<Relationship> it = sampleRel.iterator(); it.hasNext(); ) {
					Relationship rel = it.next();
					if (sampleUtil.isMatchingRelationship(rel, newRelationship)) {
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
				e.printStackTrace();
				error.add(new String[] {"Sample relationships", e.getMessage()});
			}
		}
		//Remove any that are still in the old set
		if (sampleRel != null) {
			relationships.removeAll(sampleRel);
			for (Relationship rel : sampleRel) try {
				sampleUtil.delete(rel);
			} catch (StorageAccessException e) {
				e.printStackTrace();
			}
		}

		//Lastly - strat relationships
		if (request.getParameter("StratRel").length() > 0) {
			//Go through each entered relationship
			for (String relationshipDesc : request.getParameter("StratRel").split("\\n")) try {
				Relationship newRelationship = sampleUtil.decodeStratigraphicRelationshipDescription(relationshipDesc);
				newRelationship.setSample(sample);
				boolean found = false;
				for (Iterator<Relationship> it = stratRel.iterator(); it.hasNext(); ) {
					Relationship rel = it.next();
					if (sampleUtil.isMatchingRelationship(rel, newRelationship)) {
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
		if (stratRel != null) {
			relationships.removeAll(stratRel);
			for (Relationship rel : stratRel) try {
				sampleUtil.delete(rel);
			} catch (StorageAccessException e) {
				e.printStackTrace();
			}
		}
		
		//Column map
		sample.setColumnMap(request.getParameter("ColMap"));
		
		//Dip
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
		
		//Dip Direction
		String dipDir = request.getParameter("DipDir");
		if (dipDir.length() == 0)
			sample.setDipDirection(null);
		else {
			if (dipDir.equals("N") || dipDir.equals("NE") || dipDir.equals("E") || dipDir.equals("SE") || dipDir.equals("S") || dipDir.equals("SW") || dipDir.equals("W") || dipDir.equals("NW"))
				sample.setDipDirection(dipDir);
			else
				error.add(new String[] {"Dip Direction", dipDir + " is not valid. Dip Direction must be either 'N', 'NE', 'E', 'SE', 'S', 'SW', 'W' or 'SW'"});
		}
		
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
		else {
			if (facing.equals("Normal") || facing.equals("Overturned"))
				sample.setFacing(facing);
			else
				error.add(new String[] {"Facing", facing + " is not valid. Facing must be either 'Normal' or 'Overturned'"});
		}
		
		//Grainsize
		String grainSizeP = request.getParameter("GrainSizeP");
		try {
			sample.setPrimaryGrainSize(getGrainSize(grainSizeP));
			String grainSizeS = request.getParameter("GrainSizeS");
			if (FREDUtil.decodeCombo(grainSizeP) == null && FREDUtil.decodeCombo(grainSizeS) != null)
				error.add(new String[] {"Grain Size", "Primary Grain Size must be entered if entering Secondary Grain Size"});
			else 
				sample.setSecondaryGrainSize(getGrainSize(grainSizeS));
		} catch (StorageAccessException e) {
			error.add(new String[] {"Grain Size", "Database problem: " + e.getMessage()});
		}	
		String gsComp = request.getParameter("GSComp");
		if (FREDUtil.isEmpty(gsComp))
			sample.setComparatorUsed(null);
		else {
			if (FREDUtil.decodeCombo(grainSizeP) == null) {
				error.add(new String[] {"Comparator Used", "Primary grain size must be entered if entering Comparator Used"});
			} else {
				if (gsComp.equals("Y") || gsComp.equals("N"))
					sample.setComparatorUsed(gsComp);
				else
					error.add(new String[] {"Comparator Used", gsComp + " is not valid. Comparator Used must be either 'Y' or 'N'"});
			}
		}
		
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
		
		//Wet/Dry
		String wet = request.getParameter("Wet");
		if (wet.length() == 0)
			sample.setWet(null);
		else {
			if (wet.equals("Wet") || wet.equals("Dry"))
				sample.setWet(wet);
			else
				error.add(new String[] {"Wet/Dry", wet + " is not valid. Wet/Dry must be either 'Wet' or 'Dry'"});
		}
		
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
		String depEnv1 = request.getParameter("DepEnv1").trim();
		if (depEnv1 == null || depEnv1.length() == 0)
			depEnv1 = getDepositionalEnvironmentMarineOrNot(request.getParameter("DepEnv2"));
		String depEnv2 = getDepositionalEnvironmentFreeText(request.getParameter("DepEnv2"));
		if (depEnv1 != null)
			depEnv.append(depEnv1);
		if (depEnv.length() > 0 && depEnv2 != null && depEnv2.length() > 0)
			depEnv.append(": ");
		if (depEnv2 != null)
			depEnv.append(depEnv2);
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
            if (copySample != null) {
            	getFromDatabase(copySample);
            	copySample = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private DrillType getDrillType(String parameter) throws NumberFormatException, StorageAccessException {
		parameter = FREDUtil.decodeCombo(parameter);
		if (parameter == null)
			return null;
		return sampleUtil.getDrillType(new Integer(parameter));
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
