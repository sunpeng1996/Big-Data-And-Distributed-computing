# 这里记录了Storm的学习记录，是本人一字字打出来的，希望可以学的更扎实！ #

## 第一章：Storm概述 ##

1.Storm是一个分布式流式计算的结构，目前阿里巴巴（淘宝）的大数据主要采用了这种技术，以Strom为基础，封装、改进抽象出来一款自己的分布式计算模式。Storm有很多应用场景，如实时分析、在线机器学习、持续计算、分布式RPC、ETL等等。

2.Strom分布式计算结构称为topology(拓扑)，由stream(数据流),spout(数据流的生成者),和bolt(运算)组成。如果你学过hadoop的话，topo大致等同于hadoop中的Job。然而，对于topo来说，只要你没有取消部署和杀死进程，topo就会一直运行下去。

3.

- stream的核心数据结构是tulpe。tuple是包含了一个或者多个键值对的列表。
- spout代表了一个Storm Topology的主要数据入口，充当采集器的角色，连接到数据源，将数据转化成一个一个的tuple，将tuple作为数据流进行发射。
- bolt是运算过程或者函数，将数据流作为输入，对数据实施某些运算或者操作，然后选择性地输出数据流。bolt可以订阅多个由spout或者其他bolt发射的数据流，这样就可以建立起数据流转换网络。



4.Storm有如下特性：

- 编程模型简单
- 可扩展
- 高可靠性
- 高容错性
- 支持多种编程语言
- 支持本地模式
- 高效
- 运维和部署简单
- 图形化监控

## 第二章：Storm初体验 ##

1.Storm是apache下的项目，去Apache官网下载即可。Storm提供本地模式，允许用户将Topology提交到本地集群，所有的bolt、spout跑在一个进程内，能够很方便地对topo进行调试。

2.说一下Storm的本地集群：
Storm集群包含两类结点：主控节点（Master Node）和工作节点（Worker Node）;

- (1)主控节点上运行着一个称为Nimbus的后台程序，它负责在storm集群上分发代码、分配任务并负责监控集群的运行状态；
- (2)每个工作节点上运行着一个称为Supervisor的后台程序。Supervisor负责监听Nimbus分配给它执行的任务。每一个工作进程执行一个Topology的子集，一个运行中的Topo由分布在不同工作节点上的多个工作进程组成。

3.Nimbus和Supervisor节点之间的所有协调工作是通过Zookeeper集群来实现的。如下图所示：
![图片](http://www.tools138.com/create/images/20151101/020012064.jpg)

4.此外Nimbus和Supervisor都是快速失败和无状态的，包括zookeeper也是快速失败的。这个设计让Storm集群拥有不可思议的稳定性。


##第三章： 构建Topology ##


1.Topologies:通过Stream Groupings 将spout和bolt连接起来构成一个Topology，如图所示。


![](http://image.lxway.com/upload/6/77/6773742c1ef20efbea011cab7a888626.jpg)

2.Streams：消息流是Stream是Storm里的关键抽象，一个消息流是一个没有边界的tuple序列，而这些tuple序列会以一种分布式的方式并行地创建和处理。通过对Stream中的tuple的每个字段命名来定义Stream。


3.Spout：消息源spout是Storm里面一个Topology中的消息生产者。一般来说，消息源会从外部源读取数据并且向Topo里面发出消息tuple。Spout可以是可靠的，也可以是不可靠的。加入这个tuple没有被Storm成功处理，那可靠的消息源Spout可以重新发送一个tuple，但是不可靠的消息源就不能发送了。

 Spout里面最重要的方法是nexttuple()，要么发射一个新的tuple到Topo里面，要么简单地返回，
另外两个主要的方法是act和fail，Storm在检测到一个tuple被整个Topo成功处理的时候调用ack，否则调用fail。Storm只对可靠的Spout调用ack和fail。

4.Bolts：所有消息处理逻辑都被封装到Bolts里面。Boltes可以做很多事，如过滤、聚合、查询数据库，等等。
Bolts可以发射多条数据流，使用OutputFieldsDeclarer.declareStream定义Stream，使用OutputCollector.emit选择要发射的Stream。

Bolts的主要方法是execute，它以一个tuple作为输入，发射0个或者多个tuple，然后调用ack方法通知Storm自己已经处理过这个tuple了。

5.Stream Groups：用来定义一个Stream应该如何分配数据给Bolts上面的多个tasks的。我认为，就是让一个bolt如何将数据分发到下一级的bolt。Stream提供了7中类型的Stream Grouping.

1. Shuffle Grouping
1. Fields Grouping
1. All Grouping 
1. Golbal Grouping
1. Non Grouping
1. Direct Grouping
1. Local or Shuffle Grouping


6.Reliability：Storm保证每个tuple都被Topo完整地执行，Storm会追踪由每个tuple 所产生的Tuple树，每个topo都有一个消息超时的设置，如果Storm在这个超时的时间内检测不到某个tuple树有没有执行成功，那么这个topo会把这个tuple标记为执行失败，并且过一会重新发送这个tuple。

7.Tasks：每一个spout和bolt都被会当做task在整个集群里面执行，每一个executor是一个线程，在这个线程中可以执行多个tasks。可以调用TopologyBuilder类的setSpout和setBolt设置并行度（也就是有几个tasks）。

8.Workers：一个Topology可能会存在一个或者多个Worker（工作进程）里面执行，每个worker是一个物理JVM并且执行topo的一部分。比如并行度是300的Topo，如果我们使用50个工作进程来执行这个Topo,那么每个工作进程会处理大约6个tasks。

9.Configuration：Storm里面有一大堆参数可以配置Storm的某些属性设置，调整Nimbus和Supervisor。