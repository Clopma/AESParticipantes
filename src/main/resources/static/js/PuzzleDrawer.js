class Scramble {
    yellow = '#dbb223';
    blue = '#1a5395';
    red = '#bb3035';
    white = '#ffffff';
    green = '#1f6d39';
    orange = '#dd601d';

    constructor(puzzle, div, autorefresh, colours) {
        if(colours){
         this.green = colours[0];
         this.white = colours[1];
         this.red = colours[2];
         this.blue = colours[3];
         this.yellow = colours[4];
         this.orange = colours[5];
        }

        this.facesData = [
            {name: 'f', x: 2, y: 2, initialColour: this.green},
            {name: 'l', x: 1, y: 2, initialColour: this.orange},
            {name: 'u', x: 2, y: 1, initialColour: this.white},
            {name: 'r', x: 3, y: 2, initialColour: this.red},
            {name: 'b', x: 4, y: 2, initialColour: this.blue},
            {name: 'd', x: 2, y: 3, initialColour: this.yellow}];

        this.autorefresh = autorefresh;
        this.container = div;
        this.cleanWrapper();
        this.state = {};
        this.state.faces = [];
        if (['2x2x2', '3x3x3', '4x4x4', '5x5x5', '6x6x6', '7x7x7'].includes(puzzle)) {
            this.size = parseInt(puzzle.substr(0, 2));
            this.createBaseTable();
        }
        this.draw();
    };

    cleanWrapper(){
        this.wrapper = document.createElement('div');
        this.wrapper.className = 'scrambleDrawWrapper';
    }

    draw(){
        this.cleanWrapper();
        for (var i = 0; i < this.facesData.length; i++) {
            var face = document.createElement('div');
            face.style.gridColumn = this.facesData[i].x;
            face.style.gridRow = this.facesData[i].y;
            face.className = 'face';
            face.style.gridTemplateColumns = this.size;
            face.style.gridTemplateRows = this.size;
            this.wrapper.appendChild(face);
            for (var xx = 0; xx < this.size; xx++) {
                for (var yy = 0; yy < this.size; yy++) {
                    var sticker = document.createElement('div');
                    sticker.className = "sticker";
                    sticker.style.gridRow = xx + 1;
                    sticker.style.gridColumn = yy + 1;
                    sticker.style.backgroundColor = this.state.faces.find(o => o.face === this.facesData[i].name).grid[xx][yy];
                    face.appendChild(sticker);
                }
            }
        }

        this.container.innerHTML = this.wrapper.outerHTML;
    }

    createBaseTable() {
        for (var i = 0; i < this.facesData.length; i++) {
            var faceStatus = {face: this.facesData[i].name, grid: [...Array(this.size)].map(e => Array(this.size))};
            this.state.faces.push(faceStatus);
            for (var xx = 0; xx < this.size; xx++) {
                for (var yy = 0; yy < this.size; yy++) {
                    faceStatus.grid[xx][yy] = this.facesData[i].initialColour;
                }
            }
        }

    }



    scramble(scramble){
       var autorefreshConfig = this.autorefresh;
       this.autorefresh = false;
       var movements = scramble.split(' ');
       for(var i = 0; i < movements.length; i++){
           var move = movements[i];
           var clockwise = !move.includes('\'');
           this.doMove(move, clockwise);
           if(move.includes('2')){this.doMove(move, clockwise)}
       }
       this.autorefresh = autorefreshConfig;
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

    r() {this.execR(true, 0);} rAnticlockwise() {this.execR(false,0);}
    r2() {this.r(); this.r();} rw2() {this.rw(); this.rw();} rw32() {this.rw3(); this.rw3();}
    rw() {this.execR(true, 0); this.execR(true, 1);} rwAnticlockwise() { this.execR(false, 0); this.execR(false, 1);}
    rw3() {this.execR(true, 0); this.execR(true, 1); this.execR(true, 2);} rwAnticlockwise3() { this.execR(false, 0); this.execR(false, 1); this.execR(false, 2);}

    l() { this.execL(true, 0);} lAnticlockwise() {this.execL(false, 0);}
    l2() {this.l(); this.l();} lw2() {this.lw(); this.lw();} lw32() {this.lw3(); this.lw3();}
    lw() { this.execL(true, 0); this.execL(true, 1);} lwAnticlockwise() {this.execL(false, 0); this.execL(false, 1);}
    lw3() { this.execL(true, 0); this.execL(true, 1); this.execL(true, 2);} lwAnticlockwise3() {this.execL(false, 0); this.execL(false, 1); this.execL(false, 2);}

    u() {this.execU(true, 0);} uAnticlockwise() {this.execU(false, 0);}
    u2() {this.u(); this.u();} uw2() {this.uw(); this.uw();} uw32() {this.uw3(); this.uw3();}
    uw() {this.execU(true, 0); this.execU(true, 1);} uwAnticlockwise() {this.execU(false, 0); this.execU(false, 1);}
    uw3() {this.execU(true, 0); this.execU(true, 1); this.execU(true, 2);} uwAnticlockwise3() {this.execU(false, 0); this.execU(false, 1); this.execU(false, 2);}

    d() {this.execD(true, 0);} dAnticlockwise() {this.execD(false, 0);}
    d2() {this.d(); this.d();} dw2() {this.dw(); this.dw();} dw32() {this.dw3(); this.dw3();}
    dw() {this.execD(true, 0); this.execD(true, 1);} dwAnticlockwise() {this.execD(false, 0); this.execD(false, 1);}
    dw3() {this.execD(true, 0); this.execD(true, 1); this.execD(true, 2);} dwAnticlockwise3() {this.execD(false, 0); this.execD(false, 1); this.execD(false, 2);}

    f(){this.execF(true, 0);} fAnticlockwise(){this.execF(false, 0);}
    f2() {this.f(); this.f();} fw2() {this.fw(); this.fw();} fw32() {this.fw3(); this.fw3();}
    fw(){this.execF(true, 0); this.execF(true, 1);} fwAnticlockwise(){this.execF(false, 0); this.execF(false, 1);}
    fw3(){this.execF(true, 0); this.execF(true, 1); this.execF(true, 2);} fwAnticlockwise3(){this.execF(false, 0); this.execF(false, 1); this.execF(false, 2);}

    b(){this.execB(true, 0);} bAnticlockwise(){this.execB(false, 0);}
    b2() {this.b(); this.b();} bw2() {this.bw(); this.bw();} bw32() {this.bw3(); this.bw3();}
    bw(){this.execB(true, 0); this.execB(true, 1);} bwAnticlockwise(){this.execB(false, 0); this.execB(false, 1);}
    bw3(){this.execB(true, 0); this.execB(true, 1); this.execB(true, 2);} bwAnticlockwise3(){this.execB(false, 0); this.execB(false, 1); this.execB(false, 2);}


    execF(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('f', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'u' : 'r', clockwise, clockwise ? this.size - 1 - layer : layer, clockwise ? 'r' : 'u', !clockwise, clockwise ? layer : this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'l', clockwise, clockwise ? layer : this.size - 1 - layer, clockwise ? 'l' : 'd', !clockwise, clockwise ? this.size - 1 - layer : layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'u', !clockwise, this.size - 1 - layer, clockwise ? 'u' : 'l', clockwise, this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'd', !clockwise, layer, clockwise ? 'd' : 'r', clockwise, layer, true, oldState);
        if (this.autorefresh) this.draw();

    }

    execB(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('b', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'u' : 'l', clockwise, layer, clockwise ? 'l' : 'u', !clockwise, layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'r', clockwise, this.size - 1 - layer, clockwise ? 'r' : 'd', !clockwise, this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'd', !clockwise, clockwise ? layer : this.size - 1 - layer, clockwise ? 'd' : 'l', clockwise, clockwise ? this.size - 1 - layer : layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'u', !clockwise, clockwise ? this.size - 1 - layer : layer, clockwise ? 'u' : 'r', clockwise, clockwise ? layer : this.size - 1 - layer, false, oldState);
        if (this.autorefresh) this.draw();

    }

    execU(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('u', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'l', true, layer, clockwise ? 'l' : 'f', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'r', true, layer, clockwise ? 'r' : 'b', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'b', true, layer, clockwise ? 'b' : 'l', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'f', true, layer, clockwise ? 'f' : 'r', true, layer, false, oldState);
        if (this.autorefresh) this.draw();

    }
    
    execD(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('d', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'r', true, this.size - 1 - layer, clockwise ? 'r' : 'f', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'l', true, this.size - 1 - layer, clockwise ? 'l' : 'b', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'f', true, this.size - 1 - layer, clockwise ? 'f' : 'l', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'b', true, this.size - 1 - layer, clockwise ? 'b' : 'r', true, this.size - 1 - layer, false, oldState);
        if (this.autorefresh) this.draw();

    }
    
    execL(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('l', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'd', false, layer, clockwise ? 'd' : 'f', false, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'u', false, clockwise ? this.size - 1 - layer : layer, clockwise ? 'u' : 'b', false, clockwise ? layer : this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'f', false, layer, clockwise ? 'f' : 'u', false, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'b', false, clockwise ? layer : this.size - 1 - layer, clockwise ? 'b' : 'd', false, clockwise ? this.size - 1 - layer : layer, true, oldState);
        if (this.autorefresh) this.draw();

    }
    
    execR(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('r', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'u', false, this.size - 1 - layer, clockwise ? 'u' : 'f', false, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'd', false, clockwise ? layer : this.size - 1 - layer, clockwise ? 'd' : 'b', false, clockwise ? this.size - 1 - layer : layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'b', false, clockwise ? this.size - 1 - layer : layer, clockwise ? 'b' : 'u', false, clockwise ? layer : this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'f', false, this.size - 1 - layer, clockwise ? 'f' : 'd', false, this.size - 1 - layer, false, oldState);
        if (this.autorefresh) this.draw();
    }

    

    rotateSurrounds(origin, isRowOrigin, numberOfRowColOrigin, destination, isRowDestination, numberOfRowColDestination, reverse, oldState) {

        for (var i = 0; i < this.size; i++) {
            this.state.faces.find(o => o.face === destination).grid[isRowDestination ? numberOfRowColDestination : i][isRowDestination ? i: numberOfRowColDestination] =
                oldState.faces.find(o => o.face === origin).grid[isRowOrigin ? numberOfRowColOrigin : (reverse ? this.size - 1 -i : i)][isRowOrigin ? (reverse ? this.size - 1 - i : i) : numberOfRowColOrigin];
        }

    }

    rotateSelf(face, oldState, clockwise) {

        for(var i = 0; i < this.size; i++){
            for(var j = 0; j < this.size; j++){
                this.state.faces.find(o => o.face === face).grid[clockwise ? j : this.size - 1 - j][clockwise ? this.size-1-i : i] = oldState.faces.find(o => o.face === face).grid[i][j];
            }
        }

    }
}