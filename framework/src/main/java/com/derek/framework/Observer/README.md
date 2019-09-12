# BroadcastReceiver 观察者模式深入扩展

BroadcastReceiver是应用内、进程间的一种重要通信手段，能够将某一个消息通过广播的形式传递给他注册的对应广播接收器的对象，接收对象需要通过Context的registerReceiver函数注册到AMS中，当通过sendBroadcast发送广播时，所有注册了对应的IntentFilter的BroadcastReceiver对象就会接收到这个消息，
BroadcastReceiver的onReceive方法就会被调用，这就是一个典型的发布--订阅模式，也就是观察者模式。

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("info.update");
        registerReceiver(receiver,filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show();
        }
    };
    
我们在onResume方法里注册了一个只接受Action为“info.update”的广播接收器，应用内的其他地方发布一个Action为“info.update”的广播时，就会触发updateReceiver的onReceive函数，下面分析原理：

registerReceiver 函数是在ContextWrapper类中实现的，函数如下：
    
    public class ContextWrapper extends Context {
         Context mBase;

        public ContextWrapper(Context base) {
            mBase = base;
        }
        @Override
        public Intent registerReceiver(
            BroadcastReceiver receiver, IntentFilter filter) {
            return mBase.registerReceiver(receiver, filter);
        }
    }
    
这里的mBase是ContextImpl的一个实例，转移到ContextImpl的registerReceiver函数，
    
    class ContextImpl extends Context {
        
        @Override
        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
            return registerReceiver(receiver, filter, null, null);
        }
    
        @Override
        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
                String broadcastPermission, Handler scheduler) {
            return registerReceiverInternal(receiver, filter, broadcastPermission,
                    scheduler, getOuterContext());
        }
    
        private Intent registerReceiverInternal(BroadcastReceiver receiver,
                IntentFilter filter, String broadcastPermission,
                Handler scheduler, Context context) {
            IIntentReceiver rd = null;
            if (receiver != null) {
                if (mPackageInfo != null && context != null) {
                    if (scheduler == null) {
                        // 获取Handler来投递消息
                        scheduler = mMainThread.getHandler();
                    }
                    // 获取IIntentReceiver对象，通过他与AMS交互，并且通过Handler传递消息
                    rd = mPackageInfo.getReceiverDispatcher(
                        receiver, context, scheduler,
                        mMainThread.getInstrumentation(), true);
                } else {
                    if (scheduler == null) {
                        scheduler = mMainThread.getHandler();
                    }
                    rd = new LoadedApk.ReceiverDispatcher(
                            receiver, context, scheduler, null, true).getIIntentReceiver();
                }
            }
            try {
                // 调用ActivityManageNative的registerReceiver，实际是ActivityManagerProxy
                return ActivityManagerNative.getDefault().registerReceiver(
                        mMainThread.getApplicationThread(), mBasePackageName,
                        rd, filter, broadcastPermission);
            } catch (RemoteException e) {
                return null;
            }
        }
    }

函数registerReceiverInternal的成员变量 mPackageInfo 是一个LoadedApk实例，用来辅助处理广播的接收。通过 mPackageInfo.getReceiverDispatcher 函数获得一个 IIntentReceiver 接口对象rd,
这是一个Binder对象，接下来会把他传给ActivityManagerService，ActivityManagerService在接收到相应的广播时，就通过这个Binder对象来通知MainActivity来接收的。

    public IIntentReceiver getReceiverDispatcher(BroadcastReceiver r,
            Context context, Handler handler,
            Instrumentation instrumentation, boolean registered) {
        synchronized (mReceivers) {
            LoadedApk.ReceiverDispatcher rd = null;
            HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher> map = null;
            if (registered) {
                map = mReceivers.get(context);
                if (map != null) {
                    rd = map.get(r);
                }
            }
            if (rd == null) {
                rd = new ReceiverDispatcher(r, context, handler,
                        instrumentation, registered);
                if (registered) {
                    if (map == null) {
                        map = new HashMap<BroadcastReceiver, LoadedApk.ReceiverDispatcher>();
                        mReceivers.put(context, map);
                    }
                    map.put(r, rd);
                }
            } else {
                rd.validate(context, handler);
            }
            rd.mForgotten = false;
            return rd.getIIntentReceiver();
        }
    }

