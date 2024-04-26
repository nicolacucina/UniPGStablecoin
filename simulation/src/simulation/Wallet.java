package simulation;


public class Wallet{
    
    // This is both the cryptowallet and the "bank account" of the user
    private String name;
    private double token;
    private double money;
    private double percentage;
    private Contract contract;

    Wallet(String name, double token, double money, Contract contract){
        this.name = name;
        this.token = token;
        this.money = -money; //initial investment
        this.contract = contract;
        this.percentage = 0.0;
    }

    //Buying and selling is split up in two transactions, 
    //one for the token transfer and one for the money transfer
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

//////////////////////////////////////////GETTERS AND SETTERS////////////////////////////////////////// 

    public String getName(){
        return name;
    }

    public double getToken(){
        return token;
    }
    
    public void setToken(double token){
        this.token = token;

        //Changes in the token amount will affect the percentage, 
        //only during transactions and not during rebase
        if(!contract.isRebase()){
            percentage = this.token/contract.getNumberofToken();
        }
    }
    
    public double getMoney(){
        return money;
    }

    public void setMoney(double money){
        this.money = money;
    }

    public void initPercentage(){
        percentage = token/contract.getNumberofToken();
    }

    public double getPercentage(){
        return percentage;
    }

    public void setPercentage(double percentage){
        this.percentage = percentage;
    }

    public Contract getContract(){
        return contract;
    }
}