var isChromium = window.chrome;
var winNav = window.navigator;
var vendorName = winNav.vendor;
var isOpera = typeof window.opr !== "undefined";
var isIEedge = winNav.userAgent.indexOf("Edge") > -1;
var isIOSChrome = winNav.userAgent.match("CriOS");

if (isIOSChrome) {
    // is Google Chrome on IOS
} else if(
    isChromium !== null &&
    typeof isChromium !== "undefined" &&
    vendorName === "Google Inc." &&
    isOpera === false &&
    isIEedge === false
) {
    // is Google Chrome
} else {
    // not Google Chrome

    if (navigator.userAgent.search("Firefox") === -1){
        alert('Alerta: Es posible que en este navegador la página no se visualice correctamente. ' +
            'Estoy trabajando en ello. Mientras tanto, agradezco que uses la web con uno de los navegadores soportados: Google Chrome o Mozilla Firefox');
    }

}