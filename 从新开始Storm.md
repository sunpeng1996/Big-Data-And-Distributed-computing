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


## 第四章：Topology的并行度 ##
1.在Storm中，真正运行Topo的主要有三个实体：工作进程（Worker）、Executor(线程)和任务。

2.并行度：在Storm中，并行度描述了工作进程数据，以及Executor的数量和Storm中的任务数量。
    
    目前Stormd的配置优先级如下：
    default.yaml < storm.yaml < Topology的具体配置 < 内部组件的具体配置 < 外部组件的具体配置

3.Storm的一个非常好的功能就是：你可以动态地去增加或者减少工作进程数或者Executor数量，而不用重启集群或者Topology，这叫做rebalancing。

![](http://static.open-open.com/lib/uploadImg/20160202/20160202214850_441.png)



## 第五章：消息的可靠处理 ##

1.Storm可以确保Spout发送出来的每个消息都被完整地处理。

2.一个消息从Spout中发送出来，可能导致成百上千个消息基于此消息而创建。这些消息形成一个树状结构，我们称之为“Tuple Tree”。

![](http://img.blog.csdn.net/20141202192541361?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc3VpZmVuZzMwNTE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)


3.那么什么条件下，Storm才会认为一个从Spout发送出来的消息被完整地处理了呢？
	
- Tuple Tree不在生长；
- Tuple Tree中的任何消息都被标记为“已处理”；

4.Storm使用open方法中提供的Collector向他的输出流中发送一个或者多个消息，每发送一个消息，Spout都会给这个消息提供一个id，它将被用于标示这个消息。

 当检测到一个消息的衍生出来的Tuple Tree被完整地处理后，Storm会调用Spout中的ack方法，将此消息的id作为参数传入；同理，如果某消息处理超时，则此消息对用的Spout的fail方法就会被调用，传入的参数为此消息的id。
    
    注意：一个消息只会由发送它的那个Spout任务调用ack或fail，绝不会由其他的Spout任务来应答。

5.锚定：为tuple tree中指定的节点增加一个新的节点，我们称之为锚定。锚定是在我们发送消息的同时进行的。

6.Storm系统中有一组叫做“acker”的特殊任务，它负责跟踪DAG中的每个消息。
而系统使用一种哈希算法根据Spout消息的id确定由哪个acker跟踪此消息派生出来的tuple tree。因为每个消息都知道与之相对应的根消息的id,因此它知道应该与哪个acker通信。

7.acker任务保存了Spout消息id到一对值的映射。第一个值就是Spout任务的id，通过这个id，acker就知道消息处理完成时该通知哪个Spout任务；第二个值是一个64bit的数字，称为“ack val”，它是树中所有消息随即id的异或结果。ack val代表了整棵树的状态。

8.Supervisor和Nimbus是无状态的，因此这两种节点的失败不会影响当前正在运行的任务，只要及时将它重新启动即可。


## 第六章：一致性事物 ##

1.Storm是一个分布式的流处理系统，利用锚定和ack机制保证所有的tuple都被成功处理。但是如何保证出错的tuple只被处理一个呢？Storm提供了Transactional Topology，用来解决这个问题。

2.简单设计①：强顺序流，就是将tuple流变成强顺序的，并且每次处理一个tuple。将tuple id和1（1为已经处理）形成一个对照关系存储到数据库中，当数据库中存贮的id和当前tuple id不同时，数据库的消息总数加1，同事更新数据库的值，否则这条消息已经被处理。但是这种机制无法实现分布式计算。

3.简单设计②：强顺序batch流，每次处理一组tuple，称为一个batch。一个batch内的tuple可以并行处理。类似简单设计已，只不过这次数据库中存储的是batch id。为了确保一个batch内的所有tuple都被处理完了，Storm提供了CoordinateBolt.但是，强顺序batch也无法做到分布式计算。

4.CoordinateBolt原理：

- 真正计算的Bolt外面封装了一个CoordinateBolt。我们将真正执行计算的bolt称为real bolt.

- CoordianteBolt记录两个值：有哪些task给我发送了tuple；以及我给哪些task发tuple.

- Real Bolt发出一个tuple后，其外层的CoordinateBolt会记录下来这个tuple发送给了哪个task.

- 等所有的tuple都发完，CoordinateBolt通过另一个特殊的Stream以emitDirect的方式告诉所有它发送tuple的task，它发送了多少tuple给这个task。下游的task会将这个数字和自己接受到的tuple的数字进行对比，如果相等，则说明处理完了所有的tuple。

以此类推，下游的CoordinateBolt会重复上述步骤。


5.Transactional Topology

Storm提供的Transactional Topology将batch计算分为两个阶段，process和commit阶段。process阶段可以同时处理多个batch，不用保证其顺序性；而commit阶段保证batch的强顺序性，并且一次只能处理一个batch。


图晚些再奉上......



## 第七章：Trident的特性 ##



1. Trident是对Storm的更高一层的抽象,除了提供一套简单易用的流数据处理API之外，它以batch(一组tuples)为单位进行处理，这样一来，可以使得一些处理更简单和高效。



2.  我们知道把Bolt的运行状态仅仅保存在内存中是不可靠的，如果一个node挂掉，那么这个node上的任务就会被重新分配，但是之前的状态是无法恢复的。因此，比较聪明的方式就是把storm的计算状态信息持久化到database中，基于这一点，trident就变得尤为重要。因为在处理大数据时，我们在与database打交道时通常会采用批处理的方式来避免给它带来压力，而trident恰恰是以batch groups的形式处理数据，并提供了一些聚合功能的API。Trident也支持存储到其他介质如数据库、Memcached等处。
3.  Trident提供了事物支持，由于数据是按批发送到节点上的，Trident对每批数据都分配了一个transaction id。将这批数据和transaction id同时存储到某些介质中（如Memcached中）。Trident会比较memcached中的transaction id 和新到达数据的transaction id，如果同一批数据被重复发送，那么transaction id就会等于Memcached中记录的transaction id。
4.  Trident API 实践

 [http://www.bubuko.com/infodetail-467560.html](http://www.bubuko.com/infodetail-467560.html)


5.总结：   Storm是一个实时流计算框架，Trident是对storm的一个更高层次的抽象，Trident最大的特点以batch的形式处理stream。