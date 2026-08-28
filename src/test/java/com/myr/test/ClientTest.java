package com.myr.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.KnownHostsServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceParser;
import org.apache.sshd.common.util.io.output.NoCloseOutputStream;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;


@Slf4j
@SpringBootTest
public class ClientTest {

    @Value("${ssh.connect.user}")
    private String user;

    @Value("${ssh.connect.host}")
    private String host;

    @Value("${ssh.connect.password}")
    private String password;

    @Test
    public void testClient() throws IOException {
        SshClient sshClient = null;
        try {
            sshClient = SshClient.setUpDefaultClient();

            //配置默认的公钥接受策略
            //sshClient.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);


            //配置校验策略
            Path knownHostsPath = Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts");
            KnownHostsServerKeyVerifier verifier = new KnownHostsServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE,knownHostsPath);
            sshClient.setServerKeyVerifier(verifier);

            sshClient.start();
            ClientSession session = sshClient.connect(user, host, 22).verify(10000).getSession();

            KeyPairResourceParser loader = SecurityUtils.getKeyPairResourceParser();

            Collection<KeyPair> keys = loader.loadKeyPairs(null, Paths.get(password), null);

            for (KeyPair kp : keys) {
                session.addPublicKeyIdentity(kp);
            }
            session.auth().verify(10000);
            log.info("认证成功");

            try (ChannelExec channel = session.createExecChannel("free -m")) {
                channel.setOut(new NoCloseOutputStream(System.out));
                channel.setErr(new NoCloseOutputStream(System.err));
                channel.open().verify(10000);
                channel.waitFor(Collections.singletonList(ClientChannelEvent.CLOSED), 5000);
            }

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } finally {
            if (sshClient != null){
                sshClient.stop();;
                sshClient.close();
            }
        }


    }


}