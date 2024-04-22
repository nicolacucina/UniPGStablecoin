// async function createPriceGenerator() {
//     const PriceGenerator = artifacts.require("PriceGenerator");
//     module.exports = function (deployer) {
//         deployer.deploy(PriceGenerator);
//     };
//     createStableCoin(PriceGenerator.address);
// }

// function createStableCoin(priceGenAddress) {
//     const UniPGStablecoin = artifacts.require("UniPGStablecoin");  
//     module.exports = function (deployer) {   
//         deployer.deploy(UniPGStablecoin, "UniPGStablecoin", "UPG", 18, priceGenAddress); 
//     };
// }

// createPriceGenerator();

const PriceGenerator = artifacts.require("PriceGenerator");
module.exports = function (deployer) {
    deployer.deploy(PriceGenerator);
};

