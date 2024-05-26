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
        ////////////////////////////////////////////////////////////////////////////////////
        // Simulation parameters    
        ////////////////////////////////////////////////////////////////////////////////////

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
            int exchangeDimension = Integer.parseInt(simulationProperties.getProperty("exchangeDimension"));
            double buyPrice = Double.parseDouble(simulationProperties.getProperty("buyPrice"));
            double sellPrice = Double.parseDouble(simulationProperties.getProperty("sellPrice"));
            double priceGap = Double.parseDouble(simulationProperties.getProperty("priceGap"));
            double w1 = Double.parseDouble(simulationProperties.getProperty("buyWeight1"));
            double w2 = Double.parseDouble(simulationProperties.getProperty("sellWeight2"));
            

            Random random = new Random(seed);

            double[] prices = new double[days];
            double[] tokenAmounts = new double[days];
            boolean[] newBuyers = new boolean[days];

            ////////////////////////////////////////////////////////////////////////////////////
            // Setup phase, first minting of the tokens
            ////////////////////////////////////////////////////////////////////////////////////

            Contract contract = Contract.getInstance();
            for(int i = 0; i < numberOfInitialWallets; i++){
                Wallet wallet = new Wallet(Integer.toString(i), initialTokenAmount, initialMoneyAmount, contract);
                contract.addWallet(wallet);
            }

            Exchange[] exchanges = new Exchange[numberOfExchanges];
            for(int j = 0; j < numberOfExchanges; j++){
                exchanges[j] = new Exchange(Integer.toString(j), initialMoneyAmount*exchangeDimension, initialTokenAmount*exchangeDimension, contract, 0.0, 0.0, buyPrice, sellPrice, priceGap, w1, w2);
                contract.addWallet(exchanges[j]);
            }

            out = new PrintWriter (new FileWriter("data/"+logname+".txt"));
            ////////////////////////////////////////////////////////////////////////////////////
            // Simulation phase
            ////////////////////////////////////////////////////////////////////////////////////

            for(int i = 0; i < days; i++){
                out.println("Day " + i + " of the simulation----------------------------");
                out.println();

                out.println("Token amount: " + contract.getNumberofToken());
                out.println();

                ////////////////////////////////////////////////////////////////////////////////////
                // Setup wallet intentions
                ////////////////////////////////////////////////////////////////////////////////////

                LinkedList<Wallet> buyers = new LinkedList<Wallet>();
                LinkedList<Wallet> sellers = new LinkedList<Wallet>();

                for(Wallet wallet : contract.getWallets()){
                    //check if wallet is Wallet or Exchange
                    if(wallet instanceof Exchange){
                        continue;
                    }
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

                ////////////////////////////////////////////////////////////////////////////////////
                // Randomly add new buyers that will purchase tokens from the users
                ////////////////////////////////////////////////////////////////////////////////////

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

                ////////////////////////////////////////////////////////////////////////////////////
                // Initialize the percentage of the wallets after all wallets have been created
                ////////////////////////////////////////////////////////////////////////////////////

                contract.initPercentages();

                ////////////////////////////////////////////////////////////////////////////////////
                // Setup the exchanges
                ////////////////////////////////////////////////////////////////////////////////////
                
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

                out.println("###########################");
                for(Exchange exchange : exchanges){
                    out.println("Exchange: " + exchange.getName());
                    out.println();
                    out.println("Buy price: " + exchange.getBuyPrice());
                    out.println("Sell price: " + exchange.getSellPrice());
                    out.println();
                    for(Wallet wallet : exchange.getBuyerWallets()){
                        out.println("Buyer: " + wallet.getName() + " has " + wallet.getToken() + " tokens");
                    }
                    for(Wallet wallet : exchange.getSellerWallets()){
                        out.println("Seller: " + wallet.getName() + " has " + wallet.getToken() + " tokens");
                    }
                    out.println();
                }

                for(Exchange exchange : exchanges){
                    double supply = 0.0;
                    double demand = 0.0;

                    for(Wallet wallet : exchange.getSellerWallets()){
                        supply += wallet.getToken();
                    }

                    demand = supply + random.nextGaussian();
                                        
                    exchange.setSupply(supply);
                    exchange.setDemand(demand);
                }
                
                ////////////////////////////////////////////////////////////////////////////////////
                // Buy and sell phase
                ////////////////////////////////////////////////////////////////////////////////////

                out.println("###########################");
                for(Exchange exchange : exchanges){
                    //randomly select a buyer and a seller from the exchange, whitout repetition
                    out.println("Exchange: " + exchange.getName());
                    out.println();
                    
                    while(exchange.getBuyerWallets().size() > 0 && exchange.getSellerWallets().size() > 0){
                        // transactions have to be randomized because of the buy and sell updates
                        double tokenAmount = exchange.getSupply() * random.nextDouble(); 
                        if(random.nextBoolean()){
                            Wallet buyer = exchange.getBuyerWallet();
                            out.println("Buyer: " + buyer.getName() + ", Tokens owned: " + buyer.getToken() + ", transaction amount: " + tokenAmount);
                            boolean result = contract.transfer(exchange, buyer, tokenAmount);
                            if(result){
                                exchange.buy(buyer, tokenAmount);
                            }
                            out.println("Buyer: " + buyer.getName() + " has " + buyer.getToken() + " tokens");
                            out.println("Exchange: " + exchange.getName() + " has " + exchange.getToken() + " tokens");
                            out.println();
                        }else{
                            Wallet seller = exchange.getSellerWallet();
                            out.println("Seller: " + seller.getName() + ", Tokens owned: " + seller.getToken() + ", transaction amount: " + tokenAmount);
                            boolean result = contract.transfer(seller, exchange, tokenAmount);
                            if(result){
                                exchange.sell(seller, tokenAmount);
                            }
                            out.println("Seller: " + seller.getName() + " has " + seller.getToken() + " tokens");
                            out.println("Exchange: " + exchange.getName() + " has " + exchange.getToken() + " tokens");
                            out.println();
                        }
                    }
                }
                
                out.println();
                out.println("Tokens after transactions: " + contract.getNumberofToken());

                ////////////////////////////////////////////////////////////////////////////////////
                // Rebase phase
                ////////////////////////////////////////////////////////////////////////////////////

                double endOfDayValue = 0.0;
                double endOfDayBuyPrice = 0.0;
                double endOfDaySellPrice = 0.0;
                
                for(Exchange exchange : exchanges){
                    endOfDayBuyPrice += exchange.getBuyPrice();
                    endOfDaySellPrice += exchange.getSellPrice();
                    exchange.resetWallets();
                }

                endOfDayBuyPrice = endOfDayBuyPrice/numberOfExchanges;
                out.println("End of day " + i + " medium buy price: " + endOfDayBuyPrice);

                endOfDaySellPrice = endOfDaySellPrice/numberOfExchanges;
                out.println("End of day " + i + " medium sell price: " + endOfDaySellPrice);

                endOfDayValue = (endOfDayBuyPrice + endOfDaySellPrice)/2;
                out.println("End of day " + i + " medium token price: " + endOfDayValue);
                out.println();

                contract.setValue(endOfDayValue);
                prices[i] = endOfDayValue;
                contract.rebase();
                tokenAmounts[i] = contract.getNumberofToken();
            }

            out.println();
            out.println("Simulation finished");
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
