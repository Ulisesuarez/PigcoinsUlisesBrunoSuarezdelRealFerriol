package org.mvpigs.PigCoins;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;

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

    public void summarize(Integer position) {
        System.out.println("\n"+this.getBlockChain().get(position).toString()+"\n");
    }
    public void processTransactions(PublicKey pKey_sender, PublicKey pKey_recipient, Map<String,Double> consumedCoins, String message,byte[] signedTransaction){


        if(isConsumedCoinValid(consumedCoins) && isSignatureValid(pKey_sender, message, signedTransaction)){

            createTransaction(pKey_sender, pKey_recipient, consumedCoins, message, signedTransaction);

        }
    }

    private void createTransaction(PublicKey pKey_sender, PublicKey pKey_recipient, Map<String, Double> consumedCoins, String message, byte[] signedTransaction) {

        for (Map.Entry consumed :consumedCoins.entrySet()){

            Transaction trx=new Transaction("mock",consumed.getKey().toString(),pKey_sender,pKey_recipient,consumed.getValue(),message);
            addOrigin(trx);
        }


    }

    private boolean isSignatureValid(PublicKey pKey_sender, String message, byte[] signedTransaction) {
    return GenSig.verify(pKey_sender,message,signedTransaction);}

    private boolean isConsumedCoinValid(Map<String, Double> consumedCoins) {
        for (Transaction trx :this.getBlockChain()){
            for (Map.Entry entry :consumedCoins.entrySet()){

                if(entry.getKey().equals(trx.getHash())){

                    return false;
                }
            }
        }
    return true;}
}
