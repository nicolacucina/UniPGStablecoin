////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Define variables
////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Define contract parameters
////////////////////////////////////////////////////////////////////////////////////////////////////////////

async function createContractParams(){
    const testnet = 'http://localhost:8545';
    console.log('Connecting to ' + testnet);
    web3 = new Web3(new Web3.providers.HttpProvider(testnet));
    console.log('Web3: ');
    console.log(web3);

    ////////////////////////////////////////////
    // Test the connection
    ////////////////////////////////////////////

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

    ////////////////////////////////////////////
    // Get ABI from the Truffle build
    ////////////////////////////////////////////

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

    ////////////////////////////////////////////
    // Get contract address from Ganache
    ////////////////////////////////////////////

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

    ////////////////////////////////////////////
    // Get the wallets
    ////////////////////////////////////////////

    wallets = await d3.json('./public/data/keys.json')
        .then((data)=>{
        console.log('Accounts: ')
        console.log(data);
        return data;
        });

    ////////////////////////////////////////////
    // Display information on the page
    ////////////////////////////////////////////

    displayBlocks();

    displayTransactions();
        
    var addresshtml = document.getElementById("contract-address");
    addresshtml.innerHTML = "<b>Price Generator contract address: </b>" + PriceGeneratorAddress + "<br><b>UniPG Stablecoin contract address: </b>" + UniPGStablecoinAddress;  

    var minterhtml = document.getElementById("contract-owner");
    minterhtml.innerHTML = "<b>Price Generator contract owner: </b>" + PriceGeneratorMinter + "<br><b>UniPG Stablecoin contract owner: </b>" + UniPGStablecoinMinter;  

    displayWalletsAndKeys();

}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
createContractParams().then(()=>{ 
    console.log('Contract parameters created')
    ////////////////////////////////////////////
    // Draw the charts
    ////////////////////////////////////////////
    createCoinChart(test=false);
    createTokenChart(test=false);
});
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
async function displayBlocks(){
    let blockhtml = document.getElementById("blocks");
    let latestBlock = await web3.eth.getBlockNumber();
    // console.log('Latest block number: ');
    // console.log(latestBlock);

    blockhtml.innerHTML = '';
    let blockrange = 10;

    for (let i = 0; i < blockrange; i++) {
        let temp = Number(latestBlock)-i;
        if (temp < 0){
            break;
        }else{
            web3.eth.getBlock(temp).then((data)=>{
                //console.log(data);
                //blocks.push(data);
                blockhtml.innerHTML += data.hash + "<br>";
            })
        }
    } 
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
async function displayTransactions(){
    let transactionhtml = document.getElementById("transactions");
    let latestBlock = await web3.eth.getBlockNumber();
    // console.log('Latest block number: ');
    // console.log(latestBlock);

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
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
async function displayWalletsAndKeys(){
    var n = document.getElementById("wallet-list");
    console.log('Addresses: ');
    console.log(wallets.addresses);
    n.innerHTML = '';
    let i = 0;
    for(let address in wallets.addresses){
        n.innerHTML += "<p class=\"row\"> " + i.toString() +"): " + wallets.addresses[address] + "</p>";
        i++;
    }
    let j=0;
    var m = document.getElementById("private-keys");
    console.log('Private keys: ');
    console.log(wallets.private_keys);
    m.innerHTML = '';
    for(let key in wallets.private_keys){
        m.innerHTML += "<p class=\"row\"> " + j.toString() +"): " + wallets.private_keys[key] + "</p>";
        j++;
    }   
}