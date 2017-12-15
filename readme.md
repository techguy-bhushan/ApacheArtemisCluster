# ApacheArtemisCluster Connected with Spring

* Download [Apache Artemis](https://activemq.apache.org/artemis/download.html)
or Download []JBoss AMQ](https://developers.redhat.com/products/amq/download)
* create 2 artemis server
* use [broker/server0/broker.xml](https://github.com/techguy-bhushan/ApacheArtemisCluster/blob/master/broker/server0/broker.xml) and [broker/server1/broker.xml](https://github.com/techguy-bhushan/ApacheArtemisCluster/blob/master/broker/server1/broker.xml) (replace your broker.xml with given xml file )
* start both artemis server
* Run ArtemisTest under test folder

this implementation is based on clustered static discovery

Please change the user name and password with your server username and password in ArtemisConfiguration.java -> activeMQJMSConnectionFactory

```
activeMQJMSConnectionFactory.setPassword("admin");
   activeMQJMSConnectionFactory.setUser("admin");
```

Here use are using **STRICT** value of message-load-balancing in broker.xml.
In case of STRICT incoming message will be round robin'd even though the same queues on the other nodes of the cluster may have no consumers at all, 
or they may have consumers that have non matching message filters (selectors).
Note that Apache ActiveMQ Artemis will not forward messages to other nodes if there are no queues of the same name on the other nodes, even if this parameter is set to STRICT. 
Using STRICT is like setting the legacy forward-when-no-consumers parameter to true. 


**NOTE**: HERE we are using 2 nodes inside of cluster so we are using 2 listener in ArtemisConfiguration
here is [reason][https://stackoverflow.com/questions/47779767/apache-artemis-need-to-created-many-connection-as-many-server-node-with-strict-c/47780473?noredirect=1#comment82554004_47780473]