在getReceiverDispatcher 函数中，判断是否是已经有ReceiverDispatcher存在，有则返回，否则创建一个，并保存起来

    static final class ReceiverDispatcher {

        final static class InnerReceiver extends IIntentReceiver.Stub {
            final WeakReference<LoadedApk.ReceiverDispatcher> mDispatcher;
            final LoadedApk.ReceiverDispatcher mStrongRef;

            InnerReceiver(LoadedApk.ReceiverDispatcher rd, boolean strong) {
                mDispatcher = new WeakReference<LoadedApk.ReceiverDispatcher>(rd);
                mStrongRef = strong ? rd : null;
            }
        }

        final IIntentReceiver.Stub mIIntentReceiver;
        final Handler mActivityThread;
        ReceiverDispatcher(BroadcastReceiver receiver, Context context,
                Handler activityThread, Instrumentation instrumentation,
                boolean registered) {
            if (activityThread == null) {
                throw new NullPointerException("Handler must not be null");
            }

            mIIntentReceiver = new InnerReceiver(this, !registered);
            mReceiver = receiver;
            mContext = context;
        }

        IIntentReceiver getIIntentReceiver() {
            return mIIntentReceiver;
        }

    }

在新建广播接收发布器 ReceiverDispatcher 时，会在构造函数里创建一个InnerReceiver实例，这是一个Binder对象，实现了IIntentReceiver接口，可以通过ReceiverDispatcher.getIntentReceiver函数来获得，
然后传给ActivityManagerService，以便接收广播。在ReceiverDispatcher类的构造函数中，还会把传进来的Handle类型的参数activityThread保存下来，以便后面在分发广播的时候使用。

    ActivityManagerNative.getDefault();
    private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
        protected IActivityManager create() {
            IBinder b = ServiceManager.getService("activity");
            if (false) {
                Log.v("ActivityManager", "default service binder = " + b);
            }
            IActivityManager am = asInterface(b);
            if (false) {
                Log.v("ActivityManager", "default service = " + am);
            }
            return am;
        }
    };
    static public IActivityManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IActivityManager in =
            (IActivityManager)obj.queryLocalInterface(descriptor);
        if (in != null) {
            return in;
        }
        return new ActivityManagerProxy(obj);
    }

我们来看一下ActivityManagerProxy的registerReceiver

    public Intent registerReceiver(IApplicationThread caller, String packageName,
            IIntentReceiver receiver,
            IntentFilter filter, String perm) throws RemoteException
    {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeInterfaceToken(IActivityManager.descriptor);
        data.writeStrongBinder(caller != null ? caller.asBinder() : null);
        data.writeString(packageName);
        data.writeStrongBinder(receiver != null ? receiver.asBinder() : null);
        filter.writeToParcel(data, 0);
        data.writeString(perm);
        // 向AMS提交注册广播接收器的请求
        mRemote.transact(REGISTER_RECEIVER_TRANSACTION, data, reply, 0);
        reply.readException();
        Intent intent = null;
        int haveIntent = reply.readInt();
        if (haveIntent != 0) {
            intent = Intent.CREATOR.createFromParcel(reply);
        }
        reply.recycle();
        data.recycle();
        return intent;
    }
    
