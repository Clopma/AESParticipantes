function draw(puzzle, scramble){

    if(['2x2x2', '3x3x3', '4x4x4', '5x5x5', '6x6x6', '7x7x7'].includes(puzzle)){
       return drawNxN(puzzle.substr(0, 1), scramble);
    }


    return 'XXX';

}



function drawNxN(size, scramble){

    var wrapper = createBaseTable(size);


    return wrapper;
}

function createBaseTable(size) {

    var wrapper = document.createElement('div');
    wrapper.className = 'scrambleDrawWrapper';

    addFace('lface', wrapper, 1, 2, size);
    addFace('uface', wrapper, 2, 1, size);
    addFace('fface', wrapper, 2, 2, size);
    addFace('dface', wrapper, 2, 3, size);
    addFace('rface', wrapper, 3, 2, size);
    addFace('bface', wrapper, 4, 2, size);

    return wrapper;

}

function addFace(id, wrapper, x, y, size) {
    var face = document.createElement('div');
    face.id = id;
    face.style.gridColumn = x;
    face.style.gridRow = y;
    face.className = 'face';
    face.style.gridTemplateColumns = size;
    face.style.gridTemplateRows = size;
    wrapper.appendChild(face);

    for(xx = 0; xx < size; xx++){
        for(yy = 0; yy < size; yy++){
            var sticker = document.createElement('div');
            sticker.className = "sticker";
            sticker.style.gridColumn = xx + 1;
            sticker.style.gridRow = yy + 1;
            face.appendChild(sticker);
        }
    }

}
