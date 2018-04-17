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
package org.app.network;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.config.Config;
import org.app.user.UserContext;
import org.app.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 * 
 * @author Balaji Kadambi
 *
 */

public class DeployInstantiateChaincode {

	public static void main(String[] args) {
		try {
			CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
			// Enroll Admin Users to Org1 and Org2
//			CAClient caClientOrg1 = new CAClient(Config.CA_ORG1_URL, null);
//			CAClient caClientOrg2 = new CAClient(Config.CA_ORG2_URL, null);
//
//			UserContext adminUserOrg1 = new UserContext();
//			adminUserOrg1.setMspId(Config.ORG1_MSP);
//			adminUserOrg1.setName(Config.ADMIN);
//			adminUserOrg1.setAffiliation(Config.ORG1);
//			caClientOrg1.setAdminUserContext(adminUserOrg1);
//
//			UserContext adminUserOrg2 = new UserContext();
//			adminUserOrg2.setMspId(Config.ORG2_MSP);
//			adminUserOrg2.setName(Config.ADMIN);
//			adminUserOrg2.setAffiliation(Config.ORG2);
//			caClientOrg2.setAdminUserContext(adminUserOrg2);
//
//			caClientOrg1.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);
//			caClientOrg2.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

			UserContext org1Admin = new UserContext();
			File pkFolder1 = new File(Config.ORG1_USR_ADMIN_PK);
			File[] pkFiles1 = pkFolder1.listFiles();
			File certFolder = new File(Config.ORG1_USR_ADMIN_CERT);
			File[] certFiles = certFolder.listFiles();
			Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
					Config.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
			org1Admin.setEnrollment(enrollOrg1Admin);
			org1Admin.setMspId("Org1MSP");
			org1Admin.setName("admin");

			FabricClient fabClient = new FabricClient(org1Admin);

			Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			Peer peer0 = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			Peer peer1 = fabClient.getInstance().newPeer(Config.ORG1_PEER_1, Config.ORG1_PEER_1_URL);
			mychannel.addOrderer(orderer);
			mychannel.addPeer(peer0);
			mychannel.addPeer(peer1);
			mychannel.initialize();

			Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
					Config.CHAINCODE_1_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(),
					Config.CHAINCODE_1_VERSION, mychannel.getPeers());
			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
			}

			ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

			String[] arguments = { "" };
			response = channelClient.instantiateChainCode(Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION,
					Config.CHAINCODE_1_PATH, Type.GO_LANG.toString(), "init", arguments, null);

			for (ProposalResponse res : response) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
