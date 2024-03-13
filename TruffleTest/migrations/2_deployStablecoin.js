const PriceGenerator = artifacts.require("PriceGenerator");
const UniPGStablecoin = artifacts.require("UniPGStablecoin");  
module.exports = function (deployer) {   
    deployer.deploy(UniPGStablecoin, "UniPGStablecoin", "UPG", 18, PriceGenerator.address); 
};