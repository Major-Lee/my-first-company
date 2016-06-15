1:插件安装
1) cd D:\elasticsearch-1.0.1\bin
plugin -install medcl/elasticsearch-analysis-pinyin/1.2.0

2) 类和配置文件都导入eclipse 导出jar
在到plugins目录中，新建一个文件夹名字叫 analysis-pinyin，把导出的jar和依赖的jar放进去。
在到config目录中,修改elasticsearch.yml


2.测试分词
http://localhost:9200/indexname/_analyze?text=xxx&analyzer=pinyin_analyzer
http://localhost:9200/indexname/_analyze?text=xxx&tokenizer=xx&filters=xxx

3.监控状态
http://localhost:9200/_plugin/bigdesk
http://localhost:9200/_plugin/head
http://localhost:9200/_cat/shards
localhost:9200/_nodes/process?pretty 查看节点的max_file_descriptors, 一定要设置到32k或者64k

4:配置文件
elasticsearch的config文件夹里面有两个配置文件：elasticsearch.yml和logging.yml，第一个是es的基本配置文件，第二个是日志配置文件，es也是使用log4j来记录日志的，所以logging.yml里的设置按普通log4j配置文件来设置就行了。下面主要讲解下elasticsearch.yml这个文件中可配置的东西。

cluster.name: elasticsearch
配置es的集群名称，默认是elasticsearch，es会自动发现在同一网段下的es，如果在同一网段下有多个集群，就可以用这个属性来区分不同的集群。

node.name: "Franz Kafka"
节点名，默认随机指定一个name列表中名字，该列表在es的jar包中config文件夹里name.txt文件中，其中有很多作者添加的有趣名字。

node.master: true
指定该节点是否有资格被选举成为node，默认是true，es是默认集群中的第一台机器为master，如果这台机挂了就会重新选举master。

node.data: true
指定该节点是否存储索引数据，默认为true。

index.number_of_shards: 5
设置默认索引分片个数，默认为5片。

index.number_of_replicas: 1
设置默认索引副本个数，默认为1个副本。

path.conf: /path/to/conf
设置配置文件的存储路径，默认是es根目录下的config文件夹。

path.data: /path/to/data
设置索引数据的存储路径，默认是es根目录下的data文件夹，可以设置多个存储路径，用逗号隔开，例：
path.data: /path/to/data1,/path/to/data2

path.work: /path/to/work
设置临时文件的存储路径，默认是es根目录下的work文件夹。

path.logs: /path/to/logs
设置日志文件的存储路径，默认是es根目录下的logs文件夹

path.plugins: /path/to/plugins
设置插件的存放路径，默认是es根目录下的plugins文件夹

bootstrap.mlockall: true
设置为true来锁住内存。因为当jvm开始swapping时es的效率会降低，所以要保证它不swap，可以把ES_MIN_MEM和ES_MAX_MEM两个环境变量设置成同一个值，并且保证机器有足够的内存分配给es。同时也要允许elasticsearch的进程可以锁住内存，linux下可以通过`ulimit -l unlimited`命令。

network.bind_host: 192.168.0.1
设置绑定的ip地址，可以是ipv4或ipv6的，默认为0.0.0.0。


network.publish_host: 192.168.0.1
设置其它节点和该节点交互的ip地址，如果不设置它会自动判断，值必须是个真实的ip地址。

network.host: 192.168.0.1
这个参数是用来同时设置bind_host和publish_host上面两个参数。

transport.tcp.port: 9300
设置节点间交互的tcp端口，默认是9300。

transport.tcp.compress: true
设置是否压缩tcp传输时的数据，默认为false，不压缩。

http.port: 9200
设置对外服务的http端口，默认为9200。

http.max_content_length: 100mb
设置内容的最大容量，默认100mb

http.enabled: false
是否使用http协议对外提供服务，默认为true，开启。

gateway.type: local
gateway的类型，默认为local即为本地文件系统，可以设置为本地文件系统，分布式文件系统，hadoop的HDFS，和amazon的s3服务器，其它文件系统的设置方法下次再详细说。

gateway.recover_after_nodes: 1
设置集群中N个节点启动时进行数据恢复，默认为1。

gateway.recover_after_time: 5m
设置初始化数据恢复进程的超时时间，默认是5分钟。

gateway.expected_nodes: 2
设置这个集群中节点的数量，默认为2，一旦这N个节点启动，就会立即进行数据恢复。

cluster.routing.allocation.node_initial_primaries_recoveries: 4
初始化数据恢复时，并发恢复线程的个数，默认为4。

cluster.routing.allocation.node_concurrent_recoveries: 2
添加删除节点或负载均衡时并发恢复线程的个数，默认为4。

indices.recovery.max_size_per_sec: 0
设置数据恢复时限制的带宽，如入100mb，默认为0，即无限制。

indices.recovery.concurrent_streams: 5
设置这个参数来限制从其它分片恢复数据时最大同时打开并发流的个数，默认为5。

discovery.zen.minimum_master_nodes: 1
设置这个参数来保证集群中的节点可以知道其它N个有master资格的节点。默认为1，对于大的集群来说，可以设置大一点的值（2-4）
#Its recommended to set it to a higher value than 1 when running more than 2 nodes in the cluster

