package org.mvpigs.PigCoins;

import com.sun.xml.internal.fastinfoset.util.CharArray;

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

    public Wallet(){
        this.inputTransactions= new ArrayList<Transaction>();
        this.outputTransactions= new ArrayList<Transaction>();

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

    public PrivateKey getSKey() {
        return SKey;
    }

    public void generateKeyPair(){

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

    public void setBalance(){

        this.balance=this.getTotal_input()-this.getTotal_output();
    }

    @Override
    public String toString(){
        String Cartera="\n Wallet = "+this.getAddress().hashCode()+
                "\n Total input = "+String.valueOf(this.getTotal_input())+
                "\n Total output = "+String.valueOf(this.getTotal_output())+
                "\n Balance = "+String.valueOf(this.getBalance())+"\n";
    return Cartera;}

    public void setTotal_input(double total_input) {
        this.total_input = total_input;
    }

    public void setTotal_output(double total_output) {
        this.total_output =total_output;
    }

    public void addTotal_input(double total_input) {
        this.total_input = this.total_input+total_input;
    }

    public void addTotal_output(double total_output) {
        this.total_output =this.total_output+total_output;
    }

    public void loadCoins(BlockChain bChain) {
        this.setTotal_input(0);
        this.setTotal_output(0);
        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_recipient().hashCode()==this.getAddress().hashCode()){

                this.addTotal_input(trx.getPigcoins());
            }
            if (trx.getPkey_sender().hashCode()==this.getAddress().hashCode()){

                this.addTotal_output(trx.getPigcoins());
            }

        }
        this.setBalance();
    }

    public void loadInputTransactions(BlockChain bChain) {
        this.inputTransactions=new ArrayList<Transaction>();
        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_recipient().hashCode()==this.getAddress().hashCode()){

                this.inputTransactions.add(trx);
            }

        }
    }

    public ArrayList<Transaction> getInputTransactions() {



    return this.inputTransactions;}

    public void loadOutputTransactions(BlockChain bChain) {
        this.outputTransactions=new ArrayList<Transaction>();
        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_sender().hashCode()==this.getAddress().hashCode()){

                this.outputTransactions.add(trx);
            }

        }

    }

    public ArrayList<Transaction> getOutputTransactions() {


        return this.outputTransactions;
    }

    public Map<String,Double> collectCoins(Double pigcoins) {

                ArrayList<Transaction>  operativeInputs=new ArrayList<Transaction>();
        ArrayList<Transaction>  nonOperativeInputs=new ArrayList<Transaction>();

        Map<String,Double> coinsConsumedInTransaction=new HashMap<String,Double>();
        for (Transaction trxOutput:this.getOutputTransactions()){
            for (Transaction trxInput: this.getInputTransactions()){











                if (trxOutput.getPrev_hash().equals(trxInput.getHash())){
                    nonOperativeInputs.add(trxInput);
                    if (operativeInputs.contains(trxInput)){
                    operativeInputs.remove(trxInput);}
                }



                if (!nonOperativeInputs.contains(trxInput) && !operativeInputs.contains(trxInput)){

                    operativeInputs.add(trxInput);


                }

            }
        }

        for (Transaction trx :operativeInputs){


            if (trx.getPigcoins()==pigcoins){
                coinsConsumedInTransaction.put(trx.getHash(),trx.getPigcoins());
                return coinsConsumedInTransaction;

            }
            else{if(pigcoins<trx.getPigcoins()){
                String hash2="CA"+trx.getHash();

                coinsConsumedInTransaction.put(trx.getHash(),pigcoins);
                coinsConsumedInTransaction.put(hash2,trx.getPigcoins()-pigcoins);
                return coinsConsumedInTransaction;

                }
                else{if(pigcoins>trx.getPigcoins()){


                    if (pigcoins<this.getBalance()){
                        pigcoins=pigcoins-trx.getPigcoins();
                        coinsConsumedInTransaction.put(trx.getHash(),trx.getPigcoins());
                    }
                    else{
                        return new HashMap<String,Double>();
                    }


                    }
                }
            }
        }

    return coinsConsumedInTransaction;}

    public byte[] signTransaction(String message) {

        return GenSig.sign(this.SKey,message);
    }

    public void sendCoins(PublicKey address, Double pigcoins, String message, BlockChain bChain) {

        byte[] signedTransaction = this.signTransaction(message);
        Map<String, Double> collectedCoins = this.collectCoins(pigcoins);

        bChain.processTransactions(this.address,address,collectedCoins,message,signedTransaction);
    }
}
