# Describe
This project is used to reproduce the IllegalReferenceCountException in spring cloud gateway, which consists of 4 modules.

### error-provider
Provide a http interface with the path “/err”, which will sleep for 1s before returning the request.
```java
    @PostMapping("/err")
    public String error(@RequestBody String body) throws InterruptedException {
        Thread.sleep(1000);
        return body;
    }
```
### normal-provider 
Provide a http interface with the path “/hello”, which will return immediately.
```java
    @PostMapping("/hello")
    public String hello(@RequestBody String body) {
        return body;
    }
```

### scg-app
A Spring Cloud Gateway application, which will route requests to the error-provider and normal-provider. More information can be found in [RouteLocatorConfig.java](scg-app%2Fsrc%2Fmain%2Fjava%2Forg%2Fexample%2Fscgapp%2Fconfig%2FRouteLocatorConfig.java).<br>

In the application.properties file, the response timeout is set to 500ms, so that "error" route will timeout
```properties
spring.cloud.gateway.httpclient.response-timeout=500ms
```

### jemter-consumer
An embedded JMeter program that sends requests to scg-app.

It contains a test plan that uses 50 threads running continuously for 30s, sending requests to http://localhost:8080/hello; and 50 threads running continuously for 30s, sending requests to http://localhost:8080/err.

# how to reproduce
1. use ide to start normal-provider, error-provider, scg-app.
2. start JMeter test plan
```bash
# The current JMeter test plan will continue to execute for 30 second and then end automatically
# The specific error message stack can be seen in the scg-app console during execution
./jmeter-consumer/apache-jmeter-5.6/bin/jmeter -n -t ./jmeter-consumer/illegal-ref.jmx -l ./jmeter-consumer
```

# more information
1. when "error" route is using stringModifyRequestFilter, "hello" route will get IllegalReferenceCountException 
2. when "error" route is using byteModifyRequestFilter, "hello" route will get normal response

**error message**
```text
2024-05-21 15:17:16.243 ERROR 97339 --- [ctor-http-nio-7] r.n.http.server.HttpServerOperations     : [610a6222-282, L:/127.0.0.1:8080 - R:/127.0.0.1:62795] Error finishing response. Closing connection

io.netty.channel.socket.ChannelOutputShutdownException: Channel output shutdown
	at io.netty.channel.AbstractChannel$AbstractUnsafe.shutdownOutput(AbstractChannel.java:650) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
	Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: 
Error has been observed at the following site(s):
	*__checkpoint ⇢ org.springframework.cloud.gateway.filter.WeightCalculatorWebFilter [DefaultWebFilterChain]
	*__checkpoint ⇢ HTTP POST "/hello" [ExceptionHandlingWebHandler]
Original Stack Trace:
		at io.netty.channel.AbstractChannel$AbstractUnsafe.shutdownOutput(AbstractChannel.java:650) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannel$AbstractUnsafe.handleWriteError(AbstractChannel.java:953) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannel$AbstractUnsafe.flush0(AbstractChannel.java:933) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.flush0(AbstractNioChannel.java:354) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannel$AbstractUnsafe.flush(AbstractChannel.java:895) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.DefaultChannelPipeline$HeadContext.flush(DefaultChannelPipeline.java:1372) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:921) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeFlush(AbstractChannelHandlerContext.java:907) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.flush(AbstractChannelHandlerContext.java:893) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.CombinedChannelDuplexHandler$DelegatingChannelHandlerContext.flush(CombinedChannelDuplexHandler.java:531) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.ChannelOutboundHandlerAdapter.flush(ChannelOutboundHandlerAdapter.java:125) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.CombinedChannelDuplexHandler.flush(CombinedChannelDuplexHandler.java:356) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:923) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeFlush(AbstractChannelHandlerContext.java:907) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.AbstractChannelHandlerContext.flush(AbstractChannelHandlerContext.java:893) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at reactor.netty.channel.MonoSendMany$SendManyInner$AsyncFlush.run(MonoSendMany.java:800) [reactor-netty-core-1.0.32.jar:1.0.32]
		at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:174) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:167) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:470) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:566) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) [netty-common-4.1.92.Final.jar:4.1.92.Final]
		at java.lang.Thread.run(Thread.java:748) [na:1.8.0_312]
Caused by: io.netty.util.IllegalReferenceCountException: refCnt: 0
	at io.netty.buffer.AbstractByteBuf.ensureAccessible(AbstractByteBuf.java:1454) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.buffer.AbstractByteBuf.checkIndex(AbstractByteBuf.java:1383) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.buffer.PooledByteBuf.duplicateInternalNioBuffer(PooledByteBuf.java:197) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.buffer.PooledByteBuf.nioBuffer(PooledByteBuf.java:214) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.buffer.PooledSlicedByteBuf.nioBuffer(PooledSlicedByteBuf.java:89) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.buffer.AbstractPooledDerivedByteBuf.internalNioBuffer(AbstractPooledDerivedByteBuf.java:138) ~[netty-buffer-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.ChannelOutboundBuffer.nioBuffers(ChannelOutboundBuffer.java:457) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.socket.nio.NioSocketChannel.doWrite(NioSocketChannel.java:399) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.flush0(AbstractChannel.java:931) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.flush0(AbstractNioChannel.java:354) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.flush(AbstractChannel.java:895) ~[netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.flush(DefaultChannelPipeline.java:1372) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:921) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush(AbstractChannelHandlerContext.java:907) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.flush(AbstractChannelHandlerContext.java:893) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.CombinedChannelDuplexHandler$DelegatingChannelHandlerContext.flush(CombinedChannelDuplexHandler.java:531) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.ChannelOutboundHandlerAdapter.flush(ChannelOutboundHandlerAdapter.java:125) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.CombinedChannelDuplexHandler.flush(CombinedChannelDuplexHandler.java:356) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:923) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush(AbstractChannelHandlerContext.java:907) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.AbstractChannelHandlerContext.flush(AbstractChannelHandlerContext.java:893) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at reactor.netty.channel.MonoSendMany$SendManyInner$AsyncFlush.run(MonoSendMany.java:800) [reactor-netty-core-1.0.32.jar:1.0.32]
	at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:174) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:167) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:470) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:566) [netty-transport-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:997) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) [netty-common-4.1.92.Final.jar:4.1.92.Final]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_312]

```