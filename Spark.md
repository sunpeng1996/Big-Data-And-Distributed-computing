# 这里记录了Spark的学习记录，是本人一字字打出来的，希望可以学的更扎实！ #

## 第零章：Spark概述 ##

关键词：引擎、数据处理/数据分析

1.MapReduce 与 Spark 相比，有哪些异同点？

-  基本原理上：<br>
a) MapReduce：基于磁盘的大数据批量处理系统<br>
b) Spark：基于 RDD（弹性分布式数据集）数据处理，显式的将RDD 数据存储到磁盘和内存中。

-  模型上：<br>
a) MapReduce：可以处理超大规模的数据，适合日志分析挖掘等
较少的迭代的长任务需求； 结合了数据的分布式的计算。<br>
b) Spark：适合数据的挖掘， 机器学习等多伦迭代式计算任务

- 容错性上：<br>
a） 数据容错性<br>
b） 节点容错性

2.Spark实际开发中运用scala语言比较多。学习spark的基础<br>
	
- java
- 了解hadoop
- scala
- 多看官方文档！

##第一章：Spark数据分析导论##

1.一套大数据解决方案通常包含多个重要组件，从存储、计算和网络等硬件层，到数据处理引擎，再到利用改良的设计和计算算法、数据可视化来获得商业洞见的分析层。可以说，数据处理引擎之余大数据就像CPU之于计算机，或大脑之于人类。

2.Spark允许用户将数据加载到集群内存中用于反复查询，非常适用于大数据和机器学习，日益成为最广泛的采用的大数据模块之一。

3.Spark是什么？

- Spark是一个用来实现快速而通用的集群计算的平台。

Spark软件栈：

- Spark core
- Spark SQL
- Spark Streaming
- MLlib
- GraphX


## 第二章：Spark下载与入门 ##

1.Spark可以通过Python、Java或Scala来使用。去官网上下载已经编译好的spark版本即可。（Pre-build for Hadoop 2.x and later）。

2.Spark带有交互性的shell，可以作即使数据分析。我们可以通过./bin/spark-shell来启动scala脚本交互式；也可以通过./bin/pyspark来启动Python脚本交互式端口。

3.RDD：在Spark中，我们通过对分布式数据集的操作来表达我们的计算意图，这些计算会自动地在集群上并行进行。这样的数据集被称为弹性分布式数据集（resilient distributed dataset），简称RDD。RDD是spark对分布式数据和计算的基本抽象。

4.当运行脚本启动Spark后，我们可以通过UI界面的方式来访问spark，和Hadoop、storm类似，在浏览器上输入localhost:4040,即可。

5.Spark核心基本概念：

从上层来看，每一个Spark应用都有一个驱动器程序（driver program）来发起集群上的各种并行操作。 驱动器程序包括应用的main函数，并且定义了集群上的分布式数据集，还对这些分布式数据集应用了相关操作。在前面的例子里，驱动器程序就是spark shell 本身，你只需要输入想要运行的操作就可以了。

驱动器程序通过一个SparkContext对象来访问spark，这个对象代表对计算集群的一个连接。shell脚本启动的时候已经自动创建了这样一个对象，是一个叫做sc的变量。一旦有了这个SparkContext，你就可以用它来创建RDD。

6.其实Spark API最神奇的地方就在于像filter这样基于函数的操作也会在集群上并行执行。也就是说，Spark会自动将函数（比如line.contains("Python")）发到各个执行器节点上，这样，你就可以在单一的执行器里编程，并且让代码自动运行在多个节点上。

7.除了交互式的方式访问spark，Spark也可以在Java、Scala或Python的独立程序中被连接使用。与在spark shell中使用的主要区别是你需要自行初始化SparkContext。

8.连接Spark的过程在各个语言中并不一样，在Java和scala中，只需要给应用配置maven依赖，然后Spark包，并且创建SparkContext对象就可以了。

    import org.apache.spark.SparkConf;
	import org.apache.spark.api.java.JavaSparkContext;

	SparkConf conf = new SparkConf().setMaster("local").setAppName("My APP");
	JavaSparkContext sc = new JavaSparkContext(conf);

上面通过一段简单的Java代码展示连接spark的方式，只需要传递两个参数：

- 集群URL
- 应用名

9.你可以在spark-shell中运行类似wordCount的程序，非常简单，执行./bin/spark-shell打开spark命令终端 。 





