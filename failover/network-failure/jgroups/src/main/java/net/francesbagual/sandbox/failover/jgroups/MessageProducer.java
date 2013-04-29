package net.francesbagual.sandbox.failover.jgroups;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class MessageProducer extends ReceiverAdapter {    
	JChannel channel;
	
	private void start() throws Exception {
		channel=new JChannel();
		channel.setName("producer");
		channel.setReceiver(this);
		channel.connect("producerChannel");
		channel.getState(null, 10000);
		producerLoop();
		channel.close();
	}

	private void producerLoop() {
		int i = 0;
		while(true) {
			try {
				Message msg=new Message(null, null, "Message number " + i);
				channel.send(msg);
				i++;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try{
			new MessageProducer().start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
