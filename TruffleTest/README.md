# Simulating a StableCoin using the Truffle Suite

## Setup

As shown in https://archive.trufflesuite.com/blog/build-on-web3-with-truffle-vs-code-extension/ and https://archive.trufflesuite.com/docs/vscode-ext/how-to/install-dependencies/, the requirements are:

- Node.js --version^14.0.0
- Git --version^2.10.0
- NPM --verson^7.18.1
- Truffle Suite^5.0.0
- Ganache --version^6.0.0

These can be installed in VS Code by using the UI.

Inside the Truffle tab in VS Code, a local blockchain can be setup in the Networks section

    Create a new network
    >Ganache Service
    >Local
    >Port number => 8545
    >Name => development

This particular setup is reserved inside Truffle, as explained in the _truffle-config.js_ file:

    ...
    networks: {
        // Useful for testing. The `development` name is special - truffle uses it by default
        // if it's defined here and no other network is specified at the command line.
        // You should run a client (like ganache, geth, or parity) in a separate terminal
        // tab if you use this network and you must also set the `host`, `port` and `network_id`
        // options below to some value.
        //
        development: {
         host: "127.0.0.1",     // Localhost (default: none)
         port: 8545,            // Standard Ethereum port (default: none)
         network_id: "*",       // Any network (default: none)
    },
    ...

This file is created alongside the entire directory structure by using the command:

    truffle init
    
The contracts have to be saved inside the _contracts_ folder and will be `compiled` inside the _build_ folder
The _migrations_ folder is used to `deploy` the contracts on the network, they all have the same basic structure

    // List all the dependencies of the contract
    const PriceGenerator = artifacts.require("PriceGenerator");
    const UniPGStablecoin = artifacts.require("UniPGStablecoin");
    
    // This deployes the contract by invoking its contructor, the deploy transaction comes by default from the first account in the network. 
    // The deploy function uses as paramenters the Contract name defined above, then the following parameters are passed to the constructor
    module.exports = function (deployer) {   
        deployer.deploy(UniPGStablecoin, "UniPGStablecoin", "UPG", 18, PriceGenerator.address);
    };

The Ganache Command Line Interface has various flags that can be used, as shown in https://archive.trufflesuite.com/docs/ganache/reference/cli-options/:

    -a <int>, number of accounts generated at startup, default 10
    -s 'String', seed to randomly generate mnemonics
    -m 'String', uses the specified mnemonics to generate addresses
    --wallet.accountKeysPath=<STRING>, filepath to save both accounts and private keys

Now that everything is set up, here are some example intructions:

    ganache-cli
    truffle migrate
    truffle console
    let accounts = await web3.eth.getAccounts(); 
    let pricegen = await PriceGenerator.deployed();
    let stablecoin = await UniPGStablecoin.deployed();
    accounts[0]
    pricegen.minter()
    stablecoin.minter()
    let currentprice = await pricegen.price();
    let myfunction = (numbers)=>{var result = '';for(var i = numbers.length -1; i >= 0; i--){var temp = Number(numbers[i]).toString(2);while(temp.length < 26){temp = '0' + temp;}result += temp;}return parseInt(result, 2);};
    let convertedPrice = myfunction(currentprice.words.slice(0,-1));
    stablecoin.initialMinting()
    stablecoin.mint(accounts[1], 20, {from : accounts[0]});
    stablecoin.mint(accounts[2], 20, {from : accounts[0]});
    stablecoin.mint(accounts[3], 20, {from : accounts[0]});
    stablecoin.balanceOf(accounts[1])
    stablecoin.balanceOf(accounts[2])
    stablecoin.balanceOf(accounts[3])
    web3.eth.getBalance(accounts[0])
    web3.eth.getBalance(accounts[1])
    stablecoin.blockMinting()

    // pass either strings or BigInts to avoid overflow
    newprice = 9.5e17.toString() 
    // newprice = BigInt(9.5e17)   

    pricegen.setPrice(newprice, {from : accounts[0]}) 
    stablecoin.rebase({from: accounts[0]})

These instructions are implemented inside the html page so that they can be used by a UI instead of the terminal

To simulate the changes in the price of the stablecoin, we can run the following function:
    
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

This recursively calls the setPrice function of the PriceGenerator Contract every 10 seconds until the script is stopped. To launch the script inside the truffle enviroment it must be contained inside a callback function exported via module (this is what truffle expects to find), therefore the script has been saved inside the file _priceChanger.js_, which can be launched using:

    truffle exec priceChanger.js