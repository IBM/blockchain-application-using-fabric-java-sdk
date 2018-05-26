
* While running `mvn install` command, you may get error as:

  ![](images/err1.png)

  This error says that there is some problem with `<user home>/.m2` directory. Rename/delete the existing .m2 directory and   rerun the command as:

  ```
  mvn clean install
  ```

* If you get following error anytime while running mvn command,

    ```
    log4j:WARN No appenders could be found for logger (org.hyperledger.fabric.sdk.helper.Config).
    log4j:WARN Please initialize the log4j system properly.
    log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
    May 26, 2018 8:25:34 AM org.app.util.Util deleteDirectory
    INFO: Deleting - users
    org.hyperledger.fabric.sdk.exception.TransactionException: Send transactions failed. Reason: UNAVAILABLE
      at org.hyperledger.fabric.sdk.OrdererClient.sendTransaction(OrdererClient.java:169)
      at org.hyperledger.fabric.sdk.Orderer.sendTransaction(Orderer.java:133)
      at org.hyperledger.fabric.sdk.Channel.<init>(Channel.java:222)
      at org.hyperledger.fabric.sdk.Channel.createNewInstance(Channel.java:1121)
      at org.hyperledger.fabric.sdk.HFClient.newChannel(HFClient.java:135)
      at org.app.network.CreateChannel.main(CreateChannel.java:76)
    Caused by: io.grpc.StatusRuntimeException: UNAVAILABLE
      at io.grpc.Status.asRuntimeException(Status.java:540)
      at io.grpc.stub.ClientCalls$StreamObserverToCallListenerAdapter.onClose(ClientCalls.java:392)
    ```

    it means that your hyperledger fabric network is not up and running.