这个函数通过Binder驱动程序就进入到ActivityManagerService中的registerReceiver函数中去了。

    public Intent registerReceiver(IApplicationThread caller, String callerPackage,
            IIntentReceiver receiver, IntentFilter filter, String permission) {
        enforceNotIsolatedCaller("registerReceiver");
        synchronized(this) {
            // 1、获取 ProcessRecord 
            ProcessRecord callerApp = null;
            if (caller != null) {
                callerApp = getRecordForAppLocked(caller);
                if (callerApp == null) {
                }
            } else {
                callerPackage = null;
            }

            List allSticky = null;

            // 2、根据Action查找匹配的sticky接收器
            Iterator actions = filter.actionsIterator();
            if (actions != null) {
                while (actions.hasNext()) {
                    String action = (String)actions.next();
                    allSticky = getStickiesLocked(action, filter, allSticky);
                }
            } else {
                allSticky = getStickiesLocked(null, filter, allSticky);
            }

            Intent sticky = allSticky != null ? (Intent)allSticky.get(0) : null;

            if (receiver == null) {
                return sticky;
            }
            
            // 3、获取ReceiverList
            ReceiverList rl
                = (ReceiverList)mRegisteredReceivers.get(receiver.asBinder());
            if (rl == null) {
                rl = new ReceiverList(this, callerApp,
                        Binder.getCallingPid(),
                        Binder.getCallingUid(), receiver);
                if (rl.app != null) {
                    rl.app.receivers.add(rl);
                } else {
                   
                }
                mRegisteredReceivers.put(receiver.asBinder(), rl);
            }
            // 4、构建BroadcastFilter对象，并且添加到ReceiverList中
            BroadcastFilter bf = new BroadcastFilter(filter, rl, callerPackage, permission);
            rl.add(bf);
            
            mReceiverResolver.addFilter(bf);

            return sticky;
        }
    }
    
函数首先是获得调用registerReceiver函数的应用程序进程记录块，即注释1处，然后获取这个 进程对应的pid、uicL注释2处获取到IntentFilter的所有Action,在MainActivity中构建的filter 只有一个Action，
就是前面描述的“info.update”，这里先通过getStickiesLocked函数查找一下有没 有对应的sticky intent列表存在。什么是Sticky Intent呢？
我们在最后一次调用sendStickyBroadcast 函数来发送某个Action类型的广播时，系统会把代表这个广播的Intent保存下来，这样，后来调用 registerReceiver来注册相同Action类型的广播接收器，
就会得到这个最后发出的广播，这就是为什 么叫做StickyIntent 了。这个最后发出的广播虽然被处理完了，但是仍然被粘在ActivityManagerService 中，以便下一个注册相应Action类型的广播接收器还能继承处理。

这里，假设不使用sendStickyBroadcast来发送“info.update”类型的广播，于是，得到的allSticky 和 sticky 都为 null。

继续往下看，这里传进来的receiver不为null，于是，继续往下执行到注释3处。这里其实就 是把广播接收器receiver保存到一个ReceiverList列表中，这个列表的宿主进程是rl.app，这里就是 MainActivity所在的进程，
在ActivityManagerService中，用一个进程记录块来表示这个应用程序进 程，它里面有一个列表receivers,专门用来保存这个进程注册的广播接收器。接着，又把这个 ReceiverList 列表以 receiver 为 Key 值保存在
 ActivityManagerService 的成员变量 mRegisteredReceivers 中，这些都是为了方便在收到广播时，快速找到对应的广播接收器。
再往下看注释4,只是把广播接收器receiver保存起来，但是还没有把它和filter关联起来，这里 就创建一个BroadcastFilter来把广播接收器列表rl和filter关联起来，然后保存在ActivityManagerService 的成员变量m
ReceiverResolver中，这样，就将广播接收器receiver及其要接收的广播类型filter保存在 ActivityManagerService中，以便以后能够接收到相应的广播并进行处理。

