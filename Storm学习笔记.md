# Storm学习笔记 #
http://ifeve.com/getting-started-with-storm-1/<br>
主要参照并发编程网的《Getting Started With Storm》译文。

## 2016.06.03 ##
	
 在Storm集群中，有两类节点：主节点master node和工作节点worker nodes。主节点运行着一个叫做Nimbus的守护进程。这个守护进程负责在集群中分发代码，为工作节点分配任务，并监控故障。Supervisor守护进程作为拓扑的一部分运行在工作节点上。一个Storm拓扑结构在不同的机器上运行着众多的工作节点。



 