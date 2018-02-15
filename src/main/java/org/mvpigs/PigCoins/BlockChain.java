package org.mvpigs.PigCoins;

import java.util.ArrayList;

public class BlockChain {

    private ArrayList<Transaction> blockChain;


    public void addOrigin(Transaction trx) {

        blockChain.add(trx);
    }
}