广播的发送过程比广播接收器的注册过程要复杂得多，不过这个过程仍然是以ActivityManagerService为中心。
这里我们简单总结一下Android应用程序发送广播的过程。
1、通过sendBroadcast把一个广播通过Binder发送给ActivityManagerService，AMS根据这个广播的Action的类型找到相应的广播接收器，然后把这儿广播放进自己的消息队列中，就完成第一阶段对这个广播的异步分发。

    private final int broadcastIntentLocked(ProcessRecord callerApp,
            String callerPackage, Intent intent, String resolvedType,
            IIntentReceiver resultTo, int resultCode, String resultData,
            Bundle map, String requiredPermission,
            boolean ordered, boolean sticky, int callingPid, int callingUid,
            int userId) {
        intent = new Intent(intent);

        // ..... 

        // Figure out who all will receive this broadcast.
        List receivers = null;
        List<BroadcastFilter> registeredReceivers = null;
        try {
            if (intent.getComponent() != null) {
                // ...
            } else {
                // ....
                // 查询到改Intent对应的BroadcastFilter，也就是接收器列表
                registeredReceivers = mReceiverResolver.queryIntent(intent, resolvedType, false,
                        userId);
            }
        } catch (RemoteException ex) {
            
        }

        final boolean replacePending =
                (intent.getFlags()&Intent.FLAG_RECEIVER_REPLACE_PENDING) != 0;
        
        if (DEBUG_BROADCAST) Slog.v(TAG, "Enqueing broadcast: " + intent.getAction()
                + " replacePending=" + replacePending);
        
        int NR = registeredReceivers != null ? registeredReceivers.size() : 0;
        // 无序广播
        if (!ordered && NR > 0) {
            // If we are not serializing this broadcast, then send the
            // registered receivers separately so they don't wait for the
            // components to be launched.
            final BroadcastQueue queue = broadcastQueueForIntent(intent);
            BroadcastRecord r = new BroadcastRecord(queue, intent, callerApp,
                    callerPackage, callingPid, callingUid, requiredPermission,
                    registeredReceivers, resultTo, resultCode, resultData, map,
                    ordered, sticky, false);
            if (DEBUG_BROADCAST) Slog.v(
                    TAG, "Enqueueing parallel broadcast " + r);
            final boolean replaced = replacePending && queue.replaceParallelBroadcastLocked(r);
            
            if (!replaced) {
                queue.enqueueParallelBroadcastLocked(r); //Parallel 无序广播lock
                queue.scheduleBroadcastsLocked();  // 处理广播的异步分发
            }
            registeredReceivers = null;
            NR = 0;
        }

        // ....
    }
    
2、AMS在消息循环中处理这个广播，并通过Binder机制把这个广播分发给注册的 ReceiverDispatcher , ReceiverDispatcher 把这个广播放进MainActivity所在线程的消息队列中，就完成第二阶段对这个广播的异步分发。

    
    public void scheduleBroadcastsLocked() {
        if (DEBUG_BROADCAST) Slog.v(TAG, "Schedule broadcasts ["
                + mQueueName + "]: current="
                + mBroadcastsScheduled);

        if (mBroadcastsScheduled) {
            return;
        }
        mHandler.sendMessage(mHandler.obtainMessage(BROADCAST_INTENT_MSG, this));
        mBroadcastsScheduled = true;
    }
    
mHandler的类型为Handler ,是AMS的内部类

    final Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BROADCAST_INTENT_MSG: {
                    processNextBroadcast(true);
                } break;
                case BROADCAST_TIMEOUT_MSG: {
                    synchronized (mService) {
                        broadcastTimeoutLocked(true);
                    }
                } break;
            }
        }
    };

3、ReceiverDispatcher的内部类Args在MainActivity所在的线程消息循环中处理这个广播，最终是将这个广播分发给所注册的BroadcastReceiver实例的onReceive函数进行处理。

    public final class LoadedApk {
        static final class ReceiverDispatcher {
            final class Args extends BroadcastReceiver.PendingResult implements Runnable {
                private Intent mCurIntent;
                private final boolean mOrdered;

                public void run() {
                    final BroadcastReceiver receiver = mReceiver;
                    final boolean ordered = mOrdered;
                    
                    final IActivityManager mgr = ActivityManagerNative.getDefault();
                    final Intent intent = mCurIntent;
                    mCurIntent = null;
                    
                    try {
                        ClassLoader cl =  mReceiver.getClass().getClassLoader();
                        intent.setExtrasClassLoader(cl);
                        setExtrasClassLoader(cl);
                        receiver.setPendingResult(this);
                        // 调用接收器的onReceive方法
                        receiver.onReceive(mContext, intent);
                    } catch (Exception e) {
                        
                    }
                    
                    if (receiver.getPendingResult() != null) {
                        finish();
                    }
                }
            }
        }
    }
    
简单来说，广播这既是一个订阅--发布的过程，通过一些map存储BroadcastReceiver，key就是封装了这些广播的信息类，如Action之类的。当发布一个广播时通过AMS到这个map中查询注册了这个广播的IntentFilter的
BroadcastReceiver,然后通过ReceiverDispatcher将广播分发给各个订阅对象，从而完成这个发布--订阅过程。