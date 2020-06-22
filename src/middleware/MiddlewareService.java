package middleware;

import protocol.Request;
import protocol.Response;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MiddlewareService extends Remote {
    Response sendRequest(Request request) throws RemoteException;
}
