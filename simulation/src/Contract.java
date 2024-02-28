package simulation.src;

import java.util.LinkedList;


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
    
    public void rebase(){
        rebase = true;
        double newTokenAmount = getNumberofToken()/getValue();
        for(Wallet wallet : wallets){
            wallet.setToken(newTokenAmount*wallet.getPercentage());
        }
        rebase = false;
    }
    
    public void tranfer(Wallet fromWallet, Wallet toWallet, double tokenAmount){
        if(!rebase){
            if(fromWallet.getToken() >= tokenAmount){
                fromWallet.setToken(fromWallet.getToken() - tokenAmount);
                toWallet.setToken(toWallet.getToken() + tokenAmount);
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
