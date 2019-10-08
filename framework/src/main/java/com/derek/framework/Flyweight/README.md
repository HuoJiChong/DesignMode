# 享元模式

享元模式是对象池的一种实现，它的英文名称叫做Flyweight，代表轻量级的意思。
享元模式用来尽可能减少内训使用量，它适用于可能存在大量重复创建对象的场景、来缓存可共享的对象，达到对象共享、避免创建过多对象的效果，
这样一来就可以提升性能、避免内存溢出等。

## 深入理解Android 的消息机制

Looper.loop() 消息循环

    public static void loop() {
        final Looper me = myLooper();
        
        final MessageQueue queue = me.mQueue;

        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();

        for (;;) {
            // 获取消息
            Message msg = queue.next(); // might block
            if (msg == null) {
                return;
            }

            msg.target.dispatchMessage(msg);

            // 消息回收，放到对象池中，使用链表的方式
            msg.recycle();
        }
    }

Message.next()

    final Message next() {
        int pendingIdleHandlerCount = -1; // -1 only during first iteration
        int nextPollTimeoutMillis = 0;

        for (;;) {
            // 处理Native事件
            nativePollOnce(mPtr, nextPollTimeoutMillis);

            synchronized (this) {
                if (mQuiting) {
                    return null;
                }
                // java层的消息队列
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = mMessages;
                if (msg != null && msg.target == null) {
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null && !msg.isAsynchronous());
                }
            }

            for (int i = 0; i < pendingIdleHandlerCount; i++) {
            }

            pendingIdleHandlerCount = 0;
            nextPollTimeoutMillis = 0;
        }
    }
    
nativePollOnce 函数的调用，第一个参数为mPtr,第二个参数为超时时间。
mPtr存储了Native层的消息队列对象，也就是说Native层还有一个MessageQueue类型。
    
    MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        nativeInit();
    }
    
 nativeInit函数定义在android_os_MessageQueue.cpp中：
 
    static void android_os_MessageQueue_nativeInit(JNIEnv* env, jobject obj) {
        // 构造nativeMessageQueue 对象
        NativeMessageQueue* nativeMessageQueue = new NativeMessageQueue();
        if (!nativeMessageQueue) {
            jniThrowRuntimeException(env, "Unable to allocate native queue");
            return;
        }
        nativeMessageQueue->incStrong(env);
        android_os_MessageQueue_setNativeMessageQueue(env, obj, nativeMessageQueue);
    }
    
    static void android_os_MessageQueue_setNativeMessageQueue(JNIEnv* env, jobject messageQueueObj,
            NativeMessageQueue* nativeMessageQueue) {
            // 设置Java层的mPtr字段， 将NativeMessageQueue对象转换为一个整型变量
        env->SetIntField(messageQueueObj, gMessageQueueClassInfo.mPtr,
                 reinterpret_cast<jint>(nativeMessageQueue));
    }
 
 当java层需要和Native层的MessageQueue通信时只要把这个int值传递给Native层，然后Native通过reinterpret_cast
 将传递进来的int转换为NativeMessageQueue指针传递给Native层，然后Native通过reinterpret_cast将传递进来的int转换为NativeMessageQueue指针即可得到这个
 NativeMessageQueue对象指针。先来看看NativeMessageQueue类的构造函数。
 
    NativeMessageQueue::NativeMessageQueue() : mInCallback(false), mExceptionObj(NULL) {
        mLooper = Looper::getForThread();
        if (mLooper == NULL) {
            mLooper = new Looper(false);
            Looper::setForThread(mLooper);
        }
    }
    
