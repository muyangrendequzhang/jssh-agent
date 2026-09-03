package com.myr.service;

import com.myr.entity.ConnectParam;

import java.io.IOException;
import java.util.List;

public interface ConnectionService {

    List<ConnectParam> getConnections() throws IOException;

    void deleteConnection(String connectName) throws IOException;
}
