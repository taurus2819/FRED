/**
 * Provides specific javascript functions for the result_list.jsp page.
 */


/*
 * Adds a concatenated list of feature ID's to the folderForm's action url
 */
function addFeaturesToActionURL(folderForm) {
	var urlText = "";
	var featuresList = document.getElementsByName("FeatIDs");
	for (i=0;i<featuresList.length;i++){
		if (featuresList[i].checked)
			urlText = urlText + "&fid=" + featuresList[i].value;
	}
	folderForm.action += urlText;
}


/* Loops through feature checkboxes and determines whether the master 
 * checkbox should be selected.
 */
function updateMasterCheckbox() {
	var allChecked = true;
	var features = document.getElementsByName('FeatIDs');	
	for (var i = 0; i < features.length; i++){
		if (!features[i].checked){
			allChecked = false;
			break;
		}
	}
	document.getElementsByName("MasterCheckbox")[0].checked = allChecked;
}


/*
 * Updates all checkboxes to parameter value isChecked
 */
function updateAllCheckBoxes(isChecked) {
	document.getElementsByName("MasterCheckbox")[0].checked = isChecked;
	var features = document.getElementsByName('FeatIDs');	
	for (var i = 0; i < features.length; i++){
		features[i].checked = isChecked;
	}	
}
