/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */ 
package org.app.chaincode.invocation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.config.Config;
import org.app.user.UserContext;
import org.app.util.Util;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;

/**
 * 
 * @author Balaji Kadambi
 *
 */

public class InvokeQueryChaincode {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	public static void main(String args[]) {
		try {
            Util.cleanUp();
			String caUrl = Config.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
			
			FabricClient fabClient = new FabricClient(adminUserContext);
			
			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			channel.addPeer(peer);
			channel.addEventHub(eventHub);
			channel.addOrderer(orderer);
			channel.initialize();

			TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
			ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
			request.setChaincodeID(ccid);
			request.setFcn("createCar");
			String[] arguments = { "CAR1", "Chevy", "Volt", "Red", "Nick" };
			request.setArgs(arguments);
			request.setProposalWaitTime(1000);

			Map<String, byte[]> tm2 = new HashMap<>();
			tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
																								// in transient map
			tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
			tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
			tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
			request.setTransientMap(tm2);
			Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
			
			Thread.sleep(10000);
			
			Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode("fabcar", "queryAllCars", null);
			for (ProposalResponse pres : responsesQuery) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				System.out.println(stringResponse);
			}

			Thread.sleep(10000);
			String[] args1 = {"CAR1"};
			Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode("fabcar", "queryCar", args1);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				System.out.println(stringResponse);
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
