function loadBracket(nombreCategoria, nombreCompeticionOTemporada, elementId, isCompeticion) {

    httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById(elementId).innerHTML = self.getEventContent(httpRequest.responseText).outerHTML;
            } else  if (httpRequest.status === 404) {
                document.getElementById(elementId).innerHTML = "<h2>Ha habido un error en la carga del bracket, por favor comunícaselo al desarrollador.</h2>";
            }
        }
    };
    httpRequest.open('GET', window.location.origin + '/bracket/'+ (isCompeticion ? 'competicion/' : 'temporada/') + nombreCompeticionOTemporada +'/' + nombreCategoria); //TODO nombre campeonato
    httpRequest.send();


}

function getEventContent(string) {
    // reset the loaded event content so that it can be inserted in the modal
    var div = document.createElement('div');
    div.innerHTML = string.trim();
    return div;
};


