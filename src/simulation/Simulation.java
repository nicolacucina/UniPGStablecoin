package simulation;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


public class Simulation {

    public static long seed;
    public static void main(String[] args) {
        // Simulation parameters
        seed = Long.parseLong(args[0]);
        Random random = new Random(seed);

        int days = Integer.parseInt(args[1]);
        int numberOfInitialWallets = Integer.parseInt(args[2]);
        int initialTokenAmount, initialMoneyAmount; 
        initialTokenAmount = initialMoneyAmount = Integer.parseInt(args[3]); // Assures that the initial price is 1
        double buyProbability = 0.4;
        double sellProbability = 0.4; 
        double percentageOfNewBuyers = 0.1;
        int numberOfExchanges = 3;  
        
        double[] prices = new double[days];
        double[] tokenAmounts = new double[days];

        // Setup phase, first minting of the tokens
        Contract contract = new Contract();
        for(int i = 0; i < numberOfInitialWallets; i++){
            Wallet wallet = new Wallet(Integer.toString(i), initialTokenAmount, initialMoneyAmount, contract);
            contract.addWallet(wallet);
        }

        System.out.println(numberOfInitialWallets + " wallets * " + initialTokenAmount + " token per account = "+contract.getNumberofToken() + " tokens");

        try {
            PrintWriter out = new PrintWriter (new FileWriter("data/log.txt"));
            // Simulation phase
            for(int i = 0; i < days; i++){
                System.out.println("Day " + i + " of the simulation----------------------------");
                out.println("Day " + i + " of the simulation----------------------------");
                System.out.println();
                out.println();
                
                System.out.println("Token amount: " + contract.getNumberofToken());
                // Setup wallet intentions
                LinkedList<Wallet> buyers = new LinkedList<Wallet>();
                LinkedList<Wallet> sellers = new LinkedList<Wallet>();

                for(Wallet wallet : contract.getWallets()){
                    if(random.nextDouble() < buyProbability){
                        buyers.add(wallet);
                        out.println("Wallet " + wallet.getName() + " wants to buy.");
                    }else if (random.nextDouble() < buyProbability + sellProbability) {
                        sellers.add(wallet);
                        out.println("Wallet " + wallet.getName() + " wants to sell.");
                    }else {
                        // Do nothing
                        out.println("Wallet " + wallet.getName() + " does nothing.");
                    }
                }

                // Randomly add new buyers

                boolean addBuyer = random.nextBoolean();
                
                if(addBuyer){
                    for(int j = 0; j < random.nextInt((int)(contract.getWallets().size()*percentageOfNewBuyers)); j++){
                        Wallet wallet = new Wallet(Integer.toString(contract.getWallets().size()+1 ), initialTokenAmount, initialMoneyAmount, contract);
                        contract.addWallet(wallet);
                        buyers.add(wallet);
                        out.println("Wallet " + wallet.getName() + " wants to buy.");
                    }
                }

                // Setup the exchanges

                Exchange[] exchanges = new Exchange[numberOfExchanges];
                for(int j = 0; j < numberOfExchanges; j++){
                    exchanges[j] = new Exchange(Integer.toString(j),0.0, 0.0, 1.0);
                }
                
                for(Wallet wallet: contract.getWallets()){
                    int temp = random.nextInt(numberOfExchanges*2);
                    if(temp == 0){
                        exchanges[0].addBuyerWallet(wallet);
                    }else if(temp == 1){
                        exchanges[1].addBuyerWallet(wallet);
                    }else if(temp == 2){
                        exchanges[2].addBuyerWallet(wallet);
                    }else if(temp == 3){
                        exchanges[0].addSellerWallet(wallet);
                    }else if(temp == 4){
                        exchanges[1].addSellerWallet(wallet);
                    }else{
                        exchanges[2].addSellerWallet(wallet);
                    }
                }

                for(Exchange exchange : exchanges){
                    double supply = 0.0;
                    double demand = 0.0;

                    for(Wallet wallet : exchange.getSellerWallets()){
                        supply += wallet.getToken();
                    }

                    for(Wallet wallet : exchange.getBuyerWallets()){
                        demand += wallet.getToken();
                    }
                                        
                    exchange.setSupply(supply);
                    exchange.setDemand(demand);
                }
                
                // Buy and sell phase

                for(Exchange exchange : exchanges){
                    //randomly select a buyer and a seller from the exchange, whitout repetition
                    System.out.println("Exchange: " + exchange.getName());
                    while(exchange.getBuyerWallets().size() > 1 && exchange.getSellerWallets().size() > 1){
                        Wallet buyer = exchange.getBuyerWallet();
                        Wallet seller = exchange.getSellerWallet();
    
                        double tokenAmount = seller.getToken()*random.nextDouble();    
                        double transationPrice = Math.abs(random.nextGaussian() + 1);
                        
                        System.out.println("Buyer: " + buyer.getName() + " Seller: " + seller.getName() + " Token amount: " + tokenAmount + " Transaction price: " + transationPrice);
                        buyer.buy(seller, tokenAmount, exchange, transationPrice);
                    }
                }
                double endOfDayValue = 0.0;
                // Rebase phase
                for(Exchange exchange : exchanges){
                    endOfDayValue += exchange.getPrice();
                    exchange.resetWallets();
                }
                endOfDayValue = endOfDayValue/numberOfExchanges;
                out.println("End of day " + i + " token price: " + endOfDayValue);
                System.out.println("End of day " + i + " token price: " + endOfDayValue);

                contract.setValue(endOfDayValue);
                prices[i] = endOfDayValue;
                contract.rebase(out);
                tokenAmounts[i] = contract.getNumberofToken();
            }
            out.close();
            System.out.println("Simulation finished");
            System.out.println("Prices, Tokens");
            System.out.println("------------");
            System.out.println(1.0 +", " + initialTokenAmount*numberOfInitialWallets);
            for(int i = 0; i < days; i++){
                System.out.println(prices[i] + ", " + tokenAmounts[i]);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getSeed(){
        return seed;
    }

}
