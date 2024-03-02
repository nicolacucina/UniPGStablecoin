package simulation;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Random;

public class Exchange{

    private String name;
    private double demand;
    private double supply;
    private double price;
    private LinkedList<Wallet> buyerWallets;
    private LinkedList<Wallet> sellerWallets;
    private Random random;

    Exchange(String name, double demand, double supply, double price){
        this.name = name;
        this.demand = demand;
        this.supply = supply;
        this.price = price;
        this.buyerWallets = new LinkedList<Wallet>();
        this.sellerWallets = new LinkedList<Wallet>();
        this.random = new Random(Simulation.getSeed());
    }

    public void transfer(Wallet fromWallet, Wallet toWallet, double price, double tokenAmount){
        // riorganize
        fromWallet.setMoney(fromWallet.getMoney() - price);
        toWallet.setMoney(toWallet.getMoney() + price);
        PrintWriter out = Simulation.getWriter();
        out.print("Supply was " + supply + ", Demand was " +  demand + " and now ");
        supply -= tokenAmount;
        out.print("Supply is " + supply);
        demand += tokenAmount;
        out.println(" and Demand is " + demand);

        //Price update, weighted average between old price and price of the latest transaction
        out.print("Price was " + this.price + " and now ");
        double w1 = 0.9;
        double w2 = 0.1;
        this.price = (this.price * w1 + price * w2) / (w1 + w2);
        out.println("Price is " + this.price);
    }   

    //////////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////

    public String getName(){
        return name;
    }

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
        buyerWallets.remove(seller);
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

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }   
}