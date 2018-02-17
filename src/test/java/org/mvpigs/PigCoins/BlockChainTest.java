package org.mvpigs.PigCoins;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class BlockChainTest {
    private Wallet wallet_1;
    private Wallet wallet_2;
    private Wallet origin;
    private Transaction trx;
    private BlockChain bChain;


    @Before
    public void setUp(){
        wallet_1 = new Wallet();
        wallet_1.generateKeyPair();

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

    }
    @Test
    public void testBlockChain(){

        assertEquals("hash_1",bChain.getBlockChain().get(0).getHash());
        assertEquals("hash_2",bChain.getBlockChain().get(1).getHash());
        assertEquals("hash_3",bChain.getBlockChain().get(2).getHash());
    }

    @Test
    public  void testSummarize(){
        bChain.summarize();
        bChain.summarize(2);
    }
    @Test
    public void testCreateTransaction(){




        wallet_1.loadCoins(bChain);
        wallet_1.loadInputTransactions(bChain);
        wallet_1.loadOutputTransactions(bChain);
        //System.out.println(wallet_1.toString());

        wallet_2.loadCoins(bChain);
        wallet_2.loadInputTransactions(bChain);
        wallet_2.loadOutputTransactions(bChain);

        Double pigcoins = 25d;
        Map<String, Double> consumedCoins = wallet_1.collectCoins(pigcoins);

        String message = "he roto la hucha :(";
        byte[] signedTransaction = wallet_1.signTransaction(message); // usa GenSig.sign()
        wallet_1.sendCoins(wallet_2.getAddress(), pigcoins, message, bChain);

    }

    @Test
    public void testisConsumedValid() {
        Map<String, Double> consumedCoins = new HashMap<String, Double>();
        consumedCoins.putIfAbsent("hash_1", 20d);


        assertEquals(false, bChain.isConsumedCoinValid(consumedCoins, wallet_1.getAddress()));
        assertEquals(false, bChain.isConsumedCoinValid(consumedCoins, wallet_2.getAddress()));
        consumedCoins = new HashMap<String, Double>();
        consumedCoins.putIfAbsent("hash_5", 2d);
        assertEquals(false, bChain.isConsumedCoinValid(consumedCoins, wallet_2.getAddress()));
        assertEquals(true, bChain.isConsumedCoinValid(consumedCoins, wallet_1.getAddress()));
        consumedCoins = new HashMap<String, Double>();
        consumedCoins.putIfAbsent("hash_2", 2d);
        assertEquals(false, bChain.isConsumedCoinValid(consumedCoins, wallet_1.getAddress()));
        assertEquals(true, bChain.isConsumedCoinValid(consumedCoins, wallet_2.getAddress()));


    }
    @Test
            public void testIsSignatureValid(){
        byte[] signedTransaction=wallet_1.signTransaction("bla");
        assertEquals(true,bChain.isSignatureValid(wallet_1.getAddress(),"bla",signedTransaction));
        assertEquals(false,bChain.isSignatureValid(wallet_1.getAddress(),"bsa",signedTransaction));

        }
}