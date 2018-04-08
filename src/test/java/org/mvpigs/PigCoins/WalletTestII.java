package org.mvpigs.PigCoins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class WalletTestII {

    @Test
    public void constructor_test() {
        Wallet wallet = new Wallet();
        assertNotNull(wallet);
    }

    @Test
    public void generate_key_pair_test() {
        Wallet wallet = new Wallet();
        assertNotNull(wallet);
        wallet.generateKeyPair();
        assertNotNull(wallet.getAddress());
        assertNotNull(wallet.getSKey());
    }

    @Test
    public void generateSk_test() {
        // problema al testear Gava desde Junit => hacerlo desde main App
        // wallet.generateSk("gelpiorama@gmail.com");
    }

    @Test
    public void total_pigcoins_input_y_output_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(trx);

        wallet_1.loadCoins(bChain);
        assertEquals(20, wallet_1.getTotalInput(), 0);
        assertEquals(0, wallet_1.getTotalOutput(), 0);
        assertEquals(20, wallet_1.getBalance(), 0);

        wallet_2.loadCoins(bChain);
        assertEquals(10, wallet_2.getTotalInput(), 0);
        assertEquals(0, wallet_2.getTotalOutput(), 0);
        assertEquals(10, wallet_2.getBalance(), 0);

        // una wallet que no existe
        Wallet wallet_3 = new Wallet();
        wallet_3.generateKeyPair();
        wallet_3.loadCoins(bChain);
        assertEquals(0, wallet_3.getTotalInput(), 0);
        assertEquals(0, wallet_3.getTotalOutput(), 0);
        assertEquals(0, wallet_3.getBalance(), 0);
    }

    @Test
    public void load_input_transactions_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();
        Wallet wallet_3 = new Wallet();
        wallet_3.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(trx);

        wallet_1.loadInputTransactions(bChain);
        assertTrue(wallet_1.getInputTransactions().size() == 1);
        assertTrue(wallet_1.getInputTransactions().get(0).getPigCoins() == 20);

        wallet_2.loadInputTransactions(bChain);
        assertTrue(wallet_2.getInputTransactions().size() == 1);
        assertTrue(wallet_2.getInputTransactions().get(0).getPigCoins() == 10);

        wallet_3.loadInputTransactions(bChain);
        assertTrue(wallet_3.getInputTransactions().size() == 0);
    }

    @Test
    public void collect_coins_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet = new Wallet();
        wallet.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", origin.getAddress(), wallet.getAddress(), 20, "a flying pig!");
        assertTrue(transaction.getHash().equals("hash_1"));
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", origin.getAddress(), wallet.getAddress(), 10, "pig things!");
        assertTrue(transaction.getHash().equals("hash_2"));
        bChain.addOrigin(transaction);

        wallet.loadInputTransactions(bChain);
        assertTrue(wallet.getInputTransactions().size() == 2);
        assertTrue(wallet.getInputTransactions().get(0).getPigCoins() == 20);
        assertTrue(wallet.getInputTransactions().get(1).getPigCoins() == 10);

        wallet.loadCoins(bChain);
        assertEquals(30, wallet.getTotalInput(), 0);
        assertEquals(0, wallet.getTotalOutput(), 0);
        assertEquals(30, wallet.getBalance(), 0);

        // la cantidad a enviar es exactamente la primera transaccion entrante
        Double pigcoins = 20d;
        assertNull(wallet.getOutputTransactions());
        Map<String, Double> coins = wallet.collectCoins(pigcoins);
        assertNotNull(coins);
        assertEquals(coins.size(), 1);
        assertEquals(20, coins.get("hash_1"), 0);

        // la cantidad a enviar es menor que la primera transaccion entrante

        wallet.loadInputTransactions(bChain);
        assertEquals(2, wallet.getInputTransactions().size());
        pigcoins = 10.2d;
        coins = wallet.collectCoins(pigcoins);
        assertNotNull(coins);
        assertEquals(2, coins.size());
        assertEquals(10.2, coins.get("hash_1"), 0);
        assertEquals(9.8, coins.get("CA_hash_1"), 0);

        // la cantidad a enviar es mayor que la primera transaccion entrante

        wallet.loadInputTransactions(bChain);
        pigcoins = 25d;
        coins = wallet.collectCoins(pigcoins);
        assertNotNull(coins);
        assertTrue(coins.size() == 3);
        assertEquals(20, coins.get("hash_1"), 0);
        assertEquals(5, coins.get("hash_2"), 0);
        assertEquals(5, coins.get("CA_hash_2"), 0);

        // la cantidad a enviar es mayor que el balance = pigcoins disponibles en la wallet
        wallet.loadInputTransactions(bChain);
        pigcoins = 40d;
        coins = wallet.collectCoins(pigcoins);
        assertNull(coins);

    }

    @Test
    public void signTransaction() {

        Wallet wallet = new Wallet();
        wallet.generateKeyPair();
        byte[] signedMessage = wallet.signTransaction("pig things!");

        BlockChain bChain = new BlockChain();
        assertTrue(bChain.isSignatureValid(wallet.getAddress(), "pig things!", signedMessage));
    }

    @Test
    public void send_transaction_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(transaction);

        wallet_1.loadCoins(bChain);
        assertEquals(20, wallet_1.getTotalInput(), 0);
        assertEquals(0, wallet_1.getTotalOutput(), 0);
        assertEquals(20, wallet_1.getBalance(), 0);
        wallet_1.loadInputTransactions(bChain);
        assertTrue(wallet_1.getInputTransactions().size() == 1);

        wallet_2.loadCoins(bChain);
        assertEquals(10, wallet_2.getTotalInput(), 0);
        assertEquals(0, wallet_2.getTotalOutput(), 0);
        assertEquals(10, wallet_2.getBalance(), 0);
        wallet_2.loadInputTransactions(bChain);
        assertTrue(wallet_2.getInputTransactions().size() == 1);

        // enviamos una transaccion que supone un change address

        wallet_1.sendCoins(wallet_2.getAddress(), 10.2d, "pig things!", bChain);
        assertEquals(4, bChain.getBlockChain().size(), 0);

        wallet_1.loadInputTransactions(bChain);
        assertEquals(2, wallet_1.getInputTransactions().size());
        assertEquals(20d, wallet_1.getInputTransactions().get(0).getPigCoins(), 0);
        assertEquals(9.8d, wallet_1.getInputTransactions().get(1).getPigCoins(), 0);

        wallet_1.loadOutputTransactions(bChain);
        assertEquals(2, wallet_1.getOutputTransactions().size(), 0);
        assertEquals(10.2d, wallet_1.getOutputTransactions().get(0).getPigCoins(), 0);
        assertEquals(9.8d, wallet_1.getOutputTransactions().get(1).getPigCoins(), 0);
        wallet_1.loadCoins(bChain);
        assertEquals(9.8, wallet_1.getBalance(), 0);
        wallet_2.loadCoins(bChain);
        assertEquals(20.2, wallet_2.getBalance(), 0);
    }
}