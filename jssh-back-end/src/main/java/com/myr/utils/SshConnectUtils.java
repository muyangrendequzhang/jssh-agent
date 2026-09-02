package com.myr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.KnownHostsServerKeyVerifier;
import org.apache.sshd.client.keyverifier.RejectAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceParser;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collection;

@Slf4j
public class SshConnectUtils {

    public SshClient baseSshClient(){
        //构建基础的sshClient
        SshClient sshClient = null;
        sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        sshClient.start();
        return sshClient;
    }


    public SshClient acceptAllSshClient(){
        //使用初次接受后续拒绝策略
        SshClient sshClient = null;
        sshClient = SshClient.setUpDefaultClient();
        //配置策略
        Path knownHostsPath = Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts");
        KnownHostsServerKeyVerifier verifier = new KnownHostsServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE,knownHostsPath);
        sshClient.setServerKeyVerifier(verifier);
        sshClient.start();
        return sshClient;
    }

    public SshClient rejectSshClient(){
        //拒绝所有没有存续的公钥
        SshClient sshClient = null;
        sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier(RejectAllServerKeyVerifier.INSTANCE);
        sshClient.start();
        return sshClient;
    }

    public ClientSession usePasswordSession(String username,
                                            String host,
                                            int port,
                                            SshClient client,String password){
        try {
            //使用密码进行校验
            ClientSession session = client.connect(username, host, port).verify(10000).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(10000);
            log.debug("使用密码获取沟通session");
            return session;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public ClientSession usePasswordKeySession(String username,
                                            String host,
                                            int port,
                                            SshClient client, String passwordPath){
        try {
            //使用密钥进行校验
            ClientSession session = client.connect(username, host, port).verify(10000).getSession();
            KeyPairResourceParser loader = SecurityUtils.getKeyPairResourceParser();

            Collection<KeyPair> keys = loader.loadKeyPairs(null, Paths.get(passwordPath), null);

            for (KeyPair kp : keys) {
                session.addPublicKeyIdentity(kp);
            }
            session.auth().verify(10000);
            log.debug("使用密钥获取沟通session");
            return session;
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

    }
}
