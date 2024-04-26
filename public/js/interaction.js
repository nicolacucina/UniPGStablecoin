////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Interaction methods
////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    n.innerHTML = '';
    if (PriceGenerator == undefined){
        PriceGenerator = new web3.eth.Contract(PriceGeneratorABI, PriceGeneratorAddress);
    }
    PriceGenerator.methods.price().call().then((data)=>{
        let temp = Number(data).toExponential()/Number(BigInt(1e18)).toExponential();
        console.log(temp);
        n.innerHTML = " &emsp;" + temp + " Euro";
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

async function getAllowance(){
    var n = document.getElementById("wallet-allowance");
    var owner = document.getElementById("owner-wallet").value;
    var recipient = document.getElementById("recipient-wallet").value;
    console.log('owner:' + owner);
    console.log('recipient:' + recipient);
    if (UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }
    UniPGStablecoin.methods.allowance(owner,recipient).call().then((data)=>{
        console.log(data)
        n.innerHTML = " &emsp;" + data;
    });
}

async function mintTokens(){
    var minter = document.getElementById("minter-wallet").value;
    var account = document.getElementById("target-wallet").value;
    var amount = document.getElementById("token-amount").value;
    console.log('minter:'+ minter);
    console.log('account:' + account);
    console.log('amount:' + amount);
    if (UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }

    // since this method changes the state of the contract, it requires a signed transaction
    var gasAmount = await UniPGStablecoin.methods.mint(account, amount).estimateGas({from: minter});
    console.log("Gas amount is " + gasAmount);
    var gasPrice = await web3.eth.getGasPrice();
    console.log("Gas price is " + gasPrice);
    if(minter != UniPGStablecoinMinter){
        console.log("You are not the owner of this contract");
        alert("You are not the owner of this contract");
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

async function approveAllowance() {
    var owner = document.getElementById("from-wallet-allowance").value;
    var recipient = document.getElementById("to-wallet-allowance").value;
    var amount = document.getElementById("amount-permitted-allowance").value;

    try {
        if (UniPGStablecoin === undefined) {
            UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
        }
        await UniPGStablecoin.methods.approve(recipient, amount).send({ from: owner });
        console.log("Approvazione dell'allowance eseguita con successo");
    } catch (error) {
        console.error("Errore durante l'approvazione dell'allowance:", error);
    }
}

async function transferTokens(){
    var from = document.getElementById("from-wallet").value;
    var to = document.getElementById("to-wallet").value;
    var amount = document.getElementById("transfer-amount").value;
    if (UniPGStablecoin == undefined){
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }

    // since this method changes the state of the contract, it requires a signed transaction
    var gasAmount = await UniPGStablecoin.methods.transfer(to, amount).estimateGas({from: from});
    console.log("Gas amount is " + gasAmount);
    var gasPrice = await web3.eth.getGasPrice();
    console.log("Gas price is " + gasPrice);

    const privateKey = wallets.private_keys[from];
    var transaction = {
        'from': from,
        'to': UniPGStablecoinAddress,
        'gas': gasAmount,
        'gasPrice': gasPrice,
        'data': UniPGStablecoin.methods.transfer(to, amount).encodeABI(),
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

async function transferIfAllowed() {
    var owner = document.getElementById("owner-wallet-allowance").value;
    var proxy = document.getElementById("proxy-wallet-allowance").value;
    var recipient = document.getElementById("recipient-wallet-allowance").value;
    var amount = document.getElementById("transfer-amount-allowance").value;

    if (UniPGStablecoin === undefined) {
        UniPGStablecoin = new web3.eth.Contract(UniPGStablecoinABI, UniPGStablecoinAddress);
    }

    try {
        // Controlla l'allowance dell'account corrente per il destinatario
        const allowance = await UniPGStablecoin.methods.allowance(owner, proxy).call();
        console.log("Allowance:", allowance);

        // Se l'allowance è sufficiente, esegui il trasferimento
        if (allowance >= amount) {

            // Ottieni la quantità di gas stimata
            const gasAmount = await UniPGStablecoin.methods.transferFrom(owner, recipient, amount)
            .estimateGas({ from: proxy})
            .then((data)=>{console.log("Gas amount is:", data); return data;});
            
            // Ottieni il prezzo del gas attuale
            const gasPrice = await web3.eth.getGasPrice()
            .then((data)=>{console.log("Gas price is:", data); return data;});

            // Crea la transazione firmata
            const privateKey = wallets.private_keys[proxy];
            console.log("Private key:", privateKey);
            const encodedABI = UniPGStablecoin.methods.transferFrom(owner, recipient, amount).encodeABI();
            const transaction = {
                'from': proxy,
                'to': UniPGStablecoinAddress,
                'gas': gasAmount,
                'gasPrice': gasPrice,
                'data': encodedABI,
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
        } else {
            console.log("Insufficient allowance");
        }
    } catch (error) {
        console.error("Error during transfer:", error);
    // Gestione dell'errore
    }
}      

async function createWallet(){
// The data is lost if the page is realoaded, but the blockchain state has been changed
// Wallets created this way do not have any eth to call transactions

    var n = document.getElementById("wallet-creation");
    var password = document.getElementById("user-password").value;
    var wallet = await web3.eth.accounts.create(password);

    console.log('New wallet: ');
    console.log(wallet);
    n.innerHTML = " Wallet created: ";
    n.innerHTML += "<p class=\"row\"> " + wallet.address + "</p>";
    n.innerHTML += "<p class=\"row\"> " + wallet.privateKey + "</p>";

    console.log('Old wallets: ');
    console.log(wallets['addresses']);
    wallets['addresses'] = JSON.parse(JSON.stringify(wallets['addresses']).slice(0, -1) + ', "' + wallet.address + '":"' + wallet.address + '"}');
    console.log('New wallets: ')
    console.log(wallets['addresses']);

    console.log('Old private keys: ');
    console.log(wallets['private_keys']);
    wallets['private_keys'] = JSON.parse(JSON.stringify(wallets['private_keys']).slice(0, -1) + ', "' + wallet.address + '":"' + wallet.privateKey + '"}');
    console.log('New private keys: ');
    console.log(wallets['private_keys']);

    // Update blocks and wallets
    displayBlocks();
    displayWalletsAndKeys();
}