package communication;

import network.Request;
import network.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {
    Response sendRequest(Request request) throws RemoteException;
}
