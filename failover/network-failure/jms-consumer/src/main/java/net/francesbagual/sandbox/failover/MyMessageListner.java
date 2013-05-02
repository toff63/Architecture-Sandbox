package net.francesbagual.sandbox.failover;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = "queue/test", activationConfig = { 
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test"),
        @ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory"),
        @ActivationConfigProperty(propertyName = "user", propertyValue = "testuser"),
        @ActivationConfigProperty(propertyName = "password", propertyValue = "testuser1")})
public class MyMessageListner implements MessageListener {

	public void onMessage(Message message) {
		try {
			System.out.println("Remote consumer > " + ((TextMessage) message).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
