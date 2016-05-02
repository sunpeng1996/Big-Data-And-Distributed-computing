# Memcached学习笔记 #

<h1>一、Memcached介绍
## 1.1、什么是Memcached？ ##
free&opensource,high-performance,distributedmemory objectcachingsystem<br/>
 自由&开放源码, 高性能 ,分布式的内存对象缓存系统 
由 livejounal 旗下的 danga 公司开发的老牌 nosql 应用.


## 1.2什么是 NoSQL?##
nosql，指的是非关系型的数据库。<br/> 
相对于传统关系型数据库的"行与列",NoSQL 的鲜明特点为 k-v 存储(memcached,redis), 或基于文档存储(mongodb).

> 注: nosql--notonlysql, 不仅仅是关系型数据库, 显著特点:key-value 键值对存储,如 memcached,redis, 或基于文档存储 如,mongodb

<h1>二、Memcached基本使用
## 2.1 linux 下编译 memcached##
2.1.1:准备编译环境<br>
在 linux 编译,需要 gcc,make,cmake,autoconf,libtool 等工具.<br>
在 linux 系统联网后,用如下命令安装<br> 
> yum install gcc make cmake autoconf libtool

## 2.2 memcached 的启动 ##




## 2.3 memcached 的连接 ##
memcached 客户端与服务器端的通信比较简单,使用的基于文本的协议,而不是二进制协议.<br>
 (http 协议也是这样), 因此我们通过 telnet 即可与 memcached 作交互.<br>
另开一个终端,并运行 telnet 命令 (开启 memcached 的终端不要关闭) 

> 格式 telnet host port<br>
>  telnet localhost 11211 <br>
>  Trying ::1... Connected to localhost. <br>
>  Escape character is '^]'.


## 2.4 memcached 的命令 ##
 增: add 往内存增加一行新记录<br>
 语法:addkeyflagexpire length 回车<br>
key 给值起一个独特的名字<br>
 flag 标志,要求为一个正整数<br>
 expire 有效期 length 缓存的长度(字节为单位)<br>


flag 的意义:<br>
 memcached 基本文本协议,传输的东西,理解成字符串来存储.<br>
 想:让你存一个 php 对象,和一个 php 数组,怎么办?<br>
 答:序列化成字符串,往出取的时候,自然还要反序列化成 对象/数组/json 格式等等. <br>
这时候,flag 的意义就体现出来了. 比如,1 就是字符串,2 反转成数组 3,反序列化对象.....<br>



expire 的意义: 设置缓存的有效期,有 3 种格式<br>
 1:设置秒数, 从设定开始数,第 n 秒后失效.<br>
 2:时间戳, 到指定的时间戳后失效. 比如在团购网站,缓存的某团到中午 12:00 失效.addkey013792099996<br>
 3: 设为 0. 不自动失效.

 delete 删除 deletekey [time seconds] 删除指定的 key.<br>
 如加可选参数 time,则指删除 key,并在删除 key 后的 time 秒内,不允许 get,add,replace 操作此 key.<br>

replace 替换 replacekeyflagexpire length 参数和 add 完全一样,不单独写.<br>

get 查询 get key 返回 key 的值<br>
set 是设置和修改值 参数和 add,replace 一样,但功能不一样. <br>
用 add 时,key 不存在,才能建立此键值.<br>
但对于已经存在的键,可以用 replace 进行替换/更改<br>
repalce,key 存在时,才能修改此键值,如上图,date 不存在,则没改成功.<br>
而 set 想当于有 addreplace 两者的功能. setkeyflagexpireleng 时 如果服务器无此键 ----> 增加的效果 如果服务器有此键 ----> 修改的效果.<br>

 incr,decr 命令:增加/减少值的大小 语法: incr/decrkeynum <br>

-  应用场景------秒杀功能, 一个人下单,要牵涉数据库读取,写入订单,更改库存,及事务要求, 对于传统型数据库来说, 压力是巨大的. 可以利用 memcached 的 incr/decr 功能, 在内存存储 count 库存量, 秒杀 1000 台 每人抢单主要在内存操作,速度非常快, 抢到 count<=1000 的号人,得一个订单号,再去另一个页面慢慢支付.


 统计命令:stats 把 memcached 当前的运行信息统计出来 

 flush_all 清空所有的存储对象

<h1>三、 memcached 的内存管理与删除机制<h1/>

# 3.1:内存的碎片化 #

如果用 c 语言直接 malloc,free 来向操作系统申请和释放内存时, 在不断的申请和释放过程中,形成了一些很小的内存片断,无法再利用. 这种空闲,但无法利用内存的现象,---称为内存的碎片化.
# 3.2: slaballocator 缓解内存碎片化 #
memcached 用 slaballocator 机制来管理内存.<br>
 slaballocator 原理: 预告把内存划分成数个 slabclass 仓库.(每个 slabclass 大小 1M) <br>
各仓库,切分成不同尺寸的小块(chunk). 需要存内容时,判断内容的大小,为其选取合理的仓库.<br>
如下图：

