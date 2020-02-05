package org.komparator.security.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class AttackFreshnessHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String SENDER_NAME = "sender.name";
	//
	// Handler interface implementation
	//

	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		// *** #2 ***
		// get token from request context
		String propertyValue = (String) smc.get(SENDER_NAME);
		
		System.out.print("Sender Name: ");
		System.out.println(propertyValue);
		
		try {
			if(outboundElement.booleanValue()) { 
				System.out.println("Writing header in outbound SOAP message...");
				
				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				
				// check body
				SOAPBody sb = se.getBody();
				if (sb == null) {
					System.out.println("Body not found.");
					return true;
				}
							
				// check header
				SOAPHeader sh = se.getHeader();
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				 
				// get id and digest
				NodeList children = sh.getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {
					Node node = children.item(i);
					if(node.getNodeName().equals("n:NONCE")) {
						sh.removeChild(node);

					}
					if(node.getNodeName().equals("t:Timestamp")) {
						sh.removeChild(node);

					}
				}
				
				msg.saveChanges();
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
		return true;
	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}

}
