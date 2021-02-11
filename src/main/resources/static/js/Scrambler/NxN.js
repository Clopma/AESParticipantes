class NxN {
    downColour = '#dbb223';
    backColour = '#1a5395';
    leftColour = '#bb3035';
    upColour = '#ffffff';
    frontColour = '#1f6d39';
    rightColour = '#dd601d';

    constructor(colours, size) {
        this.size = size;
        if(colours){
            this.frontColour = colours[0];
            this.upColour = colours[1];
            this.leftColour = colours[2];
            this.backColour = colours[3];
            this.downColour = colours[4];
            this.rightColour = colours[5];
        }

        this.facesData = [
            {name: 'f', x: 2, y: 2, initialColour: this.frontColour},
            {name: 'l', x: 1, y: 2, initialColour: this.leftColour},
            {name: 'u', x: 2, y: 1, initialColour: this.upColour},
            {name: 'r', x: 3, y: 2, initialColour: this.rightColour},
            {name: 'b', x: 4, y: 2, initialColour: this.backColour},
            {name: 'd', x: 2, y: 3, initialColour: this.downColour}];
        this.state = {};
        this.state.faces = [];
        this.createBaseTable();
    };

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

    setWrapperHTML(wrapper){

        for (var i = 0; i < this.facesData.length; i++) {
            var face = document.createElement('div');
            face.style.gridColumn = this.facesData[i].x;
            face.style.gridRow = this.facesData[i].y;
            face.className = 'face';
            face.style.gridTemplateColumns = this.size;
            face.style.gridTemplateRows = this.size;
            wrapper.appendChild(face);
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
    }

    execF(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('f', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'u' : 'r', clockwise, clockwise ? this.size - 1 - layer : layer, clockwise ? 'r' : 'u', !clockwise, clockwise ? layer : this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'l', clockwise, clockwise ? layer : this.size - 1 - layer, clockwise ? 'l' : 'd', !clockwise, clockwise ? this.size - 1 - layer : layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'u', !clockwise, this.size - 1 - layer, clockwise ? 'u' : 'l', clockwise, this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'd', !clockwise, layer, clockwise ? 'd' : 'r', clockwise, layer, true, oldState);
    }

    execB(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('b', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'u' : 'l', clockwise, layer, clockwise ? 'l' : 'u', !clockwise, layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'r', clockwise, this.size - 1 - layer, clockwise ? 'r' : 'd', !clockwise, this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'd', !clockwise, clockwise ? layer : this.size - 1 - layer, clockwise ? 'd' : 'l', clockwise, clockwise ? this.size - 1 - layer : layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'u', !clockwise, clockwise ? this.size - 1 - layer : layer, clockwise ? 'u' : 'r', clockwise, clockwise ? layer : this.size - 1 - layer, false, oldState);

    }

    execU(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('u', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'l', true, layer, clockwise ? 'l' : 'f', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'r', true, layer, clockwise ? 'r' : 'b', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'b', true, layer, clockwise ? 'b' : 'l', true, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'f', true, layer, clockwise ? 'f' : 'r', true, layer, false, oldState);

    }
    
    execD(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('d', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'r', true, this.size - 1 - layer, clockwise ? 'r' : 'f', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'l', true, this.size - 1 - layer, clockwise ? 'l' : 'b', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'l' : 'f', true, this.size - 1 - layer, clockwise ? 'f' : 'l', true, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'r' : 'b', true, this.size - 1 - layer, clockwise ? 'b' : 'r', true, this.size - 1 - layer, false, oldState);

    }
    
    execL(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('l', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'd', false, layer, clockwise ? 'd' : 'f', false, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'u', false, clockwise ? this.size - 1 - layer : layer, clockwise ? 'u' : 'b', false, clockwise ? layer : this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'f', false, layer, clockwise ? 'f' : 'u', false, layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'b', false, clockwise ? layer : this.size - 1 - layer, clockwise ? 'b' : 'd', false, clockwise ? this.size - 1 - layer : layer, true, oldState);
    }
    
    execR(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));
        if (layer === 0) {this.rotateSelf('r', oldState, clockwise)}
        this.rotateSurrounds(clockwise ? 'f' : 'u', false, this.size - 1 - layer, clockwise ? 'u' : 'f', false, this.size - 1 - layer, false, oldState);
        this.rotateSurrounds(clockwise ? 'b' : 'd', false, clockwise ? layer : this.size - 1 - layer, clockwise ? 'd' : 'b', false, clockwise ? this.size - 1 - layer : layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'u' : 'b', false, clockwise ? this.size - 1 - layer : layer, clockwise ? 'b' : 'u', false, clockwise ? layer : this.size - 1 - layer, true, oldState);
        this.rotateSurrounds(clockwise ? 'd' : 'f', false, this.size - 1 - layer, clockwise ? 'f' : 'd', false, this.size - 1 - layer, false, oldState);
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