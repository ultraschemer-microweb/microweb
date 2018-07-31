package com.ultraschemer.microweb.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This class has facilities to get Machine identification, considering the Network Adapter, and process identification,
 * including process and threads - useful for distributed systems.
 *
 * Require, at least, Java 1.9 or higher.
 */
public class MachineIdentification {
    /**
     * Get the main machine address, from IPv4
     * @return The address.
     */
    public static String getMachineAddress() {
        String ip;

        try {
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
            }
        } catch (Exception e) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * Get the current process id.
     * @return The process id.
     */
    public static long getProcessId() {
        return ProcessHandle.current().pid();
    }

    /**
     * Get the current Thread id.
     * @return TGhe thread id.
     */
    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    /**
     * Return a full thread identification, consisting of the machine network address, the process id and the thread id.
     * @return The full thread identification.
     */
    public static String getFullTreadIdentification() {
        return String.format("%s %d/%d", getMachineAddress(), getProcessId(), getThreadId());
    }
}
