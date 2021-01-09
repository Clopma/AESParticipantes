var yellow = '#dbb223';
var blue = '#1a5395';
var red = '#bb3035';
var white = '#ffffff';
var green = '#1f6d39';
var orange = '#dd601d';

var facesData = [
    {name: 'f', x: 2, y: 2, initialColour: green},
    {name: 'l', x: 1, y: 2, initialColour: orange},
    {name: 'u', x: 2, y: 1, initialColour: white},
    {name: 'r', x: 3, y: 2, initialColour: red},
    {name: 'b', x: 4, y: 2, initialColour: blue},
    {name: 'd', x: 2, y: 3, initialColour: yellow}];

class Scramble {

    constructor(puzzle, div, autorefresh) {
        this.autorefresh = autorefresh;
        this.container = div;
        this.wrapper = document.createElement('div');
        this.wrapper.className = 'scrambleDrawWrapper';
        this.state = {};
        this.state.faces = [];
        if (['2x2x2', '3x3x3', '4x4x4', '5x5x5', '6x6x6', '7x7x7'].includes(puzzle)) {
            this.size = parseInt(puzzle.substr(0, 2));
            this.createBaseTable();
        }
        this.draw();
    };

    draw(){
        for (var i = 0; i < facesData.length; i++) {
            this.colourFace(facesData[i]);
        }

        this.container.innerHTML = this.wrapper.outerHTML;
    }

    createBaseTable() {
        for (var i = 0; i < facesData.length; i++) {
            var faceStatus = {face: facesData[i].name, grid: [...Array(this.size)].map(e => Array(this.size))};
            this.state.faces.push(faceStatus);
            for (var xx = 0; xx < this.size; xx++) {
                for (var yy = 0; yy < this.size; yy++) {
                    faceStatus.grid[xx][yy] = facesData[i].initialColour;
                }
            }
        }

    }

    colourFace(faceData) {
        var face = document.createElement('div');
        face.style.gridColumn = faceData.x;
        face.style.gridRow = faceData.y;
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
                sticker.style.backgroundColor = this.state.faces.find(o => o.face === faceData.name).grid[xx][yy];
                face.appendChild(sticker);
            }
        }
    }

    scramble(scramble){

       var movements = scramble.split(' ');
       for(var i = 0; i < movements.length; i++){
           var move = movements[i];
           var clockwise = !move.includes('\'');
           this.doMove(move, clockwise);
           if(move.includes('2')){this.doMove(move, clockwise)}
       }
    }

    doMove(move, clockwise){
        if(move.includes('B')){this.execB(clockwise)}
        if(move.includes('F')){this.execF(clockwise)}
        if(move.includes('L')){this.execL(clockwise)}
        if(move.includes('R')){this.execR(clockwise)}
        if(move.includes('D')){this.execD(clockwise)}
        if(move.includes('U')){this.execU(clockwise)}
    }

    r() { this.execR(true);}
    rAnticlockwise() { this.execR(false)}

    l() { this.execL(true);}
    lAnticlockwise() {this.execL(false);}

    u() {this.execU(true);}
    uAnticlockwise() {this.execU(false);}

    d() {this.execD(true);}
    dAnticlockwise() {this.execD(false);}

    f(){this.execF(true);}
    fAnticlockwise(){this.execF(false);}

    b(){this.execB(true);}
    bAnticlockwise(){this.execB(false);}

    execR(clockwise){
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('r', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'f' : 'u', false, this.size -1, clockwise ? 'u' : 'f', false, this.size -1, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'f', false, this.size -1, clockwise ? 'f' : 'd', false, this.size -1, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'd', false, clockwise ? 0 : this.size -1, clockwise ? 'd' : 'b', false, clockwise ? this.size -1 : 0, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'b', false, clockwise ? this.size -1 : 0, clockwise ? 'b' : 'u', false, clockwise ? 0 : this.size -1, true, oldState);
        if (this.autorefresh) this.draw();
    }

    execL(clockwise){
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('l', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'u' : 'f', false, 0, clockwise ? 'f' : 'u', false, 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'f' : 'd', false, 0, clockwise ? 'd' : 'f', false, 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'b', false, clockwise ? 0 : this.size -1, clockwise ? 'b' : 'd', false, clockwise ? this.size -1 : 0, true, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'u', false, clockwise ? this.size -1 : 0, clockwise ? 'u' : 'b', false, clockwise ? 0 : this.size -1, true, oldState);
        if (this.autorefresh) this.draw();

    }

    execU(clockwise) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('u', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'f' : 'l', true, 0, clockwise ? 'l' : 'f', true, 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'f', true, 0, clockwise ? 'f' : 'r', true, 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'r', true, 0, clockwise ? 'r' : 'b', true, 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'b', true, 0, clockwise ? 'b' : 'l', true, 0, false, oldState);
        if (this.autorefresh) this.draw();

    }

    execD(clockwise) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('d', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'l' : 'f', true, this.size -1, clockwise ? 'f' : 'l', true, this.size -1, false, oldState);
        this.rotateSurrounds(clockwise ? 'f' : 'r', true, this.size -1, clockwise ? 'r' : 'f', true, this.size -1, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'b', true, this.size -1, clockwise ? 'b' : 'r', true, this.size -1, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'l', true, this.size -1, clockwise ? 'l' : 'b', true, this.size -1, false, oldState);
        if (this.autorefresh) this.draw();

    }

    execF(clockwise) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('f', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'l' : 'u', !clockwise, this.size -1, clockwise ? 'u' : 'l', clockwise, this.size -1, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'r', clockwise, clockwise ? this.size - 1 : 0, clockwise ? 'r' : 'u', !clockwise, clockwise ? 0 : this.size - 1, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'd', !clockwise, 0, clockwise ? 'd' : 'r', clockwise, 0, true, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'l', clockwise, clockwise ? 0 : this.size - 1, clockwise ? 'l' : 'd', !clockwise, clockwise ? this.size - 1 : 0, false, oldState);
        if (this.autorefresh) this.draw();

    }

    execB(clockwise) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        this.rotateSelf('b', oldState, clockwise);
        this.rotateSurrounds(clockwise ? 'u' : 'l', clockwise, 0, clockwise ? 'l' : 'u', !clockwise, 0, true, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'd', !clockwise, clockwise ? 0 : this.size - 1, clockwise ? 'd' : 'l', clockwise, clockwise ? this.size - 1 : 0, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'r', clockwise, this.size - 1, clockwise ? 'r' : 'd', !clockwise, this.size - 1, true, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'u', !clockwise, clockwise ? this.size - 1 : 0, clockwise ? 'u' : 'r', clockwise, clockwise ? 0 : this.size - 1, false, oldState);
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