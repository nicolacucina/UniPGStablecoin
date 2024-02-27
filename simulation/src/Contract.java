package simulation.src;
import java.util.LinkedList;

public abstract class Contract {
    
    private LinkedList<Wallet> wallets;
    double value;

    Contract(){
        wallets = new LinkedList<Wallet>();
    }
    
    public abstract void rebase();

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

    public static double getNumberofToken(){
        double tokenAmount = 0;
        for(Wallet wallet : wallets){
            tokenAmount += wallet.getToken();
        }
        return tokenAmount;
    }
}
