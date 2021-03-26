function cambiarSeleccion(span) {

        $("button[data-competicion='"+  $(span).attr("data-competicion") +"']")[0].disabled = false;
        if(span.classList.contains('categoriaDeseleccionada')){
            span.classList.remove('categoriaDeseleccionada');
            span.classList.add('categoriaSeleccionada');
        } else {
            span.classList.remove('categoriaSeleccionada');
            span.classList.add('categoriaDeseleccionada');
        }
}

function inscribirse(button) {
    var spansCategorias = $("span.categoriaSeleccionada[data-competicion='"+  $(button).attr("data-competicion") +"']").toArray();
    var categorias = spansCategorias.map(s => $(s).attr("data-categoria"));

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && (this.status === 200 || this.status === 401)) {
            alert(xhttp.responseText);
            location.reload();
        } else if(this.readyState === 4) {
            alert('Esto es un error, por favor, contacta con el desarrollador y muéstrale lo siguente: '+ xhttp.responseText);
        }
    };
    xhttp.open("POST", "/inscripcion/"+ $(button).attr("data-competicion"), true);
    xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhttp.send(JSON.stringify(categorias));
}