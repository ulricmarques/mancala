package com.ulric.mancala.Communication;

import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Ulric
 */
public class Client {

    public static void main(String args[]) {
        try {
            MancalaInterface Inv
                    = (MancalaInterface) Naming.lookup("//localhost:5001/InverterRef");

            System.out.println("Objeto Localizado!");

            while(true) {
                System.out.println("Digite a Frase: ");
                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                String line = r.readLine();
                System.out.println("Frase Invertida = ");
            }
        } catch (IOException | NotBoundException e) {
            System.out.println("Erro: "+e.toString());
        }
        System.exit(0);
    }
}
