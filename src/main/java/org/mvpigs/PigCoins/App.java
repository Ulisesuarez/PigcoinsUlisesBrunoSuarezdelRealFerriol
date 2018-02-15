package org.mvpigs.PigCoins;



import java.security.KeyPair;
import java.util.Map;

public class App {

    public static void main( String[] args ) {
        /**
         * Crea una wallet
         * Genera las claves privada y publica de la wallet
         */

        System.out.println("\n" + "Ver clave Privada y clave Pública de una wallet" + "\n" +
                "===============================================");

        Wallet wallet_1 = new Wallet();
        KeyPair pair = GenSig.generateKeyPair();
        wallet_1.setSK(pair.getPrivate());
        wallet_1.setAddress(pair.getPublic());

        System.out.println("\n Direccion de la Wallet_1: \n" + wallet_1.getAddress().hashCode());

        /**
         * Crea una segunda wallet, esta vez generando sus claves
         * con un metodo wallet.generateKeyPair() que encapsula
         * el codigo de la anterior historia de usuario
         */

        Wallet wallet_2 = new Wallet();
        wallet_2.generateKeyPair();

        System.out.println("\n Direccion de la Wallet_2: \n" + wallet_2.getAddress().hashCode());

        /**
         * Visualiza las Wallet 1 y 2
         */

        System.out.println("\n" + "Ver Wallets 1 y 2" + "\n" +
                "================="        );

        System.out.println("Wallet_1: \n" + wallet_1.toString());
        System.out.println("Wallet_2: \n" + wallet_2.toString());

        /**
         * Crea una transaccion de pigcoins
         * Visualiza la transaccion
         */

        System.out.println("\n" + "Ver transaccion" + "\n" +
                "==============="        );

        Transaction trx = new Transaction();
        trx = new Transaction("hash_1", "0", wallet_1.getAddress(), wallet_2.getAddress(), 20, "a flying pig!");

        System.out.println(trx.toString());


        /**
         * Crea el blockchain
         * y añade transacciones que crean moneda "pigcoins"
         */

        System.out.println("\n" + "Ver BlockChain" + "\n" +
                "=============="        );

        // Crea primero la direccion "origen" del sistema que genera los pigcoins
        Wallet origin = new Wallet();
        origin.generateKeyPair();

        BlockChain bChain = new BlockChain();
        trx = new Transaction("hash_1", "0", origin.getAddress(), wallet_1.getAddress(), 20, "bacon eggs");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_2", "1", origin.getAddress(), wallet_2.getAddress(), 10, "spam spam spam");
        bChain.addOrigin(trx);
        trx = new Transaction("hash_3", "hash_1", wallet_1.getAddress(), wallet_2.getAddress(), 20, "a flying pig!");
        bChain.addOrigin(trx);

        // Visualiza el blockchain
        bChain.summarize();






    }

}