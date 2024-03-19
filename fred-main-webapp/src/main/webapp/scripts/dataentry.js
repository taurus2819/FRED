/* 
 * Utility functions for data entry forms. 
 */

function requiredInput(inputName, errorRow, errorCell, message) {
    input = document.getElementById(inputName);
    input.addEventListener("change", function(e) {
        e = document.getElementById(inputName);
        if (e.hasAttribute("required")) {
            var v = e.value;
            if (v === "") {
                e.setCustomValidity(message);
                document.getElementById(errorCell).innerHTML = message;
                document.getElementById(errorRow).removeAttribute("hidden");
            } else {
                e.setCustomValidity("");
                document.getElementById(errorRow).setAttribute("hidden", "hidden");
            }
        }
        updateSubmit();
        updateSave();
    });
    input.addEventListener("invalid", function(e) {
        document.getElementById(inputName).setCustomValidity(message);
        document.getElementById(errorCell).innerHTML = message;
        document.getElementById(errorRow).removeAttribute("hidden");
    });
}

function updateSubmit() {
    const submit = document.getElementById("submit-button");
    if (submit === null) {
        return;
    }
    const invalid = document.querySelectorAll("input:invalid, textarea:invalid");
    if (invalid.length > 0) {
        submit.setAttribute("hidden", "hidden");
    } else {
        submit.removeAttribute("hidden");
    }
}

function updateSave() {
    const save = document.getElementById("save-button");
    if (save === null) {
        return;
    }
    const invalid = document.querySelectorAll("input:invalid, textarea:invalid");
    let validCoordinates = true;
    for (let i = 0; i < invalid.length; i++) {
        let id = invalid[i].id;
        if (id === "eastInput" || id === "northInput" ||
            id === "coordTypeInput" || id === "MapSheetInput") {
            // Valid co-ordinates are required before the record can be
            // saved
            validCoordinates = false;
            save.setAttribute("hidden", "hidden");
            document.getElementById("coordRequiredMessage").removeAttribute("hidden");
            return;
        }
    }
    save.removeAttribute("hidden");
    document.getElementById("coordRequiredMessage").setAttribute("hidden", "hidden");
}

function checkOnLoad() {
    // Add a leading "+" sign to the Longitude
    // if it's not there, the plus sign gets dropped on the round trip to/from
    // the backend.
    const box = document.getElementById("coordTypeInput");
	switch (box.selectedIndex) {
		case 7:
		case 8:
		case 9:
        case 10:
            const element = document.getElementById("northInput");
            const value   = element.value;
            if (value && !value.startsWith("+") && !value.startsWith("-")) {
                element.value = "+" + value;
            }
	}
    // Once the data has been adjusted, validate the required
    // fields.
    const required = document.querySelectorAll("[Required]");
    for (let i=0; i < required.length; i++) {
        required[i].checkValidity();
    }
    updateSave();
    updateSubmit();
}

const NZMG_EASTING_VALIDATION = {
    pattern: /^\d{7}(\.\d)?$/,
    error: " must be a number between 0 and 9999999",
    min: 0,
    max: 9999999
};
const NZMG_NORTHING_VALIDATION = {
    pattern: /^\d{7}(\.\d)?$/,
    error: " must be a number between 0 and 9999999",
    min: 0,
    max: 9999999
};
const NZTM_EASTING_VALIDATION = {
    pattern: /^\d{7}(\.\d)?$/,
    error: " must be a number between 0 and 9999999",
    min: 0,
    max: 9999999
};
const NZTM_NORTHING_VALIDATION = {
    pattern: /^\d{7}(\.\d)?$/,
    error: " must be a number between 0 and 9999999",
    min: 0,
    max: 9999999
};

const MS_EASTING_VALIDATION = {
    pattern: /^\d{3,4}$/,
    error: " must be a number between 0 and 9999",
    min: 0,
    max: 9999
};
const MS_NORTHING_VALIDATION = {
    pattern: /^\d{3,4}$/,
    error: " must be a number between 0 and 9999",
    min: 0,
    max: 9999
};

const LATITUDE_VALIDATION = {
    pattern: /^[-+]?\d{1,3}(\.\d{0,4})?$/,
    error: " must be a number between -179.9999 and 180.0000",
    min: -180,
    max: 180
};
const LONGITUDE_VALIDATION = {
    pattern: /^[-+].\d{1,2}(\.\d{0,4})?$/,
    error: " must be a number between -90 and +90 and sign is required",
    min: -90,
    max: 90
};

