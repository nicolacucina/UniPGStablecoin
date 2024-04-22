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

## Execution

Now that everything is set up, here are some example intructions:

    ganache-cli
    truffle migrate
    truffle console
        let accounts = await web3.eth.getAccounts();
        /*
        [
            '0x4636b54DC4f0F976744a2c57ED84703E9F897cAe',
            '0xD6c3CeceA0Bc404313A753D167560817AE0ff7fB',
            '0xf7D9764E87299669aaB9000c9F8A113d60c1d5B4',
            ...
        ]
        */
        let pricegen = await PriceGenerator.deployed();
        let stablecoin = await UniPGStablecoin.deployed();
        /*
        TruffleContract {
            constructor: [Function: TruffleContract] {
                ...
            }
            methods: {
                'minter()': [Function (anonymous)] {
                    ...
                },
            }
        */

        // Show which addresses are configured
        accounts[0]
        pricegen.minter()
        stablecoin.minter()

        // Show price 
        let currentprice = await pricegen.price();
        /*
        BN {
            negative: 0,
            words: [ 56885248, 2993385, 222, <1 empty item> ],
            length: 3,
            red: null
        }
        */
        let currentprice = pricegen.price().then((data)=>{return BigInt(data)});
        /*
        1000000000000000000n        
        */

        // Initial minting
        stablecoin.initialMinting()
        let amount = BigInt(1e10).toString();
        stablecoin.mint(accounts[1], amount, {from : accounts[0]});
        stablecoin.mint(accounts[2], amount, {from : accounts[0]});
        stablecoin.mint(accounts[3], amount, {from : accounts[0]});
        /*
        {
        tx: '0x52bc...',
        receipt: {
            ...
        },
        logs: [
            {
            address: '0xB48aa090617b977E793F05C5815D76EE31D26460',
            blockHash: '0xb8d8d45554c982a42b9936523790f5c734f510c3be13c2ab6db91225c442c832',
            blockNumber: 3,
            logIndex: 0,
            removed: false,
            transactionHash: '0x52bc64665b78e6509b49e347cf58cdb58e2b4e7ecad5e94e34b9a74382b9cf9d',
            transactionIndex: 0,
            id: 'log_09655098',
            event: 'Transfer',
            args: [Result]
            }
        ]
        }*/
        stablecoin.balanceOf(accounts[1]).then((data)=>{return BigInt(data)})
        /*
        10000000000n
        */
        stablecoin.balanceOf(accounts[2])
        stablecoin.balanceOf(accounts[3])
        /*
        BN {
            negative: 0,
            words: [ 779264, 149, <1 empty item> ],
            length: 2,
            red: null
       }
        */
        stablecoin.blockMinting()

        // Check the cost in Ether for the transactions
        web3.eth.getBalance(accounts[0])
        /*
        999993305178507839050
        */
        web3.eth.getBalance(accounts[1])
        /*
        1000000000000000000000
        */

        // Rebase
        newprice = 9.5e17.toString() 
        // newprice = BigInt(9.5e17)   

        pricegen.setPrice(newprice, {from : accounts[0]}) 
        stablecoin.rebase({from: accounts[0]})

These instructions are implemented inside the html page so that they can be used by a UI instead of the terminal

To simulate the changes in the price of the stablecoin, we can run the following function:
    
    const PriceGenerator = artifacts.require("PriceGenerator");
    let pricegen = await PriceGenerator.deployed();
    const UniPGStableCoin = artifacts.require("UniPGStableCoin");
    let stablecoin = await UniPGStableCoin.deployed();
    const accounts = await web3.eth.getAccounts();
    
    const timeout = 4; // time in seconds
    const amount = BigInt(1e10);
    var counter = 0;

    function myFunction() {
        if(counter > 25){
            callback();
        }else{
            let price = BigInt((Math.random()*0.6 + 0.7)*1e18); // random price between 0.7 and 1.3
            pricegen.setPrice(price, {from: accounts[0]}).then((data) => {
                stablecoin.rebase({from: accounts[0]}).then((data) => {
                    counter++;
                    setTimeout(myFunction, timeout*1000);
                });
            });
        }
    } 

    stablecoin.mint(accounts[1], amount, {from: accounts[0]}).then((data) => {
        stablecoin.mint(accounts[2], amount, {from: accounts[0]}).then((data) => {
            myFunction();
        });
    });

This script mints an _amount_ of tokens to the wallets _accounts[1]_ and _accounts[2]_ then calls the recursive function _myFunction()_ that changes the price of the stablecoin and invokes the rebase process. The _then()_ construct is used to ensure that the asynchronous model used by node.js doesnt interfere with the ordeer of execution.

To launch the script inside the truffle enviroment it must be contained inside a callback function exported via module (this is what truffle expects to find), therefore the script has been saved inside the file _simulation.js_, which can be launched using:

    truffle exec simulation.js

