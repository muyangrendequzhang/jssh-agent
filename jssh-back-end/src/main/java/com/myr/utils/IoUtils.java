package com.myr.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

/**
 * 本地文件 IO 工具：负责连接信息等数据在本地文件上的读写。
 */
@Slf4j
public final class IoUtils {

    private IoUtils() {
    }

    /**
     * 读取整个文件内容为字符串，文件不存在时返回 null。
     */
    public static String read(Path file) throws IOException {
        if (!Files.exists(file)) {
            return null;
        }
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    /**
     * 按行读取文件内容，文件不存在时返回空列表。
     */
    public static List<String> readLines(Path file) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    /**
     * 覆盖写入字符串内容，自动创建父目录。
     */
    public static void write(Path file, String content) throws IOException {
        ensureParent(file);
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 追加写入字符串内容，自动创建父目录。
     */
    public static void append(Path file, String content) throws IOException {
        ensureParent(file);
        Files.write(file, content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * 按行覆盖写入，自动创建父目录。
     */
    public static void writeLines(Path file, List<String> lines) throws IOException {
        ensureParent(file);
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /**
     * 删除指定路径的文件，文件不存在时不做任何操作。
     */
    public static void delete(Path file) throws IOException {
        Files.deleteIfExists(file);
    }

    /**
     * 确保父目录存在，不存在则递归创建。
     */
    public static void ensureParent(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /**
     * 将连接信息写入 properties 文件，自动创建父目录。
     */
    public static void saveConnection(ConnectionInfo info, Path file) throws IOException {
        ensureParent(file);
        Properties props = new Properties();
        if (info.getHost() != null) {
            props.setProperty("host", info.getHost());
        }
        if (info.getUser() != null) {
            props.setProperty("user", info.getUser());
        }
        if (info.getPassword() != null) {
            props.setProperty("password", info.getPassword());
        }
        if (info.getPrivateKeyPath() != null) {
            props.setProperty("privateKeyPath", info.getPrivateKeyPath());
        }
        props.setProperty("port", String.valueOf(info.getPort()));

        try (OutputStream out = Files.newOutputStream(file)) {
            props.store(out, "jssh-agent connection info");
        }
        log.debug("连接信息已保存到 {}", file.toAbsolutePath());
    }

    /**
     * 从 properties 文件读取连接信息，文件不存在时返回 null。
     */
    public static ConnectionInfo loadConnection(Path file) throws IOException {
        if (!Files.exists(file)) {
            return null;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }

        ConnectionInfo info = new ConnectionInfo();
        info.setHost(props.getProperty("host"));
        info.setUser(props.getProperty("user"));
        info.setPassword(props.getProperty("password"));
        info.setPrivateKeyPath(props.getProperty("privateKeyPath"));
        String port = props.getProperty("port");
        if (port != null && !port.isBlank()) {
            try {
                info.setPort(Integer.parseInt(port.trim()));
            } catch (NumberFormatException e) {
                log.warn("端口解析失败，使用默认值 22: {}", port);
            }
        }
        return info;
    }

    /**
     * 连接信息模型：保存连接所需的数据。
     */
    @Data
    public static class ConnectionInfo {

        private String host;
        private int port = 22;
        private String user;
        private String password;
        private String privateKeyPath;
    }
}
