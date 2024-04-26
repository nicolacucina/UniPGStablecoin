package simulation;

import java.util.LinkedList;
import java.io.PrintWriter;


public class Contract {
    
    private LinkedList<Wallet> wallets;
    private double value;
    private boolean rebase;
    private static Contract instance = null; 

    private Contract(){
        this.wallets = new LinkedList<Wallet>();
        this.value = 1.0;
        this.rebase = false;
    }

    public static Contract getInstance(){
        if(instance == null){
            instance = new Contract();
        }
        return instance;
    }
    
    public void rebase(){
        // there are some numerical errors because of double precision
        rebase = true;
        PrintWriter out = Simulation.getWriter();
        out.println("Rebase");
        out.println("Old token amount: " + getNumberofToken()); 
        double newTokenAmount = getNumberofToken()*value;
        out.println();
        for(Wallet wallet : wallets){
            out.println("Wallet: "+wallet.getName() + ", old token amount: " + wallet.getToken() + ", percentage: " + wallet.getPercentage());
            wallet.setToken(newTokenAmount*wallet.getPercentage());
            out.println("Wallet: "+wallet.getName() + ", new token amount: " + wallet.getToken() + ", percentage: " + wallet.getPercentage());
            out.println();
        }
        rebase = false;
        
        out.println("Rebase finished, token amount: " + getNumberofToken());
        out.println();
    }
    
    public boolean tranfer(Wallet fromWallet, Wallet toWallet, double tokenAmount){
        if(!rebase){
            if(fromWallet.getToken() >= tokenAmount){
                fromWallet.setToken(fromWallet.getToken() - tokenAmount);
                toWallet.setToken(toWallet.getToken() + tokenAmount);
                return true;
            }
            else{
                Simulation.getWriter().println("Transaction between "+ fromWallet.getName() + " and "+ toWallet.getName() + " not allowed, not enough tokens");
                return false;
            }
        } else{
            Simulation.getWriter().println("Rebase in progress, transaction not allowed");
            return false;
        }
    }

    public void initPercentages(){
        for(Wallet wallet : wallets){
            wallet.initPercentage();
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
