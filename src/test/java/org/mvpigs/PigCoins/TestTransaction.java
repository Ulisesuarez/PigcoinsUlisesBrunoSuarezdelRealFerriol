package org.mvpigs.PigCoins;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTransaction {
    private Wallet wallet_1=new Wallet();
    private Wallet wallet_2=new Wallet();
    private Transaction trx = new Transaction();


    @Test
public void testTransactions(){
        wallet_1.generateKeyPair();
        wallet_2.generateKeyPair();

        trx = new Transaction("hash_1", "0", wallet_1.getAddress(), wallet_2.getAddress(), 20, "a flying pig!");
    assertEquals("hash_1",trx.getHash());
    assertEquals("0",trx.getPrev_hash());
    assertEquals(wallet_1.getAddress(),trx.getPkey_sender());
    assertEquals(wallet_2.getAddress(),trx.getPkey_recipient());
    assertEquals(20,trx.getPigcoins(),0.001);
    assertEquals("a flying pig!",trx.getMessage());

    }
}