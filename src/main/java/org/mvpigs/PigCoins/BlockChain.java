package org.mvpigs.PigCoins;

import java.util.ArrayList;

public class BlockChain {

    private ArrayList<Transaction> blockChain;

    public BlockChain(){
        this.blockChain=new ArrayList<Transaction>();
    }

    public ArrayList<Transaction> getBlockChain() {
        return blockChain;
    }

    public void addOrigin(Transaction trx) {

        blockChain.add(trx);
    }

    public void summarize() {

        for (Transaction trx :this.getBlockChain()){

            System.out.println("\n"+trx.toString()+"\n");
        }
    }
}
