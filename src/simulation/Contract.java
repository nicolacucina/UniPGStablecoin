package simulation;

import java.util.LinkedList;
import java.io.PrintWriter;


public class Contract {
    
    // this is a representation of a ERC20 smart contract
    private LinkedList<Wallet> wallets;
    private double value;
    private boolean rebase;
    

    Contract(){
        this.wallets = new LinkedList<Wallet>();
        this.value = 1.0;
        this.rebase = false;
    }
    
    public void rebase(PrintWriter out){
        rebase = true;
        out.println("Rebase, old token amount: " + getNumberofToken());
        double newTokenAmount = getNumberofToken()*value; 
        out.println("Rebase, new token amount: " + newTokenAmount);
        out.println();
        for(Wallet wallet : wallets){
            System.out.println(wallet.getName() + " token amount: " + wallet.getToken() + " percentage: " + wallet.getPercentage());
            wallet.setToken(newTokenAmount*wallet.getPercentage());
            System.out.println(wallet.getName() + " token amount: " + wallet.getToken() + " percentage: " + wallet.getPercentage());
        }
        rebase = false;
        System.out.println("Rebase finished, token amount: " + getNumberofToken());
        System.out.println();
    }
    
    public void tranfer(Wallet fromWallet, Wallet toWallet, double tokenAmount){
        if(!rebase){
            if(fromWallet.getToken() >= tokenAmount){
                fromWallet.setToken(fromWallet.getToken() - tokenAmount);
                toWallet.setToken(toWallet.getToken() + tokenAmount);
            }
            else{
                System.out.println("Transaction not allowed, not enough tokens");
            }
        }
        else{
            System.out.println("Rebase in progress, transaction not allowed");
        }
    }

    //////////////////////////////////////////GETTERS AND SETTERS//////////////////////////////////////////

    public void addWallet(Wallet wallet){
        wallets.add(wallet);
    }

    public LinkedList<Wallet> getWallets(){
        return wallets;
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public double getNumberofToken(){
        double tokenAmount = 0;
        for(Wallet wallet : wallets){
            tokenAmount += wallet.getToken();
        }
        return tokenAmount;
    }

    public boolean isRebase(){
        return rebase;
    }
}
