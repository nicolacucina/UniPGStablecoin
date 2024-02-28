package simulation;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;


public class Simulation {
    public static void main(String[] args) {
        // Simulation parameters
        long seed = Long.parseLong(args[0]);
        Random random = new Random(seed);

        int days = 30;
        int numberOfInitialWallets = 100;
        int initialTokenAmount, initialMoneyAmount; 
        initialTokenAmount = initialMoneyAmount = 100; // Assures that the initial price is 1
        double buyProbability = 0.4;
        double sellProbability = 0.4; 
        double percentageOfNewBuyers = 0.1;
        int numberOfExchanges = 3;
        //int numberOfTransactions = random.nextInt(100);
        

        // Setup phase, first minting of the tokens
        Contract contract = new Contract();
        for(int i = 0; i < numberOfInitialWallets; i++){
            Wallet wallet = new Wallet(Integer.toString(i), initialTokenAmount, initialMoneyAmount, contract);
            contract.addWallet(wallet);
        }

        try {
            PrintWriter out = new PrintWriter (new FileWriter("data/log.txt"));
            // Simulation phase
            for(int i = 0; i < days; i++){
                out.println("Day " + i + " of the simulation");
                
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
                exchanges[0] = new Exchange(0.0, 0.0, 1.0);
                exchanges[1] = new Exchange(0.0, 0.0, 1.0);
                exchanges[2] = new Exchange(0.0, 0.0, 1.0);

                for(Wallet wallet: contract.getWallets()){
                    int temp = random.nextInt(numberOfExchanges);
                    if(temp == 0){
                        exchanges[0].addWallet(wallet);
                    }else if(temp == 1){
                        exchanges[1].addWallet(wallet);
                    }else{
                        exchanges[2].addWallet(wallet);
                    }
                }

                for(Exchange exchange : exchanges){
                    double supply = 0.0;
                    double demand = 0.0;

                    for(int a = 0; a < exchange.getWallets().size(); a++){
                        if(sellers.contains(exchange.getWallets().get(a))){
                            supply += exchange.getWallets().get(a).getToken();
                        }
                        if(buyers.contains(exchange.getWallets().get(a))){
                            demand += exchange.getWallets().get(a).getToken();
                        }
                    }
                    exchange.setSupply(supply);
                    exchange.setDemand(demand);
                }
                
                // Buy and sell phase

                for(Exchange exchange : exchanges){
                    //randomly select a buyer and a seller from the exchange, whitout repetition
                    boolean buyerFound = false;
                    Wallet buyer = null;
                    Wallet seller = null;

                    while(!buyerFound){
                        buyer = exchange.getWallets().get(random.nextInt(exchange.getWallets().size()));
                        if(buyers.contains(buyer)){
                            buyerFound = true;
                            exchange.getWallets().remove(buyer);
                        }
                    }

                    boolean sellerFound = false;
                    while(!sellerFound){
                        seller = exchange.getWallets().get(random.nextInt(exchange.getWallets().size()));
                        if(sellers.contains(seller)){
                            sellerFound = true;
                            exchange.getWallets().remove(seller);
                        }
                    }

                    double tokenAmount = seller.getToken()*random.nextDouble();    
                    double transationPrice = random.nextGaussian() + 1;

                    buyer.buy(seller, tokenAmount, exchange, transationPrice);
                }
                
                double endOfDayValue = 0.0;
                // Rebase phase
                for(Exchange exchange : exchanges){
                    endOfDayValue += exchange.getPrice();
                    exchange.resetWallets();
                }
                endOfDayValue = endOfDayValue/numberOfExchanges;
                out.println("End of day" + i + " token price: " + endOfDayValue);
                contract.setValue(endOfDayValue);
                contract.rebase(out);
                }
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
