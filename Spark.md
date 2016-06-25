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


##第三章：RDD编程##

1.Spark对数据的核心抽象---弹性分布式数据集（RDD），RDD其实就是分布式的元素集合。在spark中，对数据的所有操作不外乎创建RDD、转化已有的RDD以及调用RDD操作进行求值！而这一切的背后，Spark会自动将RDD中的数据分发到集群上，并将操作并行化执行。

2.Spark中的RDD就是一个不可变的分布式对象集合，用户可以通过两种方式创建RDD：

- 读取一个外部数据集
- 在驱动器程序里分发驱动器程序中的对象集合

    附另一种解释方法：
	Spark的核心概念是弹性分布式数据集（RDD），RDD是一个可容错、可并行操作的分布式元素集合。总体上有两种方法可以创建RDD对象：由驱动程序中的集合对象通过并行化操作创建，或者从外部存储系统中数据集加载（如：共享文件系统、HDFS、HBase或者其他Hadoop支持的数据源）。

3.RDD支持两种类型的操作：转化操作（transformation）和行动操作（action）。转化操作会由一个RDD生成一个新的RDD。另一方面，行动操作会对RDD计算出一个结果，并把结果返回到驱动器程序中，或把结果存储到外部存储系统（如HDFS中）。例如，map是一个transformation操作，它将数据集中每个元素传给一个指定的函数，并将该函数返回结果构建为一个新的RDD；而 reduce是一个action操作，它可以将RDD中所有元素传给指定的聚合函数，并将最终的聚合结果返回给驱动器（还有一个reduceByKey操作，其返回的聚合结果是一个数据集）。

转化操作和行动操作的区别在于Spark计算RDD的方式不同。Spark中的所有转化操作都是懒惰的，Spark会惰性计算这些RDD，他们只有在第一次在一个行动操作中用到时，才会真正计算。

如果想在多个行动操作中重用一个RDD，可以使用RDD.persist()让spark把这个RDD缓存下来，我们可以将spark缓存到许多不同的地方，之后就可以重用这些数据了，可以缓存到内存、硬盘等等。

4.创建一个RDD最简单的方法就是将程序中的一个已有的集合传给sc.parallelize()方法，如下：

    val lines  = sc.parallelize(List("hello","i am sunpeng"))
	//scala语法

更常用的方式是从外部存储中读取数据来创建RDD，最简单的如下：
    val lines = sc.textFile("/path/README.md")
	//scala语法

5.RDD操作：

