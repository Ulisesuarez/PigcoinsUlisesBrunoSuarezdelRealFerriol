package org.mvpigs.PigCoins;

import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {
    
    private PrivateKey privateKey;
    private PublicKey address;
    private int total_input;
    private int total_output;
    private int balance;
    private Transaction[] inputTransactions;
    private Transaction[] outputTransactions;
    public Wallet(){
        
    }

    public void setSK(PrivateKey SK) {
        this.privateKey = SK;
    }

    public void setAddress(PublicKey aPublic) {
        this.address = aPublic;
    }

    public PublicKey getAddress() {
        return address;
    }
}
