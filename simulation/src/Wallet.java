package simulation.src;

public abstract class Wallet{
    
    private double token;
    private double money;
    private double percentage;

    public Wallet(double token, double money){
        this.token = token;
        this.money = money;
        this.percentage = 0.0;
    }

    public abstract void buy(Wallet fromWallet, double tokenAmount);

    public abstract void sell(Wallet toWallet, double tokenAmount);

    public double getToken(){
        return token;
    }
    
    public void setToken(double token){
        this.token = token;
        //changes in the token amount will affect the percentage
        this.percentage = this.token/Contract.getNumberofToken();
    }
    
    public double getMoney(){
        return money;
    }

    public void setMoney(double money){
        this.money = money;
    }

    public double getPercentage(){
        return percentage;
    }

    public void setPercentage(double percentage){
        this.percentage = percentage;
    }
}