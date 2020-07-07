基于https://github.com/yanweiling/springcloud_ticket.git

**Axon实例3**

基于Axon的spirng cloud购票系统
功能：
    
    创建用户，创建票，实现用户购票流程
    微服务系统：
    
        user服务，ticket服务
        order-command服务、order-query服务
        saga流程管理
        

基于Axon的分布式Event Sourcing-Command

    command不在本地处理，发送到某个实例
    关联同一个聚合ID的command会被始终发到同一个实例去处理
    Axon提供spring cloud和JGroups的command分发
    Axon收到command后再Aggregate对象上处理command
    
Axon分布式Command分发

![image]( https://note.youdao.com/yws/public/resource/354d8215ba83a7c5c8c20782b614e896/xmlnote/A1B7FE2EBAEA4E178B7CABCA83AD9FF0/30764) 

**CH:Command Handler**

    Connector提供实例间连接，Axon提供多种实现，有spring cloud（微服务系统），JGroup（分布式系统）
    
    Routor决定某个Command发到哪个实例上
    
   
基于Axon分布式Event Sourcing-Event

    Aggregate对象上的Event Handler会被本地调用
    Event被发布到AMQP队列
    Event Processor从队列读取并调用关联的Event Handler
    Event会在每个服务的数据库中保存，只要它处理过该事件
    
![image](https://note.youdao.com/yws/public/resource/354d8215ba83a7c5c8c20782b614e896/xmlnote/A48CE815AB5B4B519BBEB27FBBB7E8BA/30778)

    1.服务1中由command handler去触发一个event handler，如果在我本地Aggregate上对这个event注册了event handler的话，它会通过Event Bus去调用Aggregate上的Event Handler；
    
    2.调用完以后，会将event保存到本3地服务的domain_event_entry中
    
    3.然后通过AMQP_Publisher将这个event发布到AMQP的队列中
      AMQP中有exchange 、bounding 和queue，由bounding来确认该消息要存放到哪一个队列上
    4.服务2中由Message Source监听某个队列，当监听到消息以后，由Event Processor来找到这个消息中的event关联的Event Handler是谁，并调用此Event Handler；
    5.然后由服务2中的Event Handler去处理，并将event保存到本地服务的Event Sore中（domain_event_entry）
    
## Spring Cloud Axon购票系统设计
![image](https://note.youdao.com/yws/public/resource/354d8215ba83a7c5c8c20782b614e896/xmlnote/CC343363FFDD4F5EBA5D75D0F2A6C75C/30810)

**基于Axon分布式Event设计**
    
    聚合类上的Event Handler是本地处理，不通过AMQP触发
    聚合类上市处理完Event，再通过AMQP分发
    每个服务监听各自的队列
    每个事件只会被处理一次
    每个EventHandler处理过的Event，都会保存在该服务的数据库中
    
    ---
    Saga负责流程编排、Aggregate用于处理数据状态
    Saga要处理该流程内的所有事件
    Saga处理的事件不会保存在domain_event表中
    每个Saga对象也会序列好后保存在数据库中
    Saga所在的服务可以实现分布式部署
    
    
### 服务-队列-事件的关系图

![image](https://note.youdao.com/yws/public/resource/354d8215ba83a7c5c8c20782b614e896/xmlnote/1EA15599A1C946DF98BA6C6528AECEB7/30833)


----
## Axon框架事务实现

    UnitOfWork同步事务
    聚合对象内处理Command和Event时线性处理
    聚合对象和Entity共用时，使用for update锁对象
    saga处理Event时使用聚合ID和序号避免并发
    
    
    ----
    说明：
    Axon中的每一个CommandHandler和EventHandler都是在一个UnitOfWork中处理的；UnitOfWork我们可以看成是执行的一个逻辑单元。
    当一个CommandBus处理一个Command的时候，它会开启一个UnitOfWork，在这里面执行各种拦截器，最后执行这个command的commandHandler的方法。
    
    一般情况下，我们在CommandHandler中会触发EventHandler，相应的开启这个EventBus执行这个处理过程，当EventBus在处理Event的时候，它又会开启一个UnitOfWork。也就是说当一个Command开启多个event的时候，一个command 的unitOfWork中就会开启多个event的UnitOfWork，而这些unitOfWork都会在同一个事务中去执行。
    内部的evnet的unitOfWork的事务都会关联到外部的command的事务上去。
    
    另一个方面，这个聚合对象处理commandHandler和eventHandler的时候，它是线性处理的。当一个聚合对象开启执行某一个comandHandler的时候，会从资源库中获取这个聚合对象，然后会lock这个聚合对象，所有一个聚合对象中的多个commandHandler执行是依次，线性执行的。
    特殊：当聚合对象和Entity共用的时候，锁的方式是采用for update
    
    saga处理event的时候，它会使用聚合对象的ID和序列号，处理之前获取这个event，这样就知道下一个新生成的event的序列号是什么，所以当保存event的时候，就把聚合对象ID和序列号作为唯一索引保存，避免并发问题

---
Axon框架事务实现--UnitOfWork

    处理器执行的单元是UnitOfWork
    UnitOfWork与一个事务关联
    同步处理event和command时，在同一个事务中处理 
    
---
测试：
1.新增user

    post
    http://localhost:8888/user/customers?name=liuxinzhou3&password=123456
    
2.充值

    put
    http://localhost:8888/user/customers/返回的userid/deposit/100

3.新增票
    
    post
    http://localhost:8888/ticket/tickets?name=测试
4.订单

    post
    http://localhost:8888/order/orders
    {
    	"titile":"测试订17单",
    	"ticketId":"d0bb8e07-906b-4744-bfc6-f263af89f80d",
    	"customerId":"fe508ac0-f4ab-4bae-9931-61df0f6b2d3c",
    	"amount":5
    }