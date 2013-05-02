package net.francesbagual.sandbox.failover;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;

public class JMSClient {

	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("host", "192.168.20.5");
		TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
		HornetQJMSConnectionFactory factory = (HornetQJMSConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF,transportConfiguration);
		factory.setCallTimeout(500L);
		Queue queue = HornetQJMSClient.createQueue("testQueue");

		while(true){
			String msg = "This is a text message sent at " + new Date();
			try {
				sendMessage(factory, queue, msg);
			} catch (JMSException e) {
//				e.printStackTrace();
				System.out.println("Failed to send: " + e.getMessage());
				System.out.println("Received: " + msg);
			}
		}
	}

	private static void sendMessage(ConnectionFactory factory, Queue queue, String msg) throws JMSException {
		Connection connection = factory.createConnection("testuser", "testuser1");
		connection.setExceptionListener(new ExceptionListener() {
			
			public void onException(JMSException exception) {
				System.out.println("I detected an error!! I can do stuff here :)");
				
			}
		});
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(queue);
		producer.setTimeToLive(500);
		// Sends a text message to the topic
		TextMessage message = session.createTextMessage();
		message.setText(msg);
		producer.send(message);
		connection.close();
	}
}
