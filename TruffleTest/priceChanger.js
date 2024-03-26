
module.exports = async function(callback) {
    const PriceGenerator = artifacts.require("PriceGenerator");
    const pricegen = await PriceGenerator.deployed();
    const accounts = await web3.eth.getAccounts();
    const timeout = 10; // time in seconds

    function myFunction() {
        let price = BigInt((Math.random()*0.5 + 1)*1e18);
        console.log(price);
        pricegen.setPrice(price, {from: accounts[0]}).then(() => {
            console.log("Price set to " + price);
        });
        setTimeout(myFunction, timeout*1000);
    }

    myFunction();
}