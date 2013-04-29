package net.francesbagual.sandbox.failover;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class FailoverRouter extends ReceiverAdapter {    
	private JChannel localChannel;
	private Address from;
	private Address to;
	
	private void start() throws Exception {
		localChannel=new JChannel();
		localChannel.setName("router");
		localChannel.setReceiver(this);
		localChannel.connect("producerChannel");
		localChannel.getState(null, 10000);
		System.out.println("We are " + localChannel.getAddress());
		synchronized (this) {
			from = localChannel.getAddress();
			to = localChannel.getAddress();
		}
		receiverLoop();
		localChannel.close();
	}
	
	private void setFrom(Address addr){
		synchronized (from) {
			from = addr;
		}
	}
	
	private void setTo(Address addr){
		if(to == null) to = addr;
		
		synchronized (to) {
			to = addr;
		}
	}
	
	public void viewAccepted(View view) {
		List<Address> members = view.getMembers();
		boolean remoteUp = false;
		for(Address addr : members){
			if(addr.toString().equals("producer")) setFrom(addr);
			if(addr.toString().equals("remote")) {
				remoteUp = true;
				setTo(addr);
			}
		}
		if(!remoteUp) setTo(localChannel.getAddress());
		System.out.println("List of members: " + view.getMembers());
    }
	
	@Override
	public void suspect(Address mbr) {
		System.out.println("Suspecting " + mbr + " do be down.");
	}

	private Address getTo(){
		synchronized (to) {
			return to;
		}
	}

	public void receive(Message msg) {
		if(localChannel.getAddress().equals(getTo())){
			String line=msg.getSrc() + ": " + msg.getObject();
			System.out.println(line);
		} else{
			try {
				localChannel.send(new Message(to, from, msg.getObject()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void receiverLoop() {
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			try {
				System.out.print("> "); System.out.flush();
				String line=in.readLine().toLowerCase();
				if(line.startsWith("quit") || line.startsWith("exit")) {
					break;
				}
				line="["+ from + "->"+ to+"] " + line;
				Message msg=new Message(to, from, line);
				System.out.println(msg);
				localChannel.send(msg);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try{
			new FailoverRouter().start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
