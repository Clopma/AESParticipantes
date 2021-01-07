
function formatTiempos(text, categoria, numTiempos, jornada) {

    var re = /(\d{1,2}):(\d{1,2})(,(\d{1,2}))?/g;
    var match;
    var CONTROL_EXP = "#####!!!!!";

    //paso minutos a segundos
    do {
        match = re.exec(text);
        if (match) {
            const matchInSeconds = (parseInt(match[1]) * 60 + parseInt(match[2])) + (match[3] ? "," + match[4]: "");
            text = text.replace(match[0], matchInSeconds)
            re.lastIndex = 0;
        }
    } while (match);

    //cambio comas por puntos
    var re2 = /(\d+),(\d{1,2})/g;

    do {
        match = re2.exec(text);
        if (match) {
            const matchWithDot = match[1] + "." + match[2];
            text = text.replace(match[0], matchWithDot)
        }
    } while (match);


    var newText = "INSERT INTO tiempos (jornada, categoria_nombre, posicion, participante_nombre, tiempo1, tiempo2, tiempo3, tiempo4, tiempo5, puntos_tiempo, puntos_bonus" +(categoria === 'FMC' ? ", solucion, explicacion" : "")+") VALUES \n";


    var reExp = /\$\$\$((.|\n)*?)€€€/g;
    var explicaciones = [];
    do {
        match = reExp.exec(text);
        if (match) {
            explicaciones.push(match[1])
            text = text.replace(match[0], CONTROL_EXP);
            reExp.lastIndex = 0;
        }
    } while (match);


    //Por cada linea
    var re3 = /\n(.+)/g;
    text = "\n" + text;
    var i = 0;
    do {

        var linea = re3.exec(text);

        if (linea) {


            //Encuentro los campos
            var re5 = /[\t\n]([-'a-zA-ZÀ-ÖØ-öø-ÿ()€$ \d\.]+)/g;
            var campos = [];
            do {
                match = re5.exec(linea[0]);
                if (match) {
                    campos.push(match[1])
                }
            } while (match);


            newText += "(" + jornada + ",\t\"" + categoria + "\",\t" + campos[0] + ",\t\"" + campos[1] + "\",\t"
            + checkDNF(campos[2]) + ",\t";
            if(categoria !== 'FMC') {
                newText += checkDNF(campos[3]) + ",\t" + checkDNF(campos[4]) + ",\t"
                + (numTiempos > 3 ? checkDNF(campos[5]) + ",\t" + checkDNF(campos[6]) + ",\t" : 0 + ",\t" + 0 + ",\t")
                + campos[(9 - (5 - numTiempos)) - (categoria === 'BLD' ? 1 : 0)] + ",\t" + campos[(10 - (5 - numTiempos)) - (categoria === 'BLD' ? 1 : 0)] + "),\n"; //Ignorar columnas que no estarán
            } else {
                var dnf = false;
                if(campos[2] === 'DNF') {dnf = true;}
                newText += 0 + ",\t" + 0 + ",\t"+ 0 + ",\t"+ 0 + ",\t" + campos[4] + ",\t" + campos[5] + ",\t\"" + (dnf ? "-" : campos[3]) + "\",\t\""  + (explicaciones[i].trim() === "DNF" ? "-" : explicaciones[i]) + "\"),\n";
            }
        }
        i++
    } while (linea);



    return newText.substr(0, newText.length - 2) + ";";

}


function checkDNF(tiempo) {
    return tiempo === 'DNF' ? 0 : tiempo;


}