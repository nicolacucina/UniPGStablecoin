package simulation;

import java.util.LinkedList;


public class Exchange{

    private double demand;
    private double supply;
    private double price;
    private LinkedList<Wallet> wallets;

    Exchange(double demand, double supply, double price){
        this.demand = demand;
        this.supply = supply;
        this.price = price;
        this.wallets = new LinkedList<Wallet>();
    }

    public void transfer(Wallet fromWallet, Wallet toWallet, double price, double tokenAmount){
        fromWallet.setMoney(fromWallet.getMoney() - price);
        toWallet.setMoney(toWallet.getMoney() + price);
        supply -= tokenAmount;
        demand += tokenAmount;

        //Price update, weighted average between old price and price of the latest transaction
        double w1 = 0.9;
        double w2 = 0.1;
        this.price = (this.price * w1 + price * w2) / (w1 + w2);
    }

    //////////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////

    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }

    public LinkedList<Wallet> getWallets(){
        return wallets;
    }

    public void resetWallets(){
        wallets = new LinkedList<Wallet>();
    }

    public double getDemand(){
        return demand;
    }

    public void setDemand(double demand){
        this.demand = demand;
    }

    public double getSupply(){
        return supply;
    }   

    public void setSupply(double supply){
        this.supply = supply;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }   
}