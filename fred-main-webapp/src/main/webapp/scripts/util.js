/* Return the fully qualified host name as
 * determined from the current location
 * 
 */
function getFQHostName() {
    var protocol = location.protocol;
    var slashes = protocol.concat("//");
    var hostname =(window.location.hostname ==='localhost' ? 'data-dev.gns.cri.nz' : window.location.hostname);
    var host = slashes.concat(hostname);
    return host;
}

function getDataHostName() {
    var host = getFQHostName();
    return (host.indexOf('fred.org.nz') !== -1) ? 'data.gns.cri.nz' : host;
}

