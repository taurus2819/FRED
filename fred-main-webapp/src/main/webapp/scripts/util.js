/* Return the fully qualified host name as
 * determined from the current location
 * 
 */
function getFQHostName() {
    if(window.location.hostname === 'localhost'){
        return "https://data-uat.gns.cri.nz";
    } else {
        var protocol = location.protocol;
        var slashes = protocol.concat("//");
        var hostname = window.location.hostname;
        var host = slashes.concat(hostname);
        return host;
    }
}

