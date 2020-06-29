package communication;

import network.Request;
import network.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents an interface of the Java RMI service. The only method that the service exposes
 * is `sendRequest` which simply handles a requests and produces a response.
 */
public interface RMIService extends Remote {
    Response sendRequest(Request request) throws RemoteException;
}
