package org.mvpigs.PigCoins;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;


public class BlockChain {

    private ArrayList<Transaction> blockChain;

    BlockChain(){
        this.blockChain=new ArrayList<>();
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
    public void processTransactions(PublicKey pKey_sender, PublicKey pKey_recipient, Map<String,Double> consumedCoins,
                                    String message,byte[] signedTransaction){


        if(isConsumedCoinValid(consumedCoins) && isSignatureValid(pKey_sender, message, signedTransaction)){

            createTransaction(pKey_sender, pKey_recipient, consumedCoins, message, signedTransaction);

        }
    }

    private void createTransaction(PublicKey pKey_sender, PublicKey pKey_recipient, Map<String, Double> consumedCoins,
                                   String message, byte[] signedTransaction) {

        for ( Map.Entry consumedCoin :consumedCoins.entrySet()){

            String hashName="hash_"+String.valueOf(this.getBlockChain().size()+1);

            if (consumedCoin.getKey().toString().substring(0,2).equals("CA")){

                pKey_recipient=pKey_sender;

            }

            Transaction transaction=new Transaction(hashName,consumedCoin.getKey().toString(),pKey_sender,pKey_recipient,
                                        Double.valueOf(consumedCoin.getValue().toString()),message);
            transaction.setSignature(signedTransaction);

            addOrigin(transaction);
        }


    }

    boolean isSignatureValid(PublicKey pKey_sender, String message, byte[] signedTransaction) {
    return GenSig.verify(pKey_sender,message,signedTransaction);}




     boolean isConsumedCoinValid(Map<String, Double> consumedCoins) {

        for (Transaction transaction :this.getBlockChain()) {
            for (Map.Entry consumedCoin : consumedCoins.entrySet()) {

                if (transaction.getPrev_hash().equals(consumedCoin.getKey().toString())) {


                    return false;
                }


            }
        }
        return true;}


    public ArrayList<Transaction> loadInputTransactions(PublicKey address){
        ArrayList<Transaction> inputTransactions=new ArrayList<>();
        for (Transaction transaction :this.getBlockChain()){

            if (transaction.getPkey_recipient().hashCode()==address.hashCode()){

                inputTransactions.add(transaction);
            }

        }
    return inputTransactions;
    }


    public ArrayList<Transaction> loadOutputTransactions(PublicKey address){
        ArrayList<Transaction> outputTransactions=new ArrayList<>();

        for (Transaction transaction :this.getBlockChain()) {

            if (transaction.getPkey_sender().hashCode() == address.hashCode()) {

                outputTransactions.add(transaction);
            }
        }
        return outputTransactions;
    }

    public double[] loadWallet(PublicKey address){
        double[] inputsAndOutputs=new double[2];
        double totalInput=0d;
        double totalOutput=0d;
        for (Transaction transaction :this.getBlockChain()){


            if (transaction.getPkey_recipient().hashCode()==address.hashCode()){

                totalInput+=transaction.getPigcoins();
            }
            if (transaction.getPkey_sender().hashCode()==address.hashCode()){

                totalOutput+=transaction.getPigcoins();
            }

        }
        inputsAndOutputs[0]=totalInput;
        inputsAndOutputs[1]=totalOutput;
        return  inputsAndOutputs;


    }


}
