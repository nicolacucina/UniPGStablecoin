package simulation;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Random;

public class Exchange extends Wallet{

    private double demand;
    private double supply; 
    private double buyPrice; // price at which users buys tokens from the exchange
    private double sellPrice; // price at which users sells tokens to the exchange
    private double priceGap; // fixed difference between buy and sell price, models the profit of the exchange
    private LinkedList<Wallet> buyerWallets;
    private LinkedList<Wallet> sellerWallets;
    private Random random;
    private double w1;
    private double w2;


    Exchange(String name, double moneyReserve, double tokenReserve, Contract contract, double demand, double supply, double buyprice, double sellprice, double priceGap, double w1, double w2){
        super(name, tokenReserve, moneyReserve, contract);
        this.demand = demand;
        this.supply = supply; 
        this.buyPrice = buyprice;
        this.sellPrice = sellprice;
        this.priceGap = priceGap;
        this.buyerWallets = new LinkedList<Wallet>();
        this.sellerWallets = new LinkedList<Wallet>();
        this.random = new Random(Simulation.getSeed());
        this.w1 = w1;
        this.w2 = w2;
    }

    public void buy(Wallet fromWallet, double tokenAmount){
        // we do not check if the user has enough money to buy the token because negative money is 
        // interpreted an investement in the token
        fromWallet.setMoney(fromWallet.getMoney() - (buyPrice*tokenAmount));
        this.setMoney(this.getMoney() + (buyPrice*tokenAmount));

        PrintWriter out = Simulation.getWriter();
        out.print("Supply was " + supply + ", Demand was " +  demand + " and now ");
        supply -= tokenAmount; 
        out.print("Supply is " + supply);
        demand += tokenAmount;
        out.println(" and Demand is " + demand);

        //If people buy, the buy price goes up
        out.print("Buy price was " + buyPrice + " and now ");
        if(buyPrice + (buyPrice * w1) > 1.3){
            buyPrice = 1.3;
        } else {
            buyPrice += buyPrice * w1;
        }
        out.println("Buy price is " + buyPrice);

        //Update sell price using priceGap
        out.print("Sell price was " + sellPrice + " and now ");
        sellPrice = buyPrice - priceGap;
        out.println("Sell price is " + sellPrice);
    }   

    public void sell(Wallet toWallet, double tokenAmount){
        toWallet.setMoney(toWallet.getMoney() + (sellPrice*tokenAmount));
        this.setMoney(this.getMoney() - (sellPrice*tokenAmount));

        PrintWriter out = Simulation.getWriter();
        out.print("Supply was " + supply + ", Demand was " +  demand + " and now ");
        supply += tokenAmount; 
        out.print("Supply is " + supply);
        demand -= tokenAmount;
        out.println(" and Demand is " + demand);

        //If people sell, the sell price goes down
        out.print("Sell price was " + sellPrice + " and now ");
        if(sellPrice - (sellPrice * w2) < 0.7){
            sellPrice = 0.7;
        } else {
            sellPrice -= sellPrice * w2;
        }
        out.println("Sell price is " + sellPrice);

        //Update buy price using priceGap
        out.print("Buy price was " + buyPrice + " and now ");
        buyPrice = sellPrice + priceGap;
        out.println("Buy price is " + buyPrice);
    }   

    //////////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////
    
    public void addBuyerWallet(Wallet wallet){
        buyerWallets.add(wallet);
    }

    public Wallet getBuyerWallet(){
        Wallet buyer = buyerWallets.get(this.random.nextInt(buyerWallets.size()));
        buyerWallets.remove(buyer);
        return buyer;
    }

    public LinkedList<Wallet> getBuyerWallets(){
        return buyerWallets;
    }  

    public void addSellerWallet(Wallet wallet){
        sellerWallets.add(wallet);
    }

    public Wallet getSellerWallet(){
        Wallet seller = sellerWallets.get(this.random.nextInt(sellerWallets.size()));
        sellerWallets.remove(seller);
        return seller;
    }

    public LinkedList<Wallet> getSellerWallets(){
        return sellerWallets;
    }

    public void resetWallets(){
        buyerWallets = new LinkedList<Wallet>();
        sellerWallets= new LinkedList<Wallet>();
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

    public double getBuyPrice(){
        return buyPrice;
    }

    public void setBuyPrice(double price){
        this.buyPrice = price;
    }
    
    public double getSellPrice(){
        return sellPrice;
    }

    public void setSellPrice(double price){
        this.sellPrice = price;
    }
}