# ApacheArtemisCluster Connected with Spring

* Download [Apache Artemis](https://activemq.apache.org/artemis/download.html)
* create 2 artemis server
* use [broker/server0/broker.xml](https://github.com/techguy-bhushan/ApacheArtemisCluster/blob/master/broker/server0/broker.xml) and [broker/server1/broker.xml](https://github.com/techguy-bhushan/ApacheArtemisCluster/blob/master/broker/server1/broker.xml) (replace your broker.xml with given xml file )
* start both artemis server
* Run ArtemisTest under test folder

this implementation is based on [clustered-static-discovery](https://github.com/apache/activemq-artemis/tree/master/examples/features/clustered/clustered-static-discovery)
 


Please change the user name and password with your server username and password in ArtemisConfiguration.java -> activeMQJMSConnectionFactory

```
activeMQJMSConnectionFactory.setPassword("admin");
   activeMQJMSConnectionFactory.setUser("admin");
```

