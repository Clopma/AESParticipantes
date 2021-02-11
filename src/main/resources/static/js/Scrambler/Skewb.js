class Skewb {
    downColour = '#dbb223';
    backColour = '#1a5395';
    leftColour = '#bb3035';
    upColour = '#ffffff';
    frontColour = '#1f6d39';
    rightColour = '#dd601d';

    constructor(colours) {
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
            var faceStatus = {face: this.facesData[i].name, grid: [...Array(2)].map(e => Array(2))};
            this.state.faces.push(faceStatus);
            faceStatus.center = this.facesData[i].initialColour;
            for (var xx = 0; xx < 2; xx++) {
                for (var yy = 0; yy < 2; yy++) {
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
            face.style.gridTemplateColumns = 2;
            face.style.gridTemplateRows = 2;
            wrapper.appendChild(face);
            var canvas = document.createElement('canvas');
            canvas.width = 700;
            canvas.style.width = "100%";
            canvas.height = 700;
            var ctx = canvas.getContext('2d');
            ctx.strokeStyle = "black";
            var lineWidth = 25;
            var size = canvas.width / 2.8;

            if(['f', 'd', 'l'].includes(this.facesData[i].name)){
                canvas.style.transform = "skewY(30deg)";
            } else if (['r', 'b'].includes(this.facesData[i].name)) {
                canvas.style.transform = "skewY(330deg)";
            }

            if(['l', 'b'].includes(this.facesData[i].name)){
                face.style.gridRowStart = 1;
                face.style.gridRowEnd = "span 2";
                face.style.marginTop = '30%';
            }

            if(['u'].includes(this.facesData[i].name)){
                face.style.width = '50%';
                face.style.marginLeft = '25%';
                face.style.marginBottom= '-11%';
                face.style.gridColumnStart = 2;
                face.style.gridColumnEnd = "span 2";
                canvas.style.transform = "scale(1.4142, 0.8164) rotate(45deg)";
            }

            ctx.lineWidth = lineWidth;
            ctx.fillStyle = this.state.faces.find(o => o.face === this.facesData[i].name).grid[0][0];
            roundTriangle(ctx, lineWidth/2, lineWidth/2, size, size, 20, true, true, 2);
            ctx.fill();
            ctx.fillStyle = this.state.faces.find(o => o.face === this.facesData[i].name).grid[0][1];
            roundTriangle(ctx, (canvas.width - lineWidth/2) - size, lineWidth/2, size, size, 20, true, true, 3);
            ctx.fill();
            ctx.fillStyle = this.state.faces.find(o => o.face === this.facesData[i].name).grid[1][0];
            roundTriangle(ctx, lineWidth/2, (canvas.height - lineWidth/2) - size, size, size, 20, true, true, 1);
            ctx.fill();
            ctx.fillStyle = this.state.faces.find(o => o.face === this.facesData[i].name).grid[1][1];
            roundTriangle(ctx, (canvas.width - lineWidth/2) - size, (canvas.height - lineWidth/2) - size, size, size, 20, true, true, 4);
            ctx.fill();

            var centerSize = canvas.width * 1.4142 / 2 - 15;
            ctx.translate(canvas.width/2, canvas.height/2);
            ctx.rotate(45*Math.PI/180);
            ctx.fillStyle = this.state.faces.find(o => o.face === this.facesData[i].name).center;
            roundRect(ctx,  -centerSize/2, -centerSize/2, centerSize, centerSize, 20, true, true);
            ctx.fill();


            face.appendChild(canvas);
            wrapper.style.marginBottom = '5%';
            wrapper.style.marginTop = '5%';
        }


    }

    execU(clockwise, layer) {

            var oldState = JSON.parse(JSON.stringify(this.state));

            this.state.faces.find(o => o.face === 'l').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'b')).grid[0][clockwise ? 0 : 1];
            this.state.faces.find(o => o.face === 'l').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'b')).grid[clockwise ? 0 : 1][1];
            this.state.faces.find(o => o.face === 'l').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'b')).grid[clockwise ? 1 : 0][0];
            this.state.faces.find(o => o.face === 'l').center = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'b')).center;

            this.state.faces.find(o => o.face === 'u').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'l')).grid[0][clockwise ? 1 : 0];
            this.state.faces.find(o => o.face === 'u').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'l')).grid[clockwise ? 1 : 0][1];
            this.state.faces.find(o => o.face === 'u').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'l')).grid[clockwise ? 0 : 1][0];
            this.state.faces.find(o => o.face === 'u').center = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'l')).center;

            this.state.faces.find(o => o.face === 'b').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'u')).grid[1][0];
            this.state.faces.find(o => o.face === 'b').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'u')).grid[0][0];
            this.state.faces.find(o => o.face === 'b').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'u')).grid[0][1];
            this.state.faces.find(o => o.face === 'b').center = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'u')).center;

            this.state.faces.find(o => o.face === 'f').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'd')).grid[clockwise ? 0 : 1][clockwise ? 1 : 0];
            this.state.faces.find(o => o.face === 'r').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'f')).grid[clockwise ? 1 : 0][0];
            this.state.faces.find(o => o.face === 'd').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'r')).grid[0][clockwise ? 0 : 1];

    }

    execB(clockwise, layer) {
        var oldState = JSON.parse(JSON.stringify(this.state));

        this.state.faces.find(o => o.face === 'l').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'd')).grid[clockwise ? 1 : 0][0];
        this.state.faces.find(o => o.face === 'l').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'd')).grid[1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'l').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'd')).grid[clockwise ? 0 : 1][1];
        this.state.faces.find(o => o.face === 'l').center = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'd')).center;

        this.state.faces.find(o => o.face === 'd').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'b')).grid[clockwise ? 0 : 1][0];
        this.state.faces.find(o => o.face === 'd').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'b')).grid[1][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'd').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'b')).grid[clockwise ? 1 : 0][1];
        this.state.faces.find(o => o.face === 'd').center = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'b')).center;

        this.state.faces.find(o => o.face === 'b').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'l')).grid[1][1];
        this.state.faces.find(o => o.face === 'b').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'l')).grid[0][0];
        this.state.faces.find(o => o.face === 'b').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'l')).grid[1][0];
        this.state.faces.find(o => o.face === 'b').center = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'l')).center;

        this.state.faces.find(o => o.face === 'u').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'f')).grid[1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'r').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'u')).grid[clockwise ? 1 : 0][0];
        this.state.faces.find(o => o.face === 'f').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'r')).grid[clockwise ? 0 : 1][clockwise ? 0 : 1];
    }

    execL(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));

        this.state.faces.find(o => o.face === 'f').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'd')).grid[clockwise ? 1 : 0][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'f').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'd')).grid[clockwise ? 1 : 0][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'f').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'd')).grid[clockwise ? 0 : 1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'f').center = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'd')).center;

        this.state.faces.find(o => o.face === 'd').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'l')).grid[1][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'd').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'l')).grid[clockwise ? 0 : 1][0];
        this.state.faces.find(o => o.face === 'd').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'l')).grid[clockwise ? 1 : 0][1];
        this.state.faces.find(o => o.face === 'd').center = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'l')).center;

        this.state.faces.find(o => o.face === 'l').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'f')).grid[1][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'l').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'f')).grid[0][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'l').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'f')).grid[clockwise ? 0 : 1][0];
        this.state.faces.find(o => o.face === 'l').center = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'f')).center;

        this.state.faces.find(o => o.face === 'u').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'r')).grid[1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'r').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'b')).grid[1][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'b').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'u')).grid[1][0];
    }
    
    execR(clockwise, layer){
        var oldState = JSON.parse(JSON.stringify(this.state));

        this.state.faces.find(o => o.face === 'r').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'b')).grid[clockwise ? 0 : 1][1];
        this.state.faces.find(o => o.face === 'r').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'b')).grid[clockwise ? 1 : 0][0];
        this.state.faces.find(o => o.face === 'r').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'b')).grid[1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'r').center = oldState.faces.find(o => o.face === (clockwise ? 'd' : 'b')).center;

        this.state.faces.find(o => o.face === 'd').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'r')).grid[clockwise ? 1 : 0][1];
        this.state.faces.find(o => o.face === 'd').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'r')).grid[clockwise ? 0 : 1][0];
        this.state.faces.find(o => o.face === 'd').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'r')).grid[1][clockwise? 0 : 1];
        this.state.faces.find(o => o.face === 'd').center = oldState.faces.find(o => o.face === (clockwise ? 'b' : 'r')).center;

        this.state.faces.find(o => o.face === 'b').grid[0][0] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'd')).grid[1][0];
        this.state.faces.find(o => o.face === 'b').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'd')).grid[1][1];
        this.state.faces.find(o => o.face === 'b').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'd')).grid[0][1];
        this.state.faces.find(o => o.face === 'b').center = oldState.faces.find(o => o.face === (clockwise ? 'r' : 'd')).center;

        this.state.faces.find(o => o.face === 'u').grid[0][1] = oldState.faces.find(o => o.face === (clockwise ? 'f' : 'l')).grid[1][clockwise ? 1 : 0];
        this.state.faces.find(o => o.face === 'f').grid[1][1] = oldState.faces.find(o => o.face === (clockwise ? 'l' : 'u')).grid[clockwise ? 1 : 0][clockwise ? 0 : 1];
        this.state.faces.find(o => o.face === 'l').grid[1][0] = oldState.faces.find(o => o.face === (clockwise ? 'u' : 'f')).grid[clockwise ? 0 : 1][1];
    }

}