# 3.3:Slab Allocator缓存原理 #
memcached根据收到的数据的大小，<br>
选择最适合数据大小的slab。memcached中保存着slab内空闲chunk的列表，<br>
根据该列表选择chunk，然后将数据缓存于其中。

> 警示: 如果有 100byte 的内容要存,但 122 大小的仓库中的 chunk 满了 并不会寻找更大的,如 144 的仓库来存储, 而是把 122 仓库的旧数据踢掉! 详见过期与删除机制.

# 3.4 固定大小 chunk 带来的内存浪费 #
由于 slaballocator 机制中, 分配的 chunk 的大小是”固定”的,<br>
 因此, 对于特定的 item,可能造 成内存空间的浪费. 比如, 将 100 字节的数据缓存到 122 字节的 chunk 中, 剩余的 22 字节就浪费了.<br>
对于 chunk 空间的浪费问题,无法彻底解决,只能缓解该问题.<br>
 开发者可以对网站中缓存中的item的长度进行统计,并制定合理的slabclass中的chunk的大小. 可惜的是,我们目前还不能自定义 chunk 的大小,但可以通过参数来调整各 slabclass 中 chunk 大小的增长速度. 即增长因子,grow factor!


# 3.5 grow factor 调优 #
memcached 在启动时可以通过­f 选项指定 Growth Factor 因子, 并在某种程度上控制 slab 之间的差异. 默认值为1.25. 但是,在该选项出现之前,这个因子曾经固定为2,称为”powersof2” 策略。 我们分别用 grow factor 为 2 和 1.25 来看一看效果:<br>
     >memcached ­f 2 ­vvv<br>
    slab class 1: chunk size 128 perslab 8192 <br>
	slab class 2: chunk size 256 perslab 4096 <br>
	slab class 3: chunk size 512 perslab 2048 <br>
	slab class 4: chunk size 1024 perslab 1024 <br>
	 .... ..... <br>
	slab class 10: chunk size 65536 perslab 16<br>
	 slab class 11: chunk size 131072 perslab 8<br>
	 slab class 12: chunk size 262144 perslab 4 <br>
	slab class 13: chunk size 524288 perslab 2 <br>
	可见，从 128 字节的组开始，组的大小依次增大为原来的 2 倍. <br>
	来看看 f=1.25 时的输出: >memcached -f 1.25 -vvv <br>
	slab class 1: chunk size 88 perslab 11915 <br>
	slab class 2: chunk size 112 perslab 9362 <br>
	slab class 3: chunk size 144 perslab 7281 <br>
	slab class 4: chunk size 184 perslab 5698 .... .... <br>
	slab class 36: chunk size 250376 perslab 4 <br>
	slab class 37: chunk size 312976 perslab 3 <br>
	slab class 38: chunk size 391224 perslab 2 <br>
	slab class 39: chunk size 489032 perslab 2

对比可知, 当 f=2 时, 各 slab 中的 chunksize 增长很快,有些情况下就相当浪费内存. 因此,我们应细心统计缓存的大小,制定合理的增长因子.<br>
注意: 当f=1.25时,从输出结果来看,某些相邻的slabclass的大小比值并非为1.25,可能会觉得有些 计算误差，这些误差是为了保持字节数的对齐而故意设置的.

# 3.6 memcached 的过期数据惰性删除 #

1: 当某个值过期后,并没有从内存删除,因此,stats 统计时,curr_item 有其信息<br>

2: 当某个新值去占用他的位置时,当成空 chunk 来占用. <br>

3: 当 get 值时,判断是否过期,如果过期,返回空,并且清空,curr_item 就减少了.<br>

即--这个过期,只是让用户看不到这个数据而已,并没有在过期的瞬间立即从内存删除. <br>
这个称为 lazyexpiration, 惰性失效. 好处--- 节省了 cpu 时间和检测的成本.

# 3.7: memcached 此处用的 lru 删除机制. #
如果以122byte大小的chunk举例,122的 chunk都满了, 又有新的值(长度为 120)要加入, 要挤掉谁?<br>
memcached 此处用的 lru 删除机制. (操作系统的内存管理,常用 fifo,lru 删除) <br>
lru:leastrecentlyused 最近最少使用<br>
 fifo:first in,first out<br>

原理: 当某个单元被请求时,维护一个计数器,通过计数器来判断最近谁最少被使用. 就把谁 t 出.<br>

> 注: 即使某个 key 是设置的永久有效期,也一样会被踢出来! 即--永久数据被踢现象

 <h1>四、分布式集群算法</h1>


# 4.1 memcached 如何实现分布式? #

在第 1 章中,我们介绍 memcached 是一个”分布式缓存”,然后 memcached 并不像 mongoDB 那样,允许配置多个节点,且节点之间”自动分配数据”. <br>
就是说--memcached 节点之间,是不互相通信的. 因此,memcached 的分布式,要靠用户去设计算法,把数据分布在多个 memcached 节点中.<br>


# 6.2 分布式之取模算法 #

最容易想到的算法是取模算法,即 N 个节点要,从 0->N-1 编号. key 对 N 取模,余 i,则 key 落在第 i 台服务器上.

