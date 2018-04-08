package org.mvpigs.PigCoins;



import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {



    private PrivateKey SKey=null;
    private PublicKey address=null;
    private double totalInput =0d;
    private double totalOutput =0d;
    private double balance=0d;
    private ArrayList<Transaction> inputTransactions=null;
    private ArrayList<Transaction> outputTransactions=null;

    Wallet(){

    }

    public void setSK(PrivateKey SK) {
        this.SKey = SK;
    }

    public void setAddress(PublicKey aPublic) {
        this.address = aPublic;
    }

    public PublicKey getAddress() {
        return address;
    }

     void generateKeyPair(){

        KeyPair pair = GenSig.generateKeyPair();
        this.setSK(pair.getPrivate());
        this.setAddress(pair.getPublic());
    }

    public double getTotalInput() {
        return totalInput;
    }

    public double getTotalOutput() {
        return totalOutput;
    }

    public double getBalance() {

        return balance;
    }

    private void setBalance(){

        this.balance=this.getTotalInput()-this.getTotalOutput();
    }

    @Override
    public String toString(){
        return "\n Wallet = "+ this.getAddress().hashCode()+
                "\n Total input = "+String.valueOf(this.getTotalInput())+
                "\n Total output = "+String.valueOf(this.getTotalOutput())+
                "\n Balance = "+String.valueOf(this.getBalance())+"\n";}

    private void setTotalInput(double totalInput) {
        this.totalInput = totalInput;
    }

    private void setTotalOutput(double totalOutput) {
        this.totalOutput =totalOutput;
    }

    public void loadCoins(BlockChain bChain) {
        this.setTotalInput(bChain.loadWallet(this.getAddress())[0]);
        this.setTotalOutput(bChain.loadWallet(this.getAddress())[1]);
        this.setBalance();
    }

    public void loadInputTransactions(BlockChain bChain) {
        this.inputTransactions=bChain.loadInputTransactions(this.getAddress());
    }

    public ArrayList<Transaction> getInputTransactions() {

        return this.inputTransactions;
    }

    public void loadOutputTransactions(BlockChain bChain) {
       this.outputTransactions=bChain.loadOutputTransactions(this.getAddress());
    }



    public ArrayList<Transaction> getOutputTransactions() {

        return this.outputTransactions;
    }

    public Map<String,Double> collectCoins(Double pigcoins) {
        if (this.outputTransactions==null){
            this.outputTransactions= new ArrayList<>();
        }
        if (this.inputTransactions==null){

            this.inputTransactions= new ArrayList<>();
        }

        ArrayList<Transaction>  operativeInputs=new ArrayList<>();
        ArrayList<Transaction>  nonOperativeInputs=new ArrayList<>();
        Map<String,Double> coinsConsumedInTransaction=new HashMap<>();

        if(this.getOutputTransactions().size() > 0){
        for (Transaction outputTransactions:this.getOutputTransactions()){

            for (Transaction inputTransaction: this.getInputTransactions()){


                if (outputTransactions.getPrev_hash().equals(inputTransaction.getHash())){

                    nonOperativeInputs.add(inputTransaction);

                    if (operativeInputs.contains(inputTransaction)){

                        operativeInputs.remove(inputTransaction);
                    }
                }

                if (!nonOperativeInputs.contains(inputTransaction) && !operativeInputs.contains(inputTransaction)){

                    operativeInputs.add(inputTransaction);
                }

            }
        }
        } else {
            operativeInputs = getInputTransactions();
        }

        for (Transaction transaction : operativeInputs){


            if (transaction.getPigCoins() == pigcoins){

                coinsConsumedInTransaction.put(transaction.getHash(),transaction.getPigCoins());
                return coinsConsumedInTransaction;

            }
            else{
                if(pigcoins<transaction.getPigCoins()){

                    String hash2="CA_"+transaction.getHash();
                    coinsConsumedInTransaction.put(transaction.getHash(),pigcoins);
                    coinsConsumedInTransaction.put(hash2,transaction.getPigCoins()-pigcoins);
                    return coinsConsumedInTransaction;

                }
                else{
                    if(pigcoins>transaction.getPigCoins()){


                        if (pigcoins<this.getBalance()){

                            pigcoins=pigcoins-transaction.getPigCoins();
                            coinsConsumedInTransaction.put(transaction.getHash(),transaction.getPigCoins());
                        }
                        else{
                            return null;
                        }
                    }
                }
            }
        }

        return coinsConsumedInTransaction;
    }

    public byte[] signTransaction(String message) {

        return GenSig.sign(this.SKey,message);
    }

    public void sendCoins(PublicKey address, Double pigcoins, String message, BlockChain bChain) {

        byte[] signedTransaction = this.signTransaction(message);
        Map<String, Double> collectedCoins = this.collectCoins(pigcoins);

        bChain.processTransactions(this.address,address,collectedCoins,message,signedTransaction);
    }

    public PrivateKey getSKey() {
        return this.SKey;
    }
}
