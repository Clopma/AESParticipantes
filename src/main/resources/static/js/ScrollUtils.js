function scrollAFila() {

    $(window).scrollTop(0);
    var url = decodeURIComponent(window.location.toString());
    var p = document.getElementById(url.substring(url.indexOf("#") + 1));


    if (p) {
        p.classList.add("participanteEncontrado");

        let page = $("html, body");
        page.on("scroll mousedown wheel DOMMouseScroll mousewheel keyup touchmove", function () {
            page.stop();
        });

        var goto = p.getBoundingClientRect().top + document.body.scrollTop - (window.innerHeight/2) + (p.scrollHeight/2);
        var max = document.documentElement.scrollHeight - window.innerHeight;

        page.animate({scrollTop: goto > max ? max : goto}, 2000, 'easeInOutQuint', function () {
            page.off("scroll mousedown wheel DOMMouseScroll mousewheel keyup touchmove");
        });
    }


}