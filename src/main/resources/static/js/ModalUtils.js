function loadBracket(nombreCategoria) {

    httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function () {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                document.getElementById("cuerpoModal").innerHTML = self.getEventContent(httpRequest.responseText).outerHTML;
            }
        }
    };
    httpRequest.open('GET', window.location.origin + '/calendario/playoffs/Nacionline 2020/' + nombreCategoria); //TODO nombre campeonato
    httpRequest.send();




}

function getEventContent(string) {
    // reset the loaded event content so that it can be inserted in the modal
    var div = document.createElement('div');
    div.innerHTML = string.trim();
    return div;
};


