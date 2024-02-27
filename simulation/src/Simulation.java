package simulation.src;
import simulation.src.*;
import java.util.Random;
import java.util.LinkedList;

public class Simulation {

    public static void main(String[] args) {
        int days = 50;
        int wallets = 100;
        long seed = 123456789;
        Random random = new Random(seed);

        // Setup phase
        Contract contract = new Contract();
        for(int i = 0; i < wallets; i++){
            Wallet wallet = new Wallet(100, 100);
            contract.addWallet(wallet);
        }

        // Simulation phase
        for(int i = 0; i < days; i++){
            // Buy and sell phase
            int numberOfTransactions = random.nextInt(100);
            for(int j = 0; j < numberOfTransactions; j++){
                
            }
            
            contract.rebase();
        }
    }
    
}
