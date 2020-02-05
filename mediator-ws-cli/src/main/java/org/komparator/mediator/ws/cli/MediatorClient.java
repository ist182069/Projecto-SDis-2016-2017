package org.komparator.mediator.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.komparator.mediator.ws.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


/**
 * Client.
 *
 * Adds easier endpoint address configuration and 
 * UDDI lookup capability to the PortType generated by wsimport.
 */
public class MediatorClient implements MediatorPortType {
	
	FrontEnd frontEnd;
	
    MediatorService service = null;

    /** WS port (port type is the interface, port is the implementation) */
    MediatorPortType port = null;

    /** UDDI server URL */
    private String uddiURL = null;

    /** WS name */
    private String wsName = null;

    /** WS endpoint address */
    private String wsURL = null; // default value is defined inside WSDL

    public String getWsURL() {
        return wsURL;
    }
    
    public String getuddiURL() {
        return uddiURL;
    }
    
    public String getWsName() {
        return wsName;
    }
    
    public MediatorPortType getPort() {
        return port;
    }
    /** output option **/
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /** constructor with provided web service URL */
    public MediatorClient(String wsURL) throws MediatorClientException {
        this.wsURL = wsURL;
        createStub();
    }

    /** constructor with provided UDDI location and name */
    public MediatorClient(String uddiURL, String wsName) throws MediatorClientException {
        this.uddiURL = uddiURL;
        this.wsName = wsName;
        uddiLookup();
        createStub();
    }

    /** UDDI lookup */
     void uddiLookup() throws MediatorClientException {
        try {
            if (verbose)
                System.out.printf("Contacting UDDI at %s%n", uddiURL);
            UDDINaming uddiNaming = new UDDINaming(uddiURL);

            if (verbose)
                System.out.printf("Looking for '%s'%n", wsName);
            wsURL = uddiNaming.lookup(wsName);

        } catch (Exception e) {
            String msg = String.format("Client failed lookup on UDDI at %s!",
                    uddiURL);
            throw new MediatorClientException(msg, e);
        }

        if (wsURL == null) {
            String msg = String.format(
                    "Service with name %s not found on UDDI at %s", wsName,
                    uddiURL);
            throw new MediatorClientException(msg);
        }
    }

    /** Stub creation and configuration */
    private void createStub() {
        if (verbose)
            System.out.println("Creating stub ...");

        service = new MediatorService();
        port = service.getMediatorPort();

        if (wsURL != null) {
            if (verbose)
                System.out.println("Setting endpoint address ...");
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider
                    .getRequestContext();
            requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
            
            try {
				//if(wsName!=null)
					frontEnd = new FrontEnd(this,requestContext);
			} catch (MediatorClientException e) {
			
			}
        
        }
    }
    
    
	@Override
	public void clear() {
		frontEnd.clear();
	}

	@Override
	public List<CartView> listCarts() {
		return frontEnd.listCarts();
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		return frontEnd.searchItems(descText);
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr) throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		return frontEnd.buyCart(cartId, creditCardNr);
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		frontEnd.addToCart(cartId, itemId, itemQty);
		
	}

	@Override
	public String ping(String name) {
		return frontEnd.ping(name);
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return frontEnd.shopHistory();
	}


	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		return frontEnd.getItems(productId);
	}
	
	@Override
	public void imAlive() {
		frontEnd.imAlive();
	}

	@Override
	public void updateShopHistory(List<ShoppingResultView> shopResults, String nonce) {
		frontEnd.updateShopHistory(shopResults, nonce);
	}

	@Override
	public void updateCart(List<CartView> updatedCarts, String nonce) {
		frontEnd.updateCart(updatedCarts, nonce);
	}

}