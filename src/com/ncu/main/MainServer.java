package com.ncu.main;
import com.ncu.processors.*;

public class MainServer {
  public final static int SOCKET_PORT = 13267;  // you may change this

	public static void main(String args[]){
		FileServer fs= new FileServer(SOCKET_PORT);	
		fs.start();
	}

}