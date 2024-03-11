// Placeholder methods to test interaction with the html page
async function getTokenName(){
    var n = document.getElementById("token-name");
    n.innerHTML = "UniPG Stablecoin";
}

async function getTokenSymbol(){
    var img = document.getElementById("token-symbol");
    img.src = "./public/img/Stablecoin.png";
    img.alt = "UniPG Stablecoin";
    img.width = 200;
    img.height = 200;
}


// Draw the charts

createCoinChart();

createTokenChart();