创建了一个Native层的Looper,然后这个Lopper设置给了当前线程。也就是说Java层的MessageQueue和Looper在Native层也都有，但是，他们的功能并不是一一对应的。
那么看看Lopper究竟做了什么，构造函数：
    
    Looper::Looper(bool allowNonCallbacks) :
        mAllowNonCallbacks(allowNonCallbacks), mSendingMessage(false),mResponseIndex(0), mNextMessageUptime(LLONG_MAX) {
        
        int wakeFds[2];
        // 1、创建管道
        int result = pipe(wakeFds);
        LOG_ALWAYS_FATAL_IF(result != 0, "Could not create wake pipe.  errno=%d", errno);
        // 管道的读写端
        mWakeReadPipeFd = wakeFds[0];
        mWakeWritePipeFd = wakeFds[1];
        
        result = fcntl(mWakeReadPipeFd, F_SETFL, O_NONBLOCK);
        LOG_ALWAYS_FATAL_IF(result != 0, "Could not make wake read pipe non-blocking.  errno=%d",errno);
    
        result = fcntl(mWakeWritePipeFd, F_SETFL, O_NONBLOCK);
        LOG_ALWAYS_FATAL_IF(result != 0, "Could not make wake write pipe non-blocking.  errno=%d",errno);
        
        // 2、创建epoll文件描述符
        mEpollFd = epoll_create(EPOLL_SIZE_HINT);
        LOG_ALWAYS_FATAL_IF(mEpollFd < 0, "Could not create epoll instance.  errno=%d", errno);
        
        struct epoll_event eventItem;
        memset(& eventItem, 0, sizeof(epoll_event)); // zero out unused members of data field union
        // 设置事件类型和文件描述符
        eventItem.events = EPOLLIN;
        eventItem.data.fd = mWakeReadPipeFd;
        // 3、监听事件
        result = epoll_ctl(mEpollFd, EPOLL_CTL_ADD, mWakeReadPipeFd, & eventItem);
        LOG_ALWAYS_FATAL_IF(result != 0, "Could not add wake read pipe to epoll instance.  errno=%d",
                errno);
    }

首先创建一个管道（pipe），管道本质上就是一个文件（在Linux中，一切皆是文件），一个管道中含有两个文件描述符，分别对应读和写。
一般的使用方式就是一个线程通过读文件描述符来读管道的内容，当管道没有内容是，这个现成就会进入等待状态；而另外一个线程通过写文件描述符来向管道中写入内容，
写入内容时，如果另一端正有现成正在等待通道中的内容，那么这儿线程就会被唤醒。这个等待和唤醒的操作是通过Linux系统的epoll机制。要使用Linux
的epoll机制，首先通过epoll_create来创建一个epoll专用的文件描述符，即注释2的代码。最后通过epoll_ctl函数设置监听的事件类型为EPOLLIN。
此时Native层的MessageQueue和Looper就构建完成啦，在底层也通过管道和epoll建立了一套消息机制。Navtive层构建完毕之后则会返回到Java层的Looper的构造函数，
因此Java层的Looper和MessageQueue也构建完成。

我们总结一下：
* 1、 首先构造Java层的Looper对象，Looper对象又会在构造函数中创建Java层的MessageQueu对象。
* 2、Java层的MessageQueue的构造函数中调用nativeInit函数初始化Native层的NativeMessageQueue,NativeMessageQueue的构造函数又会创建Native层的Looper，并且通过管道和epoll建立一套消息机制。
* 3、Native层构建完毕，将NativeMessageQueue对象转换为一个整型存储到Java层的MessageQueue的mPtr中。
* 4、启动Java层的消息循环，不断的读取、处理消息。

这个初始化过程是在ActivityThread的main函数中完成，因此，main函数运行之后，UI线程消息循环就启动了，消息循环不断的从消息队列中读取、处理消息，使得整个系统运转起来。

我们回到nativePollOnce函数，看看做了什么：

    static void android_os_MessageQueue_nativePollOnce(JNIEnv* env, jobject obj,
        jint ptr, jint timeoutMillis) {
        NativeMessageQueue* nativeMessageQueue = reinterpret_cast<NativeMessageQueue*>(ptr);
        nativeMessageQueue->pollOnce(env, timeoutMillis);
    }
    
    void NativeMessageQueue::pollOnce(JNIEnv* env, int timeoutMillis) {
        mLooper->pollOnce(timeoutMillis);   
    }
    
调用Looper的pollOnce
    
    int Looper::pollOnce(int timeoutMillis, int* outFd, int* outEvents, void** outData) {
        int result = 0;
        for (;;) {
            // ...
            result = pollInner(timeoutMillis);
        }
    }

