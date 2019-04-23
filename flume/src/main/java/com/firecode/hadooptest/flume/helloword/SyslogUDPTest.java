package com.firecode.hadooptest.flume.helloword;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SyslogUDPTest {
	
	public static void main(String[] args) throws IOException {
	    DatagramSocket socket = new DatagramSocket();
	    byte[] bytes = "mao_test_mao".getBytes();
	    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
	    packet.setAddress(InetAddress.getByName("192.168.83.137"));
	    packet.setPort(9899);
	    socket.send(packet);
	    socket.close();
	    System.err.println("发送完成.");
	}

}
