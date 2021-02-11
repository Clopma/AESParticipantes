class Scrambler {

    constructor(puzzle, div, autorefresh, colours) {

        if (['2x2x2', '3x3x3', '4x4x4', '5x5x5', '6x6x6', '7x7x7'].includes(puzzle)) {
            this.puzzle = new NxN(colours, parseInt(puzzle.substr(0, 2)));
        }
        if ('Skewb' === puzzle) {
            this.puzzle = new Skewb(colours);
        }

        this.autorefresh = autorefresh;
        this.container = div;
        this.draw();
    };


    draw(){
        this.wrapper = document.createElement('div');
        this.wrapper.className = 'scrambleDrawWrapper';
        this.puzzle.setWrapperHTML(this.wrapper);

        this.container.innerHTML = "";
        this.container.append(this.wrapper);

    }


    scramble(scramble){
       var autorefreshConfig = this.autorefresh; // Disable autorefresh temporally
       this.autorefresh = false;
       var movements = scramble.split(' ');
       for(var i = 0; i < movements.length; i++){
           var move = movements[i];
           var clockwise = !move.includes('\'');
           this.doMove(move, clockwise);
           if(move.includes('2')){this.doMove(move, clockwise)}
       }
       this.autorefresh = autorefreshConfig; // Recover autorefresh
       this.draw();
    }

    doMove(move, clockwise){
        if(move.includes('B')){move.includes('w') ? move.includes('3') ? (clockwise ? this.bw3() : this.bwAnticlockwise3()) : (clockwise ? this.bw() : this.bwAnticlockwise())    : (clockwise ? this.b() : this.bAnticlockwise())}
        if(move.includes('F')){move.includes('w') ? move.includes('3') ? (clockwise ? this.fw3() : this.fwAnticlockwise3()) : (clockwise ? this.fw() : this.fwAnticlockwise())    : (clockwise ? this.f() : this.fAnticlockwise())}
        if(move.includes('L')){move.includes('w') ? move.includes('3') ? (clockwise ? this.lw3() : this.lwAnticlockwise3()) : (clockwise ? this.lw() : this.lwAnticlockwise())    : (clockwise ? this.l() : this.lAnticlockwise())}
        if(move.includes('R')){move.includes('w') ? move.includes('3') ? (clockwise ? this.rw3() : this.rwAnticlockwise3()) : (clockwise ? this.rw() : this.rwAnticlockwise())    : (clockwise ? this.r() : this.rAnticlockwise())}
        if(move.includes('D')){move.includes('w') ? move.includes('3') ? (clockwise ? this.dw3() : this.dwAnticlockwise3()) : (clockwise ? this.dw() : this.dwAnticlockwise())    : (clockwise ? this.d() : this.dAnticlockwise())}
        if(move.includes('U')){move.includes('w') ? move.includes('3') ? (clockwise ? this.uw3() : this.uwAnticlockwise3()) : (clockwise ? this.uw() : this.uwAnticlockwise())    : (clockwise ? this.u() : this.uAnticlockwise())}
    }

    autoRefresh(){
       if (this.autorefresh){this.draw();}
    }

    r() {this.puzzle.execR(true, 0); this.autoRefresh();} rAnticlockwise() {this.puzzle.execR(false,0); this.autoRefresh();}
    r2() {this.r(); this.r(); this.autoRefresh();} rw2() {this.rw(); this.rw(); this.autoRefresh();} rw32() {this.rw3(); this.rw3(); this.autoRefresh();}
    rw() {this.puzzle.execR(true, 0); this.puzzle.execR(true, 1); this.autoRefresh();} rwAnticlockwise() { this.puzzle.execR(false, 0); this.puzzle.execR(false, 1); this.autoRefresh();}
    rw3() {this.puzzle.execR(true, 0); this.puzzle.execR(true, 1); this.puzzle.execR(true, 2); this.autoRefresh();} rwAnticlockwise3() { this.puzzle.execR(false, 0); this.puzzle.execR(false, 1); this.puzzle.execR(false, 2); this.autoRefresh();}

    l() { this.puzzle.execL(true, 0); this.autoRefresh();} lAnticlockwise() {this.puzzle.execL(false, 0); this.autoRefresh();}
    l2() {this.l(); this.l(); this.autoRefresh();} lw2() {this.lw(); this.lw(); this.autoRefresh();} lw32() {this.lw3(); this.lw3(); this.autoRefresh();}
    lw() { this.puzzle.execL(true, 0); this.puzzle.execL(true, 1); this.autoRefresh();} lwAnticlockwise() {this.puzzle.execL(false, 0); this.puzzle.execL(false, 1); this.autoRefresh();}
    lw3() { this.puzzle.execL(true, 0); this.puzzle.execL(true, 1); this.puzzle.execL(true, 2); this.autoRefresh();} lwAnticlockwise3() {this.puzzle.execL(false, 0); this.puzzle.execL(false, 1); this.puzzle.execL(false, 2); this.autoRefresh();}

    u() {this.puzzle.execU(true, 0); this.autoRefresh();} uAnticlockwise() {this.puzzle.execU(false, 0); this.autoRefresh();}
    u2() {this.u(); this.u(); this.autoRefresh();} uw2() {this.uw(); this.uw(); this.autoRefresh();} uw32() {this.uw3(); this.uw3(); this.autoRefresh();}
    uw() {this.puzzle.execU(true, 0); this.puzzle.execU(true, 1); this.autoRefresh();} uwAnticlockwise() {this.puzzle.execU(false, 0); this.puzzle.execU(false, 1); this.autoRefresh();}
    uw3() {this.puzzle.execU(true, 0); this.puzzle.execU(true, 1); this.puzzle.execU(true, 2); this.autoRefresh();} uwAnticlockwise3() {this.puzzle.execU(false, 0); this.puzzle.execU(false, 1); this.puzzle.execU(false, 2); this.autoRefresh();}

    d() {this.puzzle.execD(true, 0); this.autoRefresh();} dAnticlockwise() {this.puzzle.execD(false, 0); this.autoRefresh();}
    d2() {this.d(); this.d(); this.autoRefresh();} dw2() {this.dw(); this.dw(); this.autoRefresh();} dw32() {this.dw3(); this.dw3(); this.autoRefresh();}
    dw() {this.puzzle.execD(true, 0); this.puzzle.execD(true, 1); this.autoRefresh();} dwAnticlockwise() {this.puzzle.execD(false, 0); this.puzzle.execD(false, 1); this.autoRefresh();}
    dw3() {this.puzzle.execD(true, 0); this.puzzle.execD(true, 1); this.puzzle.execD(true, 2); this.autoRefresh();} dwAnticlockwise3() {this.puzzle.execD(false, 0); this.puzzle.execD(false, 1); this.puzzle.execD(false, 2); this.autoRefresh();}

    f(){this.puzzle.execF(true, 0); this.autoRefresh();} fAnticlockwise(){this.puzzle.execF(false, 0); this.autoRefresh();}
    f2() {this.f(); this.f(); this.autoRefresh();} fw2() {this.fw(); this.fw(); this.autoRefresh();} fw32() {this.fw3(); this.fw3(); this.autoRefresh();}
    fw(){this.puzzle.execF(true, 0); this.puzzle.execF(true, 1); this.autoRefresh();} fwAnticlockwise(){this.puzzle.execF(false, 0); this.puzzle.execF(false, 1); this.autoRefresh();}
    fw3(){this.puzzle.execF(true, 0); this.puzzle.execF(true, 1); this.puzzle.execF(true, 2); this.autoRefresh();} fwAnticlockwise3(){this.puzzle.execF(false, 0); this.puzzle.execF(false, 1); this.puzzle.execF(false, 2); this.autoRefresh();}

    b(){this.puzzle.execB(true, 0); this.autoRefresh();} bAnticlockwise(){this.puzzle.execB(false, 0); this.autoRefresh();}
    b2() {this.b(); this.b(); this.autoRefresh();} bw2() {this.bw(); this.bw(); this.autoRefresh();} bw32() {this.bw3(); this.bw3(); this.autoRefresh();}
    bw(){this.puzzle.execB(true, 0); this.puzzle.execB(true, 1); this.autoRefresh();} bwAnticlockwise(){this.puzzle.execB(false, 0); this.puzzle.execB(false, 1); this.autoRefresh();}
    bw3(){this.puzzle.execB(true, 0); this.puzzle.execB(true, 1); this.puzzle.execB(true, 2); this.autoRefresh();} bwAnticlockwise3(){this.puzzle.execB(false, 0); this.puzzle.execB(false, 1); this.puzzle.execB(false, 2); this.autoRefresh();}

}
