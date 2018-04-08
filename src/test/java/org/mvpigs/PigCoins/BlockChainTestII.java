package org.mvpigs.PigCoins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class BlockChainTestII {

    @Test
    public void constructorTest() {
        BlockChain bc = new BlockChain();
        assertNotNull(bc);
    }

    @Test
    public void addOriginTransaction() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();

        BlockChain bc = new BlockChain();
        Transaction trx = new Transaction();
        trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "pig things!");
        bc.addOrigin(trx);
        assertEquals(20, bc.getBlockChain().get(0).getPigCoins(), 0);
    }

    @Test
    public void load_wallet_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "spam eggs");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "spam eggs spam");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_3", "hash_1", wallet_1.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_4", "CA_hash_3", wallet_1.getAddress(), wallet_1.getAddress(), 10, "spam spam spam");
        bChain.addOrigin(transaction);

        double[] pigcoins = bChain.loadWallet(wallet_1.getAddress());
        assertEquals(30, pigcoins[0], 0);
        assertEquals(20, pigcoins[1], 0);
    }

    @Test
    public void load_Input_Transactions_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();
        Wallet wallet_3 = new Wallet();
        wallet_3.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(transaction);

        List<Transaction> inputTransactions = bChain.loadInputTransactions(wallet_1.getAddress());
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 1);
        assertFalse(inputTransactions.contains(transaction));

        inputTransactions = bChain.loadInputTransactions(wallet_2.getAddress());
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 1);
        assertTrue(inputTransactions.contains(transaction));

        inputTransactions = bChain.loadInputTransactions(wallet_3.getAddress());
        assertNotNull(inputTransactions);
        assertTrue(inputTransactions.size() == 0);
        assertFalse(inputTransactions.contains(transaction));
    }

    @Test
    public void is_consumed_coin_valid_test() {

        Wallet origin = new Wallet();
        origin.generateKeyPair();
        Wallet wallet_1 = new Wallet();
        wallet_1.generateKeyPair();
        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();

        BlockChain bChain = new BlockChain();
        Transaction transaction = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "spam eggs");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "spam spam spam");
        bChain.addOrigin(transaction);
        transaction = new Transaction("hash_3", "hash_1", wallet_1.getAddress(), wallet_2.getAddress(), 10, "pig things!");
        bChain.addOrigin(transaction);

        Map<String, Double> consumedCoins = new LinkedHashMap<>();
        consumedCoins.put("hash_1", 10d);
        assertFalse(bChain.isConsumedCoinValid(consumedCoins));
        consumedCoins.clear();
        consumedCoins.put("hash_2", 10d);
        consumedCoins.put("hash_3", 10d);
        assertTrue(bChain.isConsumedCoinValid(consumedCoins));
    }

    @Test
    public void create_transaction_test() {

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

        Map<String, Double> consumedCoins = new LinkedHashMap<>();
        consumedCoins.put("hash_1", 10.2d);
        consumedCoins.put("CA_hash_2", 9.8d);
        assertTrue(bChain.isConsumedCoinValid(consumedCoins));


        int previousBlockChainSize = bChain.getBlockChain().size();
        bChain.createTransaction(wallet_1.getAddress(), wallet_2.getAddress(), consumedCoins, "pig things!", wallet_1.signTransaction("pig things!"));
        assertEquals(previousBlockChainSize + consumedCoins.size(), bChain.getBlockChain().size(), 0);
        assertEquals("hash_4", bChain.getBlockChain().get(3).getHash());
        assertEquals(9.8, bChain.getBlockChain().get(3).getPigCoins(), 0);
        bChain.summarize(3);
    }
}
