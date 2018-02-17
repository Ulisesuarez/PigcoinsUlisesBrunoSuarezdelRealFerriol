package org.mvpigs.PigCoins;

import jdk.internal.util.xml.impl.Pair;

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


        if(isConsumedCoinValid(consumedCoins,pKey_sender) && isSignatureValid(pKey_sender, message, signedTransaction)){

            createTransaction(pKey_sender, pKey_recipient, consumedCoins, message, signedTransaction);

        }







    }

    private void createTransaction(PublicKey pKey_sender, PublicKey pKey_recipient, Map<String, Double> consumedCoins, String message, byte[] signedTransaction) {

        for ( Map.Entry consumed :consumedCoins.entrySet()){




            String hashName="hash_"+String.valueOf(this.getBlockChain().size()+1);
            if (consumed.getKey().toString().substring(0,2).equals("CA")){
                pKey_recipient=pKey_sender;

            }
            Transaction trx=new Transaction(hashName,consumed.getKey().toString(),pKey_sender,pKey_recipient,Double.valueOf(consumed.getValue().toString()),message);
            trx.setSignature(signedTransaction);

            addOrigin(trx);
        }


    }

    protected boolean isSignatureValid(PublicKey pKey_sender, String message, byte[] signedTransaction) {
    return GenSig.verify(pKey_sender,message,signedTransaction);}


    protected boolean isConsumedCoinValid(Map<String, Double> consumedCoins,PublicKey pKey_sender) {
        for (Transaction trx :this.getBlockChain()){
            for (Map.Entry entry :consumedCoins.entrySet()){

                if(transaccionEnviadaAOtro(pKey_sender, trx, entry) ||transaccionNoEnCartera(pKey_sender, trx, entry)){


                    return false;
                }


            }
        }
    return true;}

    private boolean transaccionEnviadaAOtro(PublicKey pKey_sender, Transaction trx, Map.Entry entry) {
        return entry.getKey().equals(trx.getPrev_hash()) && trx.getPkey_sender().hashCode()==pKey_sender.hashCode()&& trx.getPkey_recipient().hashCode()!=pKey_sender.hashCode();
    }

    private boolean transaccionNoEnCartera(PublicKey pKey_sender, Transaction trx, Map.Entry entry) {
        return entry.getKey().equals(trx.getHash())&& pKey_sender.hashCode()!=trx.getPkey_recipient().hashCode();
    }
}