## User Interaction

The users will not use the Command Line to interact with the blockchain but instead a html page.

The page is served locally using node.js using a simple script 

    var express = require('express');
    var app = express();
    var port = 5500;

    app.use(express.static(__dirname + '/'));

    app.listen(port, function () {
        console.log('Server is running on port ' + port);
    })

    app.get('/', (req, res) =>{
        res.sendFile(__dirname + '/index.html')
    })

Using this solution forces the js code to be embedded directly inside the html page.
The code is organized in different functions 

### Setup functions

These functions create the various variables used to show data inside the page

#### CreateContractParams()

This is the preliminary function that creates the connection with the blockchain and pulls the relevant information

    var web3;
    var PriceGeneratorABI;
    var UniPGStablecoinABI;
    var PriceGeneratorAddress;
    var UniPGStablecoinAddress;
    var PriceGeneratorMinter;
    var UniPGStablecoinMinter;
    var wallets;
    var PriceGenerator;
    var UniPGStablecoin;
    var coinPriceData;
    var tokenSupplyData;

    async function createContractParams(){
        const testnet = 'http://localhost:8545';
        web3 = new Web3(new Web3.providers.HttpProvider(testnet));

        // Test the connection

        web3.eth.net.getId().then((data)=>{
          console.log('Net Id:' + data); 
        });
        web3.eth.getBlock('latest').then((data)=>{
          console.log('Lastest Block: ');
          console.log(data); 
        });
        web3.eth.getBlockNumber().then((data)=>{
          console.log('Block Number:' + data); 
        });

        // Get ABI from the Truffle build

        d3.json('./TruffleTest/build/contracts/PriceGenerator.json').then((data) => {
            PriceGeneratorABI = data.abi;
          });
          
        d3.json('./TruffleTest/build/contracts/UniPGStablecoin.json').then((data) => {
            UniPGStablecoinABI = data.abi;
          });

        // Get contract address from Ganache

        let FirstBlockHash = await web3.eth.getBlock('1');
        let PriceGeneratorDeployment = await web3.eth.getTransactionReceipt(FirstBlockHash.transactions[0]);
        PriceGeneratorAddress = PriceGeneratorDeployment.contractAddress
        PriceGeneratorMinter = PriceGeneratorDeployment.from;

        let SecondBlockHash = await web3.eth.getBlock('2');
        let UniPGStablecoinDeployment = await web3.eth.getTransactionReceipt(SecondBlockHash.transactions[0]);
        UniPGStablecoinAddress = UniPGStablecoinDeployment.contractAddress
        UniPGStablecoinMinter = UniPGStablecoinDeployment.from;

        // Get the wallets
        // This can be done using web3, but since Ganache is used, the wallets are stored inside /public/data/keys.json
        
        wallets = await d3.json('./public/data/keys.json').then((data)=>{
            return data;
          });

        // Display information on the page

        displayBlocks();

        displayTransactions();
          
        var addresshtml = document.getElementById("contract-address");
        addresshtml.innerHTML = "<b>Price Generator contract address: </b>" + PriceGeneratorAddress + "<br><b>UniPG Stablecoin contract address: </b>" + UniPGStablecoinAddress;  
        
        var minterhtml = document.getElementById("contract-owner");
        minterhtml.innerHTML = "<b>Price Generator contract owner: </b>" + PriceGeneratorMinter + "<br><b>UniPG Stablecoin contract owner: </b>" + UniPGStablecoinMinter;  

        displayWalletsAndKeys();

    }

#### displayBlocks()

This function pulls the latest ten blocks from the blockchain to visualize them

    async function displayBlocks(){
        let blockhtml = document.getElementById("blocks");
        let latestBlock = await web3.eth.getBlockNumber();

        blockhtml.innerHTML = '';
        let blockrange = 10;
        for (let i = 0; i < blockrange; i++) {
            let temp = Number(latestBlock)-i;
            if (temp < 0){
                break;
            }else{
                web3.eth.getBlock(temp).then((data)=>{
                    blockhtml.innerHTML += data.hash + "<br>";
                })
            }
        } 
    }

#### displayTransactions()

    async function displayTransactions(){
        let transactionhtml = document.getElementById("transactions");
        let latestBlock = await web3.eth.getBlockNumber();

        transactionhtml.innerHTML = '';
        let blockrange = 10;
        
        for (let i = 0; i < blockrange; i++) {
          let temp = Number(latestBlock)-i;
            if (temp < 0){
                break;
            }else{
                web3.eth.getBlock(temp).then((data)=>{
                    transactionhtml.innerHTML += data.transactions[0] + "<br>";
                })
            }
        } 
    }

#### createTokenChart()

