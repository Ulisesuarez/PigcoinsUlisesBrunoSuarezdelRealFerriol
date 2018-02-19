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
            Transaction transaction=new Transaction(hashName,consumed.getKey().toString(),pKey_sender,pKey_recipient,Double.valueOf(consumed.getValue().toString()),message);
            transaction.setSignature(signedTransaction);

            addOrigin(transaction);
        }


    }

    protected boolean isSignatureValid(PublicKey pKey_sender, String message, byte[] signedTransaction) {
    return GenSig.verify(pKey_sender,message,signedTransaction);}


    protected boolean isConsumedCoinValid(Map<String, Double> consumedCoins,PublicKey pKey_sender) {
        for (Transaction trx :this.getBlockChain()){
            for (Map.Entry entry :consumedCoins.entrySet()){

                if(transaccionEnviadaAOtro(pKey_sender, trx, entry) ||transaccionNoEnWallet(pKey_sender, trx, entry)){


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
    } //carga en la wallet las transacciones que tienen como origen esa dirección o clave pública.

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


    } //carga en la wallet los pigcoins envidos y recibidos en esa dirección.


    private boolean transaccionEnviadaAOtro(PublicKey pKey_sender, Transaction trx, Map.Entry entry) {
        return entry.getKey().equals(trx.getPrev_hash()) && trx.getPkey_sender().hashCode()==pKey_sender.hashCode()&& trx.getPkey_recipient().hashCode()!=pKey_sender.hashCode();
    }

    private boolean transaccionNoEnWallet(PublicKey pKey_sender, Transaction trx, Map.Entry entry) {
        return entry.getKey().equals(trx.getHash())&& pKey_sender.hashCode()!=trx.getPkey_recipient().hashCode();
    }
}
