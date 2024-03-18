// Define contract paramenters

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

async function createContractParams(){

    // const testnet = 'https://rpc2.sepolia.org';
    const testnet = 'http://localhost:8545';
    console.log('Connecting to ' + testnet);
    web3 = new Web3(new Web3.providers.HttpProvider(testnet));
    console.log('Web3: ');
    console.log(web3);

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

    d3.json('./TruffleTest/build/contracts/PriceGenerator.json')
        .then((data) => {
            PriceGeneratorABI = data.abi;
            console.log('Price Generator ABI: ');
            console.log(PriceGeneratorABI);
        });
    
    d3.json('./TruffleTest/build/contracts/UniPGStablecoin.json')
        .then((data) => {
            UniPGStablecoinABI = data.abi;
            console.log('UniPG Stablecoin ABI: ')
            console.log(UniPGStablecoinABI);
        });

    // Get contract address from Ganache

    let FirstBlockHash = await web3.eth.getBlock('1');
    let PriceGeneratorDeployment = await web3.eth.getTransactionReceipt(FirstBlockHash.transactions[0]);
    console.log(PriceGeneratorDeployment);
    PriceGeneratorAddress = PriceGeneratorDeployment.contractAddress
    PriceGeneratorMinter = PriceGeneratorDeployment.from;
    console.log('Price Generator contract address: ');
    console.log(PriceGeneratorAddress);
    console.log('Price Generator contract owner: ');
    console.log(PriceGeneratorMinter);

    let SecondBlockHash = await web3.eth.getBlock('2');
    let UniPGStablecoinDeployment = await web3.eth.getTransactionReceipt(SecondBlockHash.transactions[0]);
    console.log(UniPGStablecoinDeployment);
    UniPGStablecoinAddress = UniPGStablecoinDeployment.contractAddress
    UniPGStablecoinMinter = UniPGStablecoinDeployment.from;
    console.log('UniPG Stablecoin contract address: ');
    console.log(UniPGStablecoinAddress);
    console.log('UniPG Stablecoin contract owner: ');
    console.log(UniPGStablecoinMinter);

    
    wallets = await web3.eth.getAccounts().then((data)=>{  
        console.log('Accounts: ' + data);
        return data;
    });

    // display information on the page

    var addresshtml = document.getElementById("contract-address");
    addresshtml.innerHTML = "<b>Price Generator contract address: </b>" + PriceGeneratorAddress + "<br><b>UniPG Stablecoin contract address: </b>" + UniPGStablecoinAddress;  
    
    var minterhtml = document.getElementById("contract-owner");
    minterhtml.innerHTML = "<b>Price Generator contract owner: </b>" + PriceGeneratorMinter + "<br><b>UniPG Stablecoin contract owner: </b>" + UniPGStablecoinMinter;  

    var n = document.getElementById("wallet-list");
    wallets.forEach((wallet)=>{
        n.innerHTML += "<option value=" + wallet + "> - " + wallet + "</option>";
    });
}

createContractParams();

// Draw the charts

createCoinChart();

createTokenChart();

async function getTokenName(){
    var n = document.getElementById("token-name");
    if(UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.name().call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data ;
    });
}

async function getTokenSymbol(){
    var n = document.getElementById("token-symbol");
    if(UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.symbol().call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data ;
    });
}

async function getTokenDecimals(){
    var n = document.getElementById("token-decimals");
    if(UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.decimals().call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data ;
    });
}

async function getTotalSupply(){
    var n = document.getElementById("token-total-supply");
    if(UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.totalSupply().call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data ;
    });
}

async function getPrice(){
    var n = document.getElementById("token-price");
    if (PriceGenerator == undefined){
        PriceGenerator = new web3.eth.Contract(PriceGeneratorABI, PriceGeneratorAddress);
    }
    PriceGenerator.methods.price().call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data/BigInt(1e18) ;
    });
}

async function getBalance(){
    var n = document.getElementById("wallet-balance");
    var account = document.getElementById("user-wallet").value;
    console.log(account);
    if (UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.balanceOf(account).call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data;
    });
}

async function mintTokens(){
    var minter = document.getElementById("user-wallet").value;
    var account = document.getElementById("target-wallet").value;
    var amount = document.getElementById("token-amount").value;
    console.log(account);
    console.log(amount);
    if (UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }

    // since this method changes the state of the contract, it requires a signed transaction
    var gasAmount = await UniPGStablecoin.methods.mint(account, amount).estimateGas({from: minter});
    console.log("Gas amount is " + gasAmount);
    var gasPrice = await web3.eth.getGasPrice();
    const privateKey = "0x6fe7ac8879f36047d68dc946aee079044405cbc13aa74f6eef95af93f688afbb"
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
    })
}