
function getTiempo(divTiempo) {



    if (!divTiempo || divTiempo.children().length === 0) { //Cuando hay menos de 5 mezclas
        return 0;
    } //TODO: https://trello.com/c/zx5rFvfH

    var minutos = !divTiempo.find(".minutos")[0].disabled && divTiempo.find(".minutos")[0].value ? divTiempo.find(".minutos")[0].value : 0;
    var segundos = !divTiempo.find(".segundos")[0].disabled && divTiempo.find(".segundos")[0].value ?  divTiempo.find(".segundos")[0].value : 0;
    var centesimas;
    if(!divTiempo.find(".centesimas")[0].disabled && divTiempo.find(".centesimas")[0].value){
        if(divTiempo.find(".centesimas")[0].value.length < 2){
            alert('Introduce al menos dos decimales en las centésimas, aunque acabe en 0. Esto es para prevenir confusiones.');
            return -1;
        }
        centesimas = ((divTiempo.find(".centesimas")[0].value) + "").substr(0, 2).padEnd(2, "0");
    } else {
        centesimas = 0;
    }

    if(!divTiempo.find(".mas2cb")[0].disabled && divTiempo.find(".mas2cb")[0].checked) {segundos = segundos*1/*to int*/ + 2;}
    if(!divTiempo.find(".mas2cb")[0].disabled && divTiempo.find(".mas4cb")[0].checked) {segundos = segundos*1/*to int*/ + 4;}


    return parseFloat(+(minutos * 60) + +segundos + +(centesimas / 100.0)).toPrecision(9);


}
