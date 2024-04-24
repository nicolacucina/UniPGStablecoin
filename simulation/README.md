# Stablecoin simulation using Java

This is a simulation of the behaviour of a Smart Contract that creates a token with a stable price over time.

## Contract

This class represents the Smart Contract. Acts as a the central point of all transactions and logic. Here are reported the important steps of the most important functions
    
    public void rebase(){
        rebase = true;
        double newTokenAmount = getNumberofToken()*value;
        for(Wallet wallet : wallets){
            wallet.setToken(newTokenAmount*wallet.getPercentage());
        }
        rebase = false;
    }

The flag _rebase_ is used to signal that the contract will not accept transactions during the rebase transactions. To update the token supply, the _value_ stored in the contract is used along side the _percentage_ that each wallet owns
    
    public void tranfer(Wallet fromWallet, Wallet toWallet, double tokenAmount){
        if(!rebase){
            if(fromWallet.getToken() >= tokenAmount){
                fromWallet.setToken(fromWallet.getToken() - tokenAmount);
                toWallet.setToken(toWallet.getToken() + tokenAmount);
            }
            else{
                Simulation.getWriter().println("Transaction between "+ fromWallet.getName() + " and "+ toWallet.getName() + " not allowed, not enough tokens");
            }
        }
        else{
            Simulation.getWriter().println("Rebase in progress, transaction not allowed");
        }
    }

In this simulation, the costs of using the blockchain for the computations is not taken into account since the aim is to show that the rebase logic is working as intended

## Wallet

This class represents a user wallet. Each wallet has a _token_ amount that it owns, a _money_ variable that represents the investements or the earnings of the wallet and a _percentage_ used to keep track of the proportion of tokens owned by each wallet

    public void buy(Wallet fromWallet, double tokenAmount, Exchange exchange, double price){
        contract.tranfer(fromWallet, this, tokenAmount);
        exchange.transfer(this, fromWallet, price, tokenAmount);
    }

    public void sell(Wallet toWallet, double tokenAmount, Exchange exchange, double price){
        contract.tranfer(this, toWallet, tokenAmount);
        exchange.transfer(toWallet, this, price, -tokenAmount);
    }

the _buy()_ and _sell()_ methods are used by the wallets to signal their intentions, and are split between a call to the contract to transfer the tokens and a call to the exchange to transfer money

## Exchange

This models a platform where tokens can be exchanged at a certain price. Each day the wallets choose an exchange wehere they want to operate and they are added either to a list of _buyers_ or _sellers_. The Exchange keeps track of the _demand_, _supply_ and _price_ of the tokens, modeled as the amounts of tokens available in the platform.

    public void transfer(Wallet fromWallet, Wallet toWallet, double price, double tokenAmount){
        fromWallet.setMoney(fromWallet.getMoney() - price);
        toWallet.setMoney(toWallet.getMoney() + price);
        supply -= tokenAmount;
        demand += tokenAmount;
        this.price = (this.price * w1 + price * w2) / (w1 + w2);
    }   

The exchange does not concern itself with the transfer of token but only of the payment of the tokens. The price is adjusted as a weighted mean of the previous price and the new one because other models where too difficult to implement and led to unwanted behavious

## Simulation

Using the previous classes and methods, an interval of days is simulated where the various wallets exchange with each other the tokens and the rebase logic is used to update the total supply based on the price at the end of each day
Inside _/bin/data/_ the _simulation.properties_ file can be found where all the simulation parameters can be set

    logName=run1
    seed=5
    days=20
    numberOfInitialWallets=20
    initialTokenAmount=20
    maxMoneyAmount=100
    buyProbability=0.4
    sellProbability=0.4
    percentageOfNewBuyers=0.1
    numberOfExchanges=3
    ExchangeWeight1=0.85
    ExchangeWeight2=0.15

The two scripts _compile.bat_ and _run.bat_ simplify the execution of the simulation. The output is stored in the _/bin/data/_ folder in two files

- _run-name.txt_ : contains all the transactions that have been simulated, along side all the rebase calculations so that the results can be checked for errors
- _run-name.csv_ : is a summary of all the data in _csv_ format so that a python script can be used for visualization