discovery.zen.ping.timeout: 3s
设置集群中自动发现其它节点时ping连接超时时间，默认为3秒，对于比较差的网络环境可以高点的值来防止自动发现时出错。

discovery.zen.ping.multicast.enabled: false
设置是否打开多播发现节点，默认是true。

discovery.zen.ping.unicast.hosts: ["host1", "host2:port", "host3[portX-portY]"]
设置集群中master节点的初始列表，可以通过这些节点来自动发现新加入集群的节点。

下面是一些查询时的慢日志参数设置
index.search.slowlog.level: TRACE
index.search.slowlog.threshold.query.warn: 10s
index.search.slowlog.threshold.query.info: 5s
index.search.slowlog.threshold.query.debug: 2s
index.search.slowlog.threshold.query.trace: 500ms

index.search.slowlog.threshold.fetch.warn: 1s
index.search.slowlog.threshold.fetch.info: 800ms
index.search.slowlog.threshold.fetch.debug:500ms
index.search.slowlog.threshold.fetch.trace: 200ms

5:线程池设置
一个Elasticsearch节点会有多个线程池，但重要的是下面四个：
索引（index）：主要是索引数据和删除数据操作（默认是cached类型）
搜索（search）：主要是获取，统计和搜索操作（默认是cached类型）
批量操作（bulk）：主要是对索引的批量操作（默认是cached类型）
更新（refresh）：主要是更新操作（默认是cached类型）
可以通过给设置一个参数来改变线程池的类型（type），例如，把索引的线程池改成blocking类型：

threadpool:   
    index:   
        type: blocking   
        min: 1   
        size: 30   
        wait_time: 30s  
下面是三种可以设置的线程池的类型
cache
cache线程池是一个无限大小的线程池，如果有很请求的话都会创建很多线程，下面是个例子：

threadpool:   
    index:   
        type: cached  
fixed
fixed线程池保持固定个数的线程来处理请求队列。
size参数设置线程的个数，默认设置是cpu核心数的5倍
queue_size可以控制待处理请求队列的大小。设置为-1，意味着无限制。当一个请求到来但队列满了的时候，reject_policy参数可以控制它的行为。默认是abort，会使那个请求失败。设置成caller会使该请求在io线程中执行。

threadpool:   
    index:   
        type: fixed   
        size: 30   
        queue: 1000   
        reject_policy: caller  
blocking
blocking线程池允许设置一个最小值（min，默认为1）和线程池大小（size，默认为cpu核心数的5倍）。它也有一个等待队列，队列的大小（queue_size ）默认是1000，当这队列满了的时候。它会根据定好的等待时间（wait_time，默认是60秒）来调用io线程，如果没有执行就会报错。

threadpool:   
    index:   
        type: blocking   
        min: 1   
        size: 30   
        wait_time: 30s  

6:服务器端运行插件
如果是在服务器上就可以使用elasticsearch-servicewrapper这个es插件，它支持通过参数，指定是在后台或前台运行es，并且支持启动，停止，重启es服务（默认es脚本只能通过ctrl+c关闭es）。使用方法是到https://github.com/elasticsearch/elasticsearch-servicewrapper下载service文件夹，放到es的bin目录下。下面是命令集合：
bin/service/elasticsearch +
console 在前台运行es
start 在后台运行es
stop 停止es
install 使es作为服务在服务器启动时自动启动
remove 取消启动时自动启动

在service目录下有个elasticsearch.conf配置文件，主要是设置一些java运行环境参数，其中比较重要的是下面的

参数：

#es的home路径，不用用默认值就可以
set.default.ES_HOME=<Path to ElasticSearch Home>

#分配给es的最小内存
set.default.ES_MIN_MEM=256

#分配给es的最大内存
set.default.ES_MAX_MEM=1024

默认JAVA HEAP大小为1G，根据你的服务器环境，需要自行调整，一般设置为物理内存的50%.
set.default.ES_HEAP_SIZE=1024


# 启动等待超时时间（以秒为单位）
wrapper.startup.timeout=300

# 关闭等待超时时间（以秒为单位）

wrapper.shutdown.timeout=300

# ping超时时间(以秒为单位)

wrapper.ping.timeout=300

10:FAQ
1) 使用了RangeFilter来检索，我看Query里边也有RangeQuery，那这两种有什么区别呢 ？
什么使用Filter 什么时候使用Query呢 ？

filter不参与搜索打分,单纯过滤掉不符合条件的,并且有缓存,filter比query效率高

2) 对于解析searchResponse hits() 和faces() 的区别是什么？

hits就是根据搜索条件得到的搜索结果,facets是分组统计

3) term query 与 field query 有什么区别吗

term query是词项查询，就是要完全匹配这个词的结果才会出来。
field query会对查询语句进行分词，也可以设置相应的分词器

4) 如果不是同一个网段的，怎么样为集群添加节点呢？

可以设置discovery.zen.ping.unicast.hosts来设置主节点从而加入集群

11:源码1.1.0
1）TypeParsers 类解析store, index, multi_field等类型的代码.可查看属性值有哪些


java 远程调试

1) java 远程 debug (适用于内网测试环境，因为需要重启，而且断点的位置会阻塞)
在远程启动脚本中加入 （tomcat在启动服务中添加）
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

本地eclipse 在debug中remote java application设置远程地址和端口，工程。并且在需要的地方打断点。

2) 外网环境使用 btrace


