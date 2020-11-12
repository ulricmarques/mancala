package com.ulric.mancala.Communication;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Ulric
 */
public class ServerRegistry {
    
    protected int portNumber;
    protected String name;
    
    public ServerRegistry(int portNumber, String name){
        
        this.portNumber = portNumber;
        this.name = name;

    }
    
    public boolean initializeServer() throws RemoteException {
        try {

            Server serverObj = new Server();

            Registry registry = LocateRegistry.createRegistry(this.portNumber);

            registry.rebind(this.name, serverObj);

            System.out.println("Servidor Registrado!");

        } catch (RemoteException e) {
            System.out.println("deu erro:" + e.toString());
            return false;
        }
        
        return true;
        
    }

}