This function creates the linechart showing the total supply of token that exist. The data is found by looking inside the blockchain for rebase transactions, identified by the fields _to_ and _from_ that refer to the stablecoin contract address and minter. Since the rebase triggers the emission of a `Transfer` event each time the tokens are updated in a wallet, a for cycle is needed to check all the various amounts.
The data is formatted as a JSON and then drawn.

    async function createTokenChart(test=true) {
        const drawTokenLineChart = (data) => {
            ...  
        };

        if(test){
            ...
        }else{
            let latestBlock = await web3.eth.getBlockNumber();
            let dayrange = 20;
            let index = 0;
            let mydata = '[';
            while(dayrange>0){
                let temp = Number(latestBlock)-index;
                if (temp < 0){
                    break;
                }else{
                    let block = await web3.eth.getBlock(Number(latestBlock)-index);
                    let transaction = await web3.eth.getTransaction(block.transactions[0]); 
                    if(transaction.to == UniPGStablecoinAddress && transaction.from == UniPGStablecoinMinter){
                        let receipt = await web3.eth.getTransactionReceipt(transaction.hash);
                        let logs = receipt.logs;
                        let sum = 0;
                        for(let i =0; i<logs.length;i++){
                            let log = logs[i];
                            let decoded = await web3.eth.abi.decodeLog(UniPGStablecoinABI[3].inputs, log.data, log.topics);
                            let token = Number(BigInt(decoded['0']));
                            sum += token;
                        }
                        mydata += '{"day": ' + dayrange + ', "token": ' + sum + '},';
                        dayrange--;
                    }
                index++;
                }      
            }
            mydata = mydata.slice(0, -1) + ']';
            tokenSupplyData = JSON.parse(mydata);
            
            drawTokenLineChart(tokenSupplyData);
        }
    }

#### createCoinChart()

This function scans the blockchain looking for transactions that changed the price of the coins, recognized by the _to_ and _from_ fields of the transaction that refer to the coin contract address and minter.

    async function createCoinChart(test=true) {
        const drawCoinLineChart = (data) => {
            ...
        };

        if(test){
          ....
        }else{
            let latestBlock = await web3.eth.getBlockNumber();
            let dayrange = 20;
            let index = 0;
            let mydata = '[';
            while(dayrange>0){
                let temp = Number(latestBlock)-index;
                if (temp < 0){
                    break;
                }else{
                    let block = await web3.eth.getBlock(Number(latestBlock)-index);
                    let transaction = await web3.eth.getTransaction(block.transactions[0]); 
                    if(transaction.to == PriceGeneratorAddress && transaction.from == PriceGeneratorMinter){
                        let decoded = await web3.eth.abi.decodeParameters(PriceGeneratorABI[3].inputs, transaction.input.slice(10));
                        let price = BigInt(decoded['0']);
                        mydata += '{"day": ' + dayrange + ', "coin": ' +  Number(price).toExponential()/Number(BigInt(1e18)).toExponential() + '},';
                        dayrange--;
                    }
                    index++;
                }      
            }
            mydata = mydata.slice(0, -1) + ']';
            coinPriceData = JSON.parse(mydata);
            
            drawCoinLineChart(coinPriceData);
        }
    }

### Interaction functions

These functions are invoked by the various buttons in the html page to trigger specific blockchain functions

#### getTotalSupply()

This serves as an example of getter functions, since they dont alter the state in the blockchain and can be invoked without creating a transaction.
The check UniPGStablecoin == _undefined_ is needed to ensure that the asynchronous code doesnt encounter problems.

    async function getTotalSupply(){
        var n = document.getElementById("token-total-supply");
        if(UniPGStablecoin == undefined){
            UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
        }
        UniPGStablecoin.methods.totalSupply().call().then((data)=>{
            n.innerHTML = " &emsp;" + data ;
        });
    }

#### mintTokens()

This serves as an example of a function that needs to change the state of the blockchain, requiring a signed transaction with a sufficient amount of gas for the computation

    async function mintTokens(){
        var minter = document.getElementById("user-wallet").value;
        var account = document.getElementById("target-wallet").value;
        var amount = document.getElementById("token-amount").value;
        if (UniPGStablecoin == undefined){
            UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
        }

        var gasAmount = await UniPGStablecoin.methods.mint(account, amount).estimateGas({from: minter});
        var gasPrice = await web3.eth.getGasPrice();
        if(minter != UniPGStablecoinMinter){
            return;
        }
        
        const privateKey = wallets.private_keys[minter];
        var transaction = {
            'from': minter,
            'to': UniPGStablecoinAddress,
            'gas': gasAmount,
            'gasPrice': gasPrice,
            'data': UniPGStablecoin.methods.mint(account, amount).encodeABI(),
        };
        web3.eth.accounts.signTransaction(transaction, privateKey).then(signed => {
            web3.eth.sendSignedTransaction(signed.rawTransaction, function(error, hash) {
                if (!error) {
                    console.log("The hash of your transaction is: ", hash);
                } else {
                    console.log("Something went wrong while submitting your transaction:", error)
                }
            })
        });
    }