function validateEasting(inputName, errorRow, errorCell) {
    let rules = new Map();
    rules.set("NZMG", NZMG_EASTING_VALIDATION);
    rules.set("NZMS260", MS_EASTING_VALIDATION);
    rules.set("NZTM", NZTM_EASTING_VALIDATION);
    rules.set("NZTopo50", MS_EASTING_VALIDATION);
    rules.set("NZ Yard SthIsl", MS_EASTING_VALIDATION);
    rules.set("NZ Yard NthIsl", MS_EASTING_VALIDATION);
    rules.set("NZMS1 SthIsl", MS_EASTING_VALIDATION);
    rules.set("NZMS1 NthIsl", MS_EASTING_VALIDATION);
    rules.set("NZGD49", LATITUDE_VALIDATION);
    rules.set("Chatham Island Datum", LATITUDE_VALIDATION);
    rules.set("NZGD2000", LATITUDE_VALIDATION);
    rules.set("WGS84", LATITUDE_VALIDATION);

    input = document.getElementById(inputName);
    input.addEventListener(
        "change",
        getCordinateValidationFunction(
            inputName, errorRow, errorCell, "easting", rules));
}

function validateNorthing(inputName, errorRow, errorCell) {
    let rules = new Map();
    rules.set("NZMG", NZMG_NORTHING_VALIDATION);
    rules.set("NZMS260", MS_NORTHING_VALIDATION);
    rules.set("NZTM", NZTM_NORTHING_VALIDATION);
    rules.set("NZTopo50", MS_EASTING_VALIDATION);
    rules.set("NZ Yard SthIsl", MS_NORTHING_VALIDATION);
    rules.set("NZ Yard NthIsl", MS_NORTHING_VALIDATION);
    rules.set("NZMS1 SthIsl", MS_NORTHING_VALIDATION);
    rules.set("NZMS1 NthIsl", MS_NORTHING_VALIDATION);
    rules.set("NZGD49", LONGITUDE_VALIDATION);
    rules.set("Chatham Island Datum", LONGITUDE_VALIDATION);
    rules.set("NZGD2000", LONGITUDE_VALIDATION);
    rules.set("WGS84", LONGITUDE_VALIDATION);

    input = document.getElementById(inputName);
    input.addEventListener(
        "change",
        getCordinateValidationFunction(
            inputName, errorRow, errorCell, "northing", rules));
}

function getCordinateValidationFunction(
        inputName, errorRow, errorCell, label, rules) {
    return function(ev) {
        const el = document.getElementById(inputName);
        const name  = document.getElementById(label).innerHTML;
        const value = el.value;
        const type  = document.getElementById("coordTypeInput").value;
        const rule  = rules.get(type);
        if (!value || !value.trim()) {
            const message = "A value is required for " + name;
            el.setCustomValidity(message);
            document.getElementById(errorCell).innerHTML = message;
            document.getElementById(errorRow).removeAttribute("hidden");
        }
        else if (!rule.pattern.test(value) || value < rule.min || value > rule.max) {
            const message = "For " + type +", " + name + rule.error;
            el.setCustomValidity(message);
            document.getElementById(errorCell).innerHTML = message;
            document.getElementById(errorRow).removeAttribute("hidden");
        } else {
            el.setCustomValidity("");
            document.getElementById(errorRow).setAttribute("hidden", "hidden");
            crossValidateCoordinates();
        }
        updateSubmit();
        updateSave();
    };
}

function crossValidateCoordinates() {
    const type  = document.getElementById("coordTypeInput").value;
    if (type === "NZMS260") {
        const message = "For NZMS260 the Northing and Easting co-ordinates must be the same length";
        // For NZMS260 the northing and easting are:
        // EITHER both 4 digits long or both 3 digits long.
        const ids = ["eastInput", "northInput"];
        for (let i = 0; i < ids.length; i++) {
            let id = ids[i];
            const m = document.getElementById(id).validationMessage;
            // Only perform the cross validation if both fields are valid
            if (m && !m.trim() && el.validationMessage !== message) {
                return;
            }
        }
        const northingEl = document.getElementById("northInput");
        const northingValue = northingEl.value;
        const eastingEl = document.getElementById("eastInput");
        const eastingValue = eastingEl.value;
        if (eastingValue && eastingValue.trim && northingValue && northingValue.trim() &&
                eastingValue.length !== northingValue.length) {
            // set error message for northing
            northingEl.setCustomValidity(message);
            document.getElementById("northErrorText").innerHTML = message;
            document.getElementById("northErrorRow").removeAttribute("hidden");
            // set error message for easting
            eastingEl.setCustomValidity(message);
            document.getElementById("eastErrorText").innerHTML = message;
            document.getElementById("eastErrorRow").removeAttribute("hidden");
        } else {
            northingEl.setCustomValidity("");
            document.getElementById("northErrorRow").setAttribute("hidden", "hidden");
            document.getElementById("northErrorText").innerHTML = "";
            eastingEl.setCustomValidity("");
            document.getElementById("eastErrorRow").setAttribute("hidden", "hidden");
            document.getElementById("eastErrorText").innerHTML = "";
        }
    }
}
