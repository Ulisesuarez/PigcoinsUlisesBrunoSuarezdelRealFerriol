package org.mvpigs.PigCoins;

import org.junit.Test;

import java.security.KeyPair;

import static org.junit.Assert.*;

public class WalletTest {

    @Test
    public void loadCoins() {
        Wallet wallet_1 = new Wallet();
        KeyPair pair = GenSig.generateKeyPair();
        wallet_1.setSK(pair.getPrivate());
        wallet_1.setAddress(pair.getPublic());

        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();
        Transaction trx = new Transaction();

        Wallet origin = new Wallet();
        origin.generateKeyPair();

        BlockChain bChain = new BlockChain();
        trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "bacon eggs");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "spam spam spam");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_3", "hash_1", wallet_1.getAddress(), wallet_2.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(trx);

        wallet_1.loadCoins(bChain);
        wallet_2.loadCoins(bChain);

        assertEquals(20,wallet_1.getTotal_input(),0.1);
        assertEquals(20,wallet_1.getTotal_output(),0.1);
        assertEquals(0,wallet_1.getBalance(),0.1);

        assertEquals(30,wallet_2.getTotal_input(),0.1);
        assertEquals(0,wallet_2.getTotal_output(),0.1);
        assertEquals(30,wallet_2.getBalance(),0.1);



    }

    @Test
    public void collectCoins() {
    }
}