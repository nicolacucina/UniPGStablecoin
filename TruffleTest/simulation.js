
module.exports = async function(callback) {
    const PriceGenerator = artifacts.require("PriceGenerator");
    let pricegen = await PriceGenerator.deployed();
    const UniPGStableCoin = artifacts.require("UniPGStableCoin");
    let stablecoin = await UniPGStableCoin.deployed();
    const accounts = await web3.eth.getAccounts();
    const timeout = 5; // time in seconds
    const amount = BigInt(20);
    var counter = 0;

    stablecoin.mint(accounts[1], amount, {from: accounts[0]}).then((data) => {
        console.log('Minted ' + amount + ' stablecoins to ' + accounts[1]);
        //console.log(data);
    });
    stablecoin.mint(accounts[2], amount, {from: accounts[0]}).then((data) => {
        console.log('Minted ' + amount + ' stablecoins to ' + accounts[2]);
        //console.log(data);
    });

    function myFunction() {
        if(counter >= 20){
            console.log("Stopping");
            callback();
        }else{
            console.log('Counter: ' + counter);
            let price = BigInt((Math.random()*0.6 + 0.7)*1e18); // random price between 0.7 and 1.3
            pricegen.setPrice(price, {from: accounts[0]}).then((data) => {
                console.log("Price set to " + price);
                // console.log(data);

                stablecoin.rebase({from: accounts[0]}).then((data) => {
                    console.log("Rebase done");
                    //console.log(data);
                    counter++;
                });
            });
            setTimeout(myFunction, timeout*1000);
        }
    } 
    myFunction();
}