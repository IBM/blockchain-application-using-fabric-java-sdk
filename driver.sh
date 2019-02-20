#!/bin/bash
#
# Exit on first error, print all commands.
set -e

cd java
mvn install
cd target
cp blockchain-java-sdk-0.0.1-SNAPSHOT-jar-with-dependencies.jar blockchain-client.jar
cp blockchain-client.jar ../../network_resources
cd ../../network_resources
java -cp blockchain-client.jar org.app.network.CreateChannel
java -cp blockchain-client.jar org.app.network.DeployInstantiateChaincode
java -cp blockchain-client.jar org.app.user.RegisterEnrollUser
java -cp blockchain-client.jar org.app.chaincode.invocation.InvokeChaincode
java -cp blockchain-client.jar org.app.chaincode.invocation.QueryChaincode
