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

function checkOnLoad() {
    const required = document.querySelectorAll("[Required]");
    console.log("Event triggered.");
    for (let i=0; i < required.length; i++) {
        required[i].checkValidity();
        console.log(required[i]);
    }
}