核心在于pollInner
    
    int Looper::pollInner(int timeoutMillis) {

        // Poll.
        int result = ALOOPER_POLL_WAKE;
        mResponses.clear();
        mResponseIndex = 0;
    
        struct epoll_event eventItems[EPOLL_MAX_EVENTS];
        // 1、 从管道中读取事件
        int eventCount = epoll_wait(mEpollFd, eventItems, EPOLL_MAX_EVENTS, timeoutMillis);
    
        // 获取锁
        mLock.lock();
    
        // Check for poll error.
        if (eventCount < 0) {
            if (errno == EINTR) {
                goto Done;
            }
            ALOGW("Poll failed with an unexpected error, errno=%d", errno);
            result = ALOOPER_POLL_ERROR;
            goto Done;
        }
    
        // Check for poll timeout.
        if (eventCount == 0) {
                result = ALOOPER_POLL_TIMEOUT;
                goto Done;
            }
    
        for (int i = 0; i < eventCount; i++) {
            int fd = eventItems[i].data.fd;
            uint32_t epollEvents = eventItems[i].events;
            if (fd == mWakeReadPipeFd) {
                if (epollEvents & EPOLLIN) {
                    awoken();
                } else {
                    ALOGW("Ignoring unexpected epoll events 0x%x on wake read pipe.", epollEvents);
                }
            } else {
                ssize_t requestIndex = mRequests.indexOfKey(fd);
                if (requestIndex >= 0) {
                    int events = 0;
                    if (epollEvents & EPOLLIN) events |= ALOOPER_EVENT_INPUT;
                    if (epollEvents & EPOLLOUT) events |= ALOOPER_EVENT_OUTPUT;
                    if (epollEvents & EPOLLERR) events |= ALOOPER_EVENT_ERROR;
                    if (epollEvents & EPOLLHUP) events |= ALOOPER_EVENT_HANGUP;
                    pushResponse(events, mRequests.valueAt(requestIndex));
                } else {
                    ALOGW("Ignoring unexpected epoll events 0x%x on fd %d that is "
                            "no longer registered.", epollEvents, fd);
                }
            }
        }
        
    Done: ;
    
        // Invoke pending message callbacks.
        mNextMessageUptime = LLONG_MAX;
        while (mMessageEnvelopes.size() != 0) {
            nsecs_t now = systemTime(SYSTEM_TIME_MONOTONIC);
            const MessageEnvelope& messageEnvelope = mMessageEnvelopes.itemAt(0);
            // 判断执行时间
            if (messageEnvelope.uptime <= now) {
                
                { // obtain handler
                    sp<MessageHandler> handler = messageEnvelope.handler;
                    Message message = messageEnvelope.message;
                    mMessageEnvelopes.removeAt(0);
                    mSendingMessage = true;
                    mLock.unlock();
                    // 处理消息
                    handler->handleMessage(message);
                } // release handler
     
                mLock.lock();
                mSendingMessage = false;
                result = ALOOPER_POLL_CALLBACK;
            } else {
                // The last message left at the head of the queue determines the next wakeup time.
                mNextMessageUptime = messageEnvelope.uptime;
                break;
            }
        }

        // Release lock.
        mLock.unlock();
  
        return result;
    }



从pollInner的核心代码中看，pollInner实际上就是从管道中读取事件，并且处理这些事件。这样一来就相当于在native层存在一个独立的消息机制，这些事件储存在管道中，
而Java层的事件则存储在消息链表中。但这两个层次的事件都在java层的Looper消息循环中进行不断的获取、处理等操作，从而实现程序的运转。但需要注意的是，Native层的NativeMessageQueue实际上只是一个代理Native Looper的角色，它没有做什么实际工作，只是吧操作转发给Looper。而Native Looper则扮演了一个Java层的Handler角色，
它能够发送消息，取消息，处理消息。

那么Android为什么要有两套消息机制呢？我们知道Android是支持纯native开发的，因此，在Native层实现消息机制是必须的，另外，Android系统的核心组件也都是运行在Native世界，各组件之间也需要通信，这样一来Native层的消息机制就变得很重要。



