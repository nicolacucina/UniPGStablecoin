
module.exports = async function(callback) {
    const PriceGenerator = artifacts.require("PriceGenerator");
    let pricegen = await PriceGenerator.deployed();
    const accounts = await web3.eth.getAccounts();
    const timeout = 2; // time in seconds
    var counter = 0;

    function myFunction() {
        if(counter >= 20){
            console.log("Price changing stopped");
            callback();
        }else{
            console.log('Counter: ' + counter);
            let price = BigInt((Math.random()*0.6 + 0.7)*1e18); // random price between 0.7 and 1.3
            pricegen.setPrice(price, {from: accounts[0]}).then(() => {
                console.log("Price set to " + price);
                counter++;
            });
            setTimeout(myFunction, timeout*1000);
        }
    };
    myFunction();
}