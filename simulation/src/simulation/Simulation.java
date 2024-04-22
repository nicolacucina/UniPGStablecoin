package simulation;

import java.util.Random;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Properties;


public class Simulation {

    public static long seed;
    public static PrintWriter out;
    public static void main(String[] args) {
        // Simulation parameters    

        Properties simulationProperties = new Properties();
        try {
            simulationProperties.load(new FileInputStream("data/simulation.properties"));
            String logname = simulationProperties.getProperty("logName");
            long seed = Long.parseLong(simulationProperties.getProperty("seed"));
            int days = Integer.parseInt(simulationProperties.getProperty("days"));
            int numberOfInitialWallets = Integer.parseInt(simulationProperties.getProperty("numberOfInitialWallets"));
            int initialTokenAmount, initialMoneyAmount;
            initialTokenAmount = initialMoneyAmount = Integer.parseInt(simulationProperties.getProperty("initialTokenAmount"));
            double maxMoneyAmount = Double.parseDouble(simulationProperties.getProperty("maxMoneyAmount"));
            double buyProbability = Double.parseDouble(simulationProperties.getProperty("buyProbability"));
            double sellProbability = Double.parseDouble(simulationProperties.getProperty("sellProbability"));
            double percentageOfNewBuyers = Double.parseDouble(simulationProperties.getProperty("percentageOfNewBuyers"));
            int numberOfExchanges = Integer.parseInt(simulationProperties.getProperty("numberOfExchanges"));
            double w1 = Double.parseDouble(simulationProperties.getProperty("ExchangeWeight1"));
            double w2 = Double.parseDouble(simulationProperties.getProperty("ExchangeWeight2"));

            Random random = new Random(seed);

            double[] prices = new double[days];
            double[] tokenAmounts = new double[days];
            boolean[] newBuyers = new boolean[days];

            // Setup phase, first minting of the tokens
            Contract contract = new Contract();
            for(int i = 0; i < numberOfInitialWallets; i++){
                Wallet wallet = new Wallet(Integer.toString(i), initialTokenAmount, initialMoneyAmount, contract);
                contract.addWallet(wallet);
            }

            Exchange[] exchanges = new Exchange[numberOfExchanges];
            for(int j = 0; j < numberOfExchanges; j++){
                exchanges[j] = new Exchange(Integer.toString(j),0.0, 0.0, 1.0, w1, w2);
            }

            out = new PrintWriter (new FileWriter("data/"+logname+".txt"));
                // Simulation phase
            for(int i = 0; i < days; i++){
                out.println("Day " + i + " of the simulation----------------------------");
                out.println();

                out.println("Token amount: " + contract.getNumberofToken());
                out.println();

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
                out.println();

                // Randomly add new buyers that will purchase tokens from the users

                boolean addBuyer = random.nextBoolean();
                newBuyers[i] = addBuyer;
                if(addBuyer){
                    out.println("New buyers");
                    for(int j = 0; j < random.nextInt((int)(contract.getWallets().size()*percentageOfNewBuyers)); j++){
                        Wallet wallet = new Wallet(Integer.toString(contract.getWallets().size()+1), 0, random.nextDouble()*maxMoneyAmount, contract);
                        contract.addWallet(wallet);
                        buyers.add(wallet);
                        out.println("New Wallet " + wallet.getName() + " wants to buy.");
                    }
                    out.println();
                }

                // Initialize the percentage of the wallets after all wallets have been created
                contract.initPercentages();

                // Setup the exchanges
                
                for(Wallet wallet: contract.getWallets()){
                    int temp = random.nextInt(numberOfExchanges);
                    if(buyers.contains(wallet)){
                        if(temp == 0){
                            exchanges[0].addBuyerWallet(wallet);
                        }else if(temp == 1){
                            exchanges[1].addBuyerWallet(wallet);
                        }else{
                            exchanges[2].addBuyerWallet(wallet);
                        }
                    }else if(sellers.contains(wallet)){
                        if(temp == 0){
                            exchanges[0].addSellerWallet(wallet);
                        }else if(temp == 1){
                            exchanges[1].addSellerWallet(wallet);
                        }else{
                            exchanges[2].addSellerWallet(wallet);
                        }
                    }
                }

                for(Exchange exchange : exchanges){
                    out.println("Exchange: " + exchange.getName());
                    for(Wallet wallet : exchange.getBuyerWallets()){
                        out.println("Buyer: " + wallet.getName());
                    }
                    for(Wallet wallet : exchange.getSellerWallets()){
                        out.println("Seller: " + wallet.getName());
                    }
                    out.println();
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
                    out.println("Exchange: " + exchange.getName());
                    out.println();
                    
                    while(exchange.getBuyerWallets().size() > 0 && exchange.getSellerWallets().size() > 0){
                        Wallet buyer = exchange.getBuyerWallet();
                        Wallet seller = exchange.getSellerWallet();

                        double tokenAmount = seller.getToken()*random.nextDouble();    
                        double transationPrice = Math.abs(random.nextGaussian() + 1);
                        
                        out.println("Buyer: " + buyer.getName() + " Seller: " + seller.getName() + " Token amount: " + tokenAmount + " Transaction price: " + transationPrice);
                        buyer.buy(seller, tokenAmount, exchange, transationPrice);
                        out.println();

                    }
                }
                
                out.println();
                out.println("Tokens after transactions: " + contract.getNumberofToken());
                
                double endOfDayValue = 0.0;
                // Rebase phase
                for(Exchange exchange : exchanges){
                    endOfDayValue += exchange.getPrice();
                    exchange.resetWallets();
                }
                endOfDayValue = endOfDayValue/numberOfExchanges;
                out.println("End of day " + i + " token price: " + endOfDayValue);
                out.println();

                contract.setValue(endOfDayValue);
                prices[i] = endOfDayValue;
                contract.rebase();
                tokenAmounts[i] = contract.getNumberofToken();
            }

            out.println();
            out.println("Simulation finished");
            out.println("Sta lista ha qualcosa che non va perchè le singole giornate invece funzionano");
            out.println();
            out.println("Prices, Tokens, New buyers");
            out.println("-----------------------------------------------------");
            out.println(1.0 +", " + initialTokenAmount*numberOfInitialWallets + ", " + false);
            for(int i = 0; i < days; i++){
                out.println(prices[i] + ", " + tokenAmounts[i] + ", " + newBuyers[i]);  
            }
            out.close();

            PrintWriter csvWriter = new PrintWriter(new FileWriter("data/"+logname+".csv"));
            csvWriter.println("Day,Price,Token");
            for(int i=0; i < days; i++){
                csvWriter.println(i + "," + prices[i] + "," + tokenAmounts[i]);
            }
            csvWriter.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getSeed(){
        return seed;
    }

    public static PrintWriter getWriter(){
        return out;
    }
}
