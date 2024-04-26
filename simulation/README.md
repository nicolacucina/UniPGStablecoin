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
        boolean result = contract.transfer(fromWallet, this, tokenAmount);
        if(result){
            exchange.buy(this, tokenAmount);
        }
    }

    public void sell(Wallet toWallet, double tokenAmount, Exchange exchange, double price){
        boolean result = contract.transfer(this, toWallet, tokenAmount);
        if(result){
            exchange.sell(this, tokenAmount);
        }
    }

the _buy()_ and _sell()_ methods are used by the wallets to signal their intentions, and are split between a call to the contract to transfer the tokens and a call to the exchange to transfer money. Money is trasfered only after the contract has transfered the tokens between the two wallets

## Exchange

This models a platform where tokens can be exchanged at a certain price. An Exchange is a certain type of `Wallet`, the main difference being the amount of token and money that it handles.
Each day the user wallets can choose one exchange where they can trade. This is done by adding the wallets either to a _buyers_ or _sellers_ list.
To compute the price at which the token are bought and sold, the exchange keeps track of _demand_, _supply_, _buyPrice_, _sellPrice_ and _priceGap_.

    public void buy(Wallet fromWallet, double tokenAmount){
        fromWallet.setMoney(fromWallet.getMoney() - (buyPrice*tokenAmount));
        this.setMoney(this.getMoney() + (buyPrice*tokenAmount));
        supply -= tokenAmount;   
        demand += tokenAmount;
        if(buyPrice + (buyPrice * w1) > 1.3){
            buyPrice = 1.3;
        } else {
            buyPrice += buyPrice * w1;
        }
        sellPrice = buyPrice - priceGap;
    }  

    public void sell(Wallet toWallet, double tokenAmount){
        toWallet.setMoney(toWallet.getMoney() + (sellPrice*tokenAmount));
        this.setMoney(this.getMoney() - (sellPrice*tokenAmount));
        supply += tokenAmount;
        demand -= tokenAmount;
        if(sellPrice - (sellPrice * w2) < 0.7){
            sellPrice = 0.7;
        } else {
            sellPrice -= sellPrice * w2;
        }
        buyPrice = sellPrice + priceGap;
    }   

The balance of the user wallet is not checked since negative money is accepted and can be interpreted as either an investement in the tokens or a loss of money.
_supply_ and _demand_ are updated using the amount of tokens transferred.
The _buyPrice_ and _sellPrice_ are adjusted using both the weights _w1_ e _w2_ and the _priceGap_

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
    exchangeDimension=50
    buyPrice=1.02
    sellPrice=1.0
    priceGap=0.02
    buyWeight1=0.05
    sellWeight2=0.05

The two scripts _compile.bat_ and _run.bat_ simplify the execution of the simulation. The output is stored in the _/bin/data/_ folder in two files

- _run-name.txt_ : contains all the transactions that have been simulated, along side all the rebase calculations so that the results can be checked for errors
- _run-name.csv_ : is a summary of all the data in _csv_ format so that a python script can be used for visualization