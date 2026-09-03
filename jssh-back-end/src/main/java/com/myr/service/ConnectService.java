package com.myr.service;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import org.apache.sshd.client.session.ClientSession;

public interface ConnectService {

    Result<String> connect(ConnectParam param);

    void close();

    ClientSession getSession();
}
