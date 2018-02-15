package org.mvpigs.PigCoins;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {



    private PrivateKey SKey;
    private PublicKey address;
    private double total_input;
    private double total_output;
    private double balance;
    private Transaction[] inputTransactions;
    private Transaction[] outputTransactions;

    public Wallet(){
        
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

    @Override
    public String toString(){
        String Cartera="\n Wallet = "+this.getAddress().hashCode()+
                "\n Total input = "+String.valueOf(this.getTotal_input())+
                "\n Total output = "+String.valueOf(this.getTotal_output())+
                "\n Balance = "+String.valueOf(this.getBalance())+"\n";
    return Cartera;}
}
