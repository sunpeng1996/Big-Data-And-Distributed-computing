# Storm学习笔记 #
http://ifeve.com/getting-started-with-storm-1/<br>
主要参照并发编程网的《Getting Started With Storm》译文。

## 2016.06.03 ##
	
 在Storm集群中，有两类节点：主节点master node和工作节点worker nodes。主节点运行着一个叫做Nimbus的守护进程。这个守护进程负责在集群中分发代码，为工作节点分配任务，并监控故障。Supervisor守护进程作为拓扑的一部分运行在工作节点上。一个Storm拓扑结构在不同的机器上运行着众多的工作节点。


## 2016.06.06 ##
Trident(简介)


   Trident是对Storm的更高一层的抽象,除了提供一套简单易用的流数据处理API之外，它以batch(一组tuples)为单位进行处理，这样一来，可以使得一些处理更简单和高效。

----------

   我们知道把Bolt的运行状态仅仅保存在内存中是不可靠的，如果一个node挂掉，那么这个node上的任务就会被重新分配，但是之前的状态是无法恢复的。因此，比较聪明的方式就是把storm的计算状态信息持久化到database中，基于这一点，trident就变得尤为重要。因为在处理大数据时，我们在与database打交道时通常会采用批处理的方式来避免给它带来压力，而trident恰恰是以batch groups的形式处理数据，并提供了一些聚合功能的API。

[http://blog.csdn.net/derekjiang/article/details/9126185#comments](http://blog.csdn.net/derekjiang/article/details/9126185#comments "[Trident] Storm Trident 教程")


 