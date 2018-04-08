package org.mvpigs.PigCoins;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;

import static org.junit.Assert.*;

public class WalletTest {

    private Wallet wallet_1;
    private Wallet wallet_2;
    private Wallet origin;
    private Transaction trx;
    private BlockChain bChain;
    private KeyPair pair = GenSig.generateKeyPair();
    @Before
    public void setUp(){
        wallet_1 = new Wallet();

        wallet_1.setSK(pair.getPrivate());
        wallet_1.setAddress(pair.getPublic());

        wallet_2 = new Wallet();
        wallet_2.generateKeyPair();


        origin = new Wallet();
        origin.generateKeyPair();

        bChain = new BlockChain();
        trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "bacon eggs");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "spam spam spam");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_3", "hash_1", wallet_1.getAddress(), wallet_2.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_4", "2", origin.getAddress(), wallet_1.getAddress(), 20, "sausages puagh!");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_5", "3", origin.getAddress(), wallet_1.getAddress(), 10, "baked beans are off!");
        bChain.addOrigin(trx);
        wallet_1.sendCoins(wallet_2.getAddress(), 25d, "flakachakala", bChain);

    }
    @Test
    public void testWallet(){

        //assertEquals(pair.getPrivate(),wallet_1.getSKey());
        assertEquals(pair.getPublic(),wallet_1.getAddress());
        System.out.println(wallet_1.toString());
    }
    @Test
    public void testGenerateKey(){
        assertEquals(sun.security.provider.DSAPublicKeyImpl.class,wallet_2.getAddress().getClass());
        //assertEquals(sun.security.provider.DSAPrivateKey.class,wallet_2..getClass());

    }


    @Test
    public void loadCoins() {


        wallet_1.loadCoins(bChain);
        wallet_2.loadCoins(bChain);

        assertEquals(50,wallet_1.getTotalInput(),0.1);
        assertEquals(20,wallet_1.getTotalOutput(),0.1);
        assertEquals(30,wallet_1.getBalance(),0.1);

        assertEquals(30,wallet_2.getTotalInput(),0.1);
        assertEquals(0,wallet_2.getTotalOutput(),0.1);
        assertEquals(30,wallet_2.getBalance(),0.1);



    }


    @Test
    public void getInputTransactions() {
        wallet_1.loadInputTransactions( bChain);
        System.out.println(wallet_1.getInputTransactions());
    }


    @Test
    public void testOutputTransactions() {
        wallet_1.loadOutputTransactions( bChain);
       System.out.println(wallet_1.getOutputTransactions());
    }

    @Test
    public void testCollectCoins() {
        wallet_1.loadCoins(bChain);
        wallet_1.loadInputTransactions(bChain);
        wallet_1.loadOutputTransactions(bChain);

        System.out.println(wallet_1.collectCoins(1d));
        System.out.println(wallet_1.collectCoins(30d));
        System.out.println(wallet_1.collectCoins(20d));

    }

    @Test
    public void testSendCoins(){
        wallet_1.sendCoins(wallet_2.getAddress(), 2.5d, "", bChain);

    }
}