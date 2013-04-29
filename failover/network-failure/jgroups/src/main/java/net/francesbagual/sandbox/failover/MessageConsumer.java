package net.francesbagual.sandbox.failover;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class MessageConsumer extends ReceiverAdapter {
	JChannel channel;
	
	public static void main(String[] args) {
		try {
			new MessageConsumer().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		channel=new JChannel();
		channel.setName("remote");
		channel.setReceiver(this);
		channel.connect("producerChannel");
		channel.getState(null, 10000);
		consumerLoop();
		channel.close();
	}

	public void receive(Message msg) {
		String line=msg.getSrc() + ": " + msg.getObject();
		System.out.println(line);
	}

	private void consumerLoop() {
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {
				System.out.print("> "); System.out.flush();
				String line=in.readLine().toLowerCase();
				if(line.startsWith("quit") || line.startsWith("exit")) {
					break;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
