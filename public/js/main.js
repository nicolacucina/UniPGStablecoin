// Define contract paramenters 
const testnet = 'https://rpc2.sepolia.org';
console.log('Connecting to ' + testnet);
const web3 = new Web3(new Web3.providers.HttpProvider(testnet));

// Test the connection
web3.eth.getBlock('latest').then(console.log)
web3.eth.getBlockNumber().then(console.log)

const jsonInterface = []
const contractAddress = '';
const Contract = new web3.eth.Contract(jsonInterface, contractAddress);
const myAddress = '';
const minterAddress = '';

// Draw the charts

createCoinChart();

createTokenChart();

// Placeholder methods to test interaction with the html page

async function getTokenName(){
    var n = document.getElementById("token-name");
    //n.innerHTML = await Contract.methods.name().call().then()
    n.innerHTML = " &emsp; UniPG Stablecoin";
}

async function getTokenSymbol(){
    var img = document.getElementById("token-symbol");
    img.src = "./public/img/Stablecoin.png";
    img.alt = "UniPG Stablecoin";
    img.width = 200;
    img.height = 200;
}

async function getBalance(){
    var n = document.getElementById("wallet-balance");
    //var balance = await Contract.methods.balances(myAddress).call().then(n.innerHTML = "&emsp;" + balance)
    balance = 1000;
    n.innerHTML = "&emsp;" + balance;
}

async function getTotalSupply(){
    var n = document.getElementById("token-total-supply");
    //var supply = await Contract.methods.totalSupply().call().then()
    supply = 1000000;
    n.innerHTML = "&emsp;" + supply;
}

