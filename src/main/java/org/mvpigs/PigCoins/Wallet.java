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
    private double total_input=0d;
    private double total_output=0d;
    private double balance=0d;
    private ArrayList<Transaction> inputTransactions;
    private ArrayList<Transaction> outputTransactions;

    Wallet(){
        this.inputTransactions= new ArrayList<>();
        this.outputTransactions= new ArrayList<>();

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

    public double getTotal_input() {
        return total_input;
    }

    public double getTotal_output() {
        return total_output;
    }

    public double getBalance() {

        return balance;
    }

    private void setBalance(){

        this.balance=this.getTotal_input()-this.getTotal_output();
    }

    @Override
    public String toString(){
        return "\n Wallet = "+ this.getAddress().hashCode()+
                "\n Total input = "+String.valueOf(this.getTotal_input())+
                "\n Total output = "+String.valueOf(this.getTotal_output())+
                "\n Balance = "+String.valueOf(this.getBalance())+"\n";}

    private void setTotal_input(double total_input) {
        this.total_input = total_input;
    }

    private void setTotal_output(double total_output) {
        this.total_output =total_output;
    }

    public void loadCoins(BlockChain bChain) {
        this.setTotal_input(bChain.loadWallet(this.getAddress())[0]);
        this.setTotal_output(bChain.loadWallet(this.getAddress())[1]);
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

        ArrayList<Transaction>  operativeInputs=new ArrayList<>();
        ArrayList<Transaction>  nonOperativeInputs=new ArrayList<>();
        Map<String,Double> coinsConsumedInTransaction=new HashMap<>();

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

        for (Transaction transaction : operativeInputs){


            if (transaction.getPigcoins() == pigcoins){

                coinsConsumedInTransaction.put(transaction.getHash(),transaction.getPigcoins());
                return coinsConsumedInTransaction;

            }
            else{
                if(pigcoins<transaction.getPigcoins()){

                    String hash2="CA"+transaction.getHash();
                    coinsConsumedInTransaction.put(transaction.getHash(),pigcoins);
                    coinsConsumedInTransaction.put(hash2,transaction.getPigcoins()-pigcoins);
                    return coinsConsumedInTransaction;

                }
                else{
                    if(pigcoins>transaction.getPigcoins()){


                        if (pigcoins<this.getBalance()){

                            pigcoins=pigcoins-transaction.getPigcoins();
                            coinsConsumedInTransaction.put(transaction.getHash(),transaction.getPigcoins());
                        }
                        else{
                            coinsConsumedInTransaction=new HashMap<>();
                            return coinsConsumedInTransaction;
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
}