# 6.3 取模算法对缓存命中率的影响 #
假设有 8 台服务器, 运行中,突然 down 一台, 则求余的底数变成 7 <br>
后果: 
> key0%8==0, key0%7 ==0 hits ....<Br>
>  key6%8==6, key6%7== 6 hits<br>
>   key7%8==7, key7%7==0 miss<br>
>    key9%8==1, key9%7 == 2 miss <br>
>    ...<br>
>     key55%8 ==7 key55%7 == 6 miss



一般地,我们从数学上归纳之: 有 N 台服务器, 变为 N-1 台, 每 N*(N-1)个数中, 只有(n-1)个单元,%N,%(N-1)得到相同的结果<br>


所以 命中率在服务器 down 的短期内, 急剧下降至 (N-1)/(N*(N-1)) = 1/(N-1) 所以: 服务器越多, 则 down 机的后果越严重!

# 6.4 一致性哈希算法原理 #


# 6.5 一致性哈希对其他节点的影响 #

# 6.6 一致性哈希+虚拟节点对缓存命中率的影响 #

理想状态下,<br>
 1) 节点在圆环上分配分配均匀,因此承担的任务也平均,但事实上, 一般的 Hash 函数对于节 点在圆环上的映射,并不均匀.<br>
 2) 当某个节点 down 后,直接冲击下 1 个节点,对下 1 个节点冲击过大,能否把 down 节点上的 压力平均的分担到所有节点上?
<br>
完全可以---引入虚拟节点来达到目标虚拟节点即----N 个真实节点,把每个真实节点映射成 M 个虚拟节点, 再把 M*N 个虚拟节点, 散列在圆环上. 各真实节点对应的虚拟节点相互交错分布 这样,某真实节点 down 后,则把其影响平均分担到其他所有节点上.


<h1> 五、memcached 经典问题或现象<h1>

# 5.1 缓存雪崩现象及真实案例 #

缓存雪崩一般是由某个缓存节点失效,导致其他节点的缓存命中率下降, 缓存中缺失的数据去数据库查询.短时间内,造成数据库服务器崩溃.<br>
重启 DB,短期又被压跨,但缓存数据也多一些.<br>
 DB 反复多次启动多次,缓存重建完毕,DB 才稳定运行.<br>
或者,是由于缓存周期性的失效,比如每 6 小时失效一次,那么每 6 小时,将有一个请求”峰值”, 严重者甚至会令 DB 崩溃.<br>


# 5.2 缓存的无底洞现象 multiget-hole #
该问题由 facebook 的工作人员提出的,facebook 在 2010 年左右,memcached 节点就已经达 3000 个.缓存数千 G 内容. 他们发现了一个问题---memcached 连接频率,效率下降了,于是加 memcached 节点, 添加了后,发现因为连接频率导致的问题,仍然存在,并没有好转,称之为”无底洞现象”.<br>
 原文见:<br>
 http://highscalability.com/blog/2009/10/26/facebooks-memcached-multiget-hole-more-machinesmore-capacit.html

## 5.2.1multiget-hole 问题分析 ##
以用户为例:user-133-age, user-133-name,user-133-height .....N 个 key, 当服务器增多,133 号用户的信息,也被散落在更多的节点, 所以,同样是访问个人主页,得到相同的个人信息, 节点越多,要连接的节点也越多. 对于 memcached 的连接数,并没有随着节点的增多,而降低. 于是问题出现.
## 5.2.2multiget-hole 解决方案: ##
把某一组 key,按其共同前缀,来分布. 比如 user-133-age,user-133-name,user-133-height 这 3 个 key, 在用分布式算法求其节点时,应该以 ‘user-133’来计算,而不是以 user-133-age/name/height 来 计算. 这样,3 个关于个人信息的 key,都落在同 1 个节点上,访问个人主页时,只需要连接 1 个节点. 问题解决. 官方回应:http://dormando.livejournal.com/521163.html
事实上: NoSQL 和传统的 RDBMS,并不是水火不容,两者在某些设计上,是可以相互参考的. 对于 memcached,redis 这种 kv 存储,key 的设计,可以参考 MySQL 中表/列的设计. 比如:user 表下,有 age 列,name 列,身高列, 对应的 key,可以用 user:133:age =23,user:133:name=‘lisi’,user:133:height =168;


# 5.3 永久数据被踢现象 #
网上有人反馈为"memcached 数据丢失",明明设为永久有效,却莫名其妙的丢失了.<br>
其实,这要从 2 个方面来找原因: 即前面介绍的 惰性删除,与 LRU 最近最少使用记录删除. 分析:<br>
 1:如果 slab 里的很多 chunk,已经过期,但过期后没有被get过, 系统不知他们已经过期.<br>
 2:永久数据很久没get了,不活跃,如果新增item,则永久数据被踢了.<br>
 3: 当然,如果那些非永久数据被 get,也会被标识为 expire,从而不会再踢掉永久数据.<br>

解决方案: 永久数据和非永久数据分开放













