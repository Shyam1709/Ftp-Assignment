package com.ncu.processors;
import com.ncu.validators.*;

import java.io.BufferedInputStream;
import java.io.*;
import java.nio.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class FileServer {

	private ServerSocket serverSocket;
	//private Socket clientSocket;
	public final static int SOCKET_PORT = 13267; // you may change this
	public final static String SERVER_DIRECTORY = System.getProperty("user.dir") + File.separator + "Storage/server/";
	public final static int FILE_SIZE = 6022386; // file size is temporary hard coded
	public final static String SERVER = "localhost";  // localhost
	String log4jConfigFile = System.getProperty("user.dir")+ File.separator + "configs/logger/logger.properties";


	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	DataInputStream dis=null;
	DataOutputStream dos=null;
Logger logger = Logger.getLogger(FileServer.class);


	public FileServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(){
		PropertyConfigurator.configure(log4jConfigFile);
		try{	
			Socket clientSocket = serverSocket.accept();
			logger.info("Accepted connection : " + clientSocket);
			System.out.println("Waiting...");
			System.out.println("Waiting for Command ...");
			while (true) {
				try
				{
					this.dis=new DataInputStream(clientSocket.getInputStream());
				// this.dos=new DataOutputStream(clientSocket.getOutputStream()); 
					String Command=this.dis.readUTF();
					if(Command.compareTo("GET")==0)
					{
						logger.info("\tGET Command Received ...");
						this.sendFile(clientSocket);
					}
					else if(Command.compareTo("SEND")==0)
					{
						logger.info("\tSEND Command Receiced ...");               
						this.recieveFile(clientSocket);
					}
					// else if(Command.compareTo("DISCONNECT")==0)
					// {
            
					// }
				}catch(Exception e){}

			}
		}catch(Exception e){}
	}

	public void sendFile(Socket clientSock) throws IOException{
		PropertyConfigurator.configure(log4jConfigFile);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			String fileName=this.dis.readUTF();
			String filepath = SERVER_DIRECTORY + fileName;
			File file=new File(filepath);
			this.dos=new DataOutputStream(clientSock.getOutputStream());
			if(!file.exists())
			{
				this.dos.writeUTF("File Not Found");
				return;
			}
			else{
			try {
				this.dos.writeUTF("READY");
				String clientMessage=this.dis.readUTF();
				if(clientMessage.compareTo("Cancel")==0){
				return;	
				}else if(clientMessage.compareTo("Overwrite")==0){
				File myFile = new File (filepath);
				logger.info("Sending..." + filepath);
				byte [] mybytearray  = new byte [(int)myFile.length()];
					// to read byte oriented data from file
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
					// It read the bytes from the specified byte-input stream into a specified byte array
				bis.read(mybytearray,0,mybytearray.length);
				os = clientSock.getOutputStream();
				os.write(mybytearray,0,mybytearray.length);
				System.out.println("Done.");
			}
			}catch(Exception e){}

		}}
		catch(Exception e){}
	}

	public void recieveFile(Socket clientSock) throws IOException{
		
		String fileName = this.dis.readUTF();
		if(fileName.compareTo("File not found")==0)
        {
            return;
        }
		String filepath = SERVER_DIRECTORY + fileName;
		FileOutputStream fos = new FileOutputStream(filepath);
		byte[] buffer = new byte[FILE_SIZE];
		
		int filesize = FILE_SIZE; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = this.dis.available();
		while((read = this.dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		fos.close();		
	}
}