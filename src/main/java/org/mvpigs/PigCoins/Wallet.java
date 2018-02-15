package org.mvpigs.PigCoins;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Wallet {



    private PrivateKey SKey;
    private PublicKey address;
    private double total_input;
    private double total_output;
    private double balance;
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
        this.total_input = this.total_input+total_input;
    }

    public void setTotal_output(double total_output) {
        this.total_output =this.total_output+total_output;
    }

    public void loadCoins(BlockChain bChain) {
        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_recipient().hashCode()==this.getAddress().hashCode()){

                this.setTotal_input(trx.getPigcoins());
            }
            if (trx.getPkey_sender().hashCode()==this.getAddress().hashCode()){

                this.setTotal_output(trx.getPigcoins());
            }

        }
        this.setBalance();
    }

    public void loadInputTransactions(BlockChain bChain) {

        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_recipient().hashCode()==this.getAddress().hashCode()){

                this.inputTransactions.add(trx);
            }

        }
    }

    public ArrayList<Transaction> getInputTransactions() {



    return this.inputTransactions;}

    public void loadOutputTransactions(BlockChain bChain) {
        for (Transaction trx :bChain.getBlockChain()){

            if (trx.getPkey_sender().hashCode()==this.getAddress().hashCode()){

                this.outputTransactions.add(trx);
            }

        }

    }

    public ArrayList<Transaction> getOutputTransactions() {


        return this.outputTransactions;
    }
}
