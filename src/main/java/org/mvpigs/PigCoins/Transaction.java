package org.mvpigs.PigCoins;

import java.security.PublicKey;



public class Transaction {
    private String hash;
    private String prev_hash;
    private PublicKey pkey_sender;
    private PublicKey pkey_recipient;
    private double pigcoins;
    private String message;
    private byte[] signature;


    Transaction(String hash, String prev_hash, PublicKey address, PublicKey address1, double cantidad, String mensaje) {
        this.hash=hash;
        this.prev_hash=prev_hash;
        this.pkey_sender=address;
        this.pkey_recipient=address1;
        this.pigcoins=cantidad;
        this.message=mensaje;


    }

    Transaction() {

    }

    public String getHash() {
        return hash;
    }

    public String getPrev_hash() {
        return prev_hash;
    }

    public PublicKey get_PK_sender() {
        return pkey_sender;
    }

    public PublicKey get_PK_recipient() {
        return pkey_recipient;
    }

    public double getPigCoins() {
        return pigcoins;
    }

    public String getMessage() {
        return message;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString(){
        return "\n hash =  "+ this.getHash()+
                "\n prev_hash = "+ this.getPrev_hash()+
                "\n pkey_sender = "+ this.get_PK_sender().hashCode()+
                "\n pkey_recipient = "+ this.get_PK_recipient().hashCode()+
                "\n pigcoins = "+String.valueOf(this.getPigCoins())+
                "\n message = "+ this.getMessage()+"\n";



    }

    @Override
    public boolean equals(Object object){
        boolean sameSame = false;

        if (object != null && object instanceof Transaction)
        {
            sameSame = this.getHash().equals(((Transaction) object).getHash());
        }

        return sameSame;

    }
    @Override
    public int hashCode(){



    return Integer.valueOf(this.getHash().substring(this.getHash().length()-1));}


}
