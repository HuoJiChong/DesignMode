# 状态模式
状态模式中的行为是有状态来决定的，不同的状态下有不同的行为。
# 使用场景
* 一个对象的行为取决于它的状态，并且它必须在运行时根据状态改变它的行为。
* 代码中包含大量与对象状态有关的条件语句，例如，一个操作中包含庞大的多分支语句（if-else或switch-case），且这些分支依赖于改对象的状态。

# UML类图




# Android Wifi状态模式源码

在frameworks\base\core\java\com\android\internal\util\StateMachine$SmHandler中

    @Override
    public final void handleMessage(Message msg) {
        if (mDbg) Log.d(TAG, "handleMessage: E msg.what=" + msg.what);

        /** Save the current message */
        mMsg = msg;

        if (mIsConstructionCompleted) {
            /** Normal path */
            // 处理消息
            processMsg(msg);
        } else if (!mIsConstructionCompleted &&
                (mMsg.what == SM_INIT_CMD) && (mMsg.obj == mSmHandlerObj)) {
            /** Initial one time path. */
            mIsConstructionCompleted = true;
            invokeEnterMethods(0);
        } else {
            throw new RuntimeException("StateMachine.handleMessage: " +
                        "The start method not called, received msg: " + msg);
        }
        // 执行状态切换
        performTransitions();

        if (mDbg) Log.d(TAG, "handleMessage: X");
    }

    
    private final void processMsg(Message msg) {
        StateInfo curStateInfo = mStateStack[mStateStackTopIndex];
        if (mDbg) {
            Log.d(TAG, "processMsg: " + curStateInfo.state.getName());
        }
        // 调用当前状态的方法处理小处理消息
        while (!curStateInfo.state.processMessage(msg)) {
            
            curStateInfo = curStateInfo.parentStateInfo;
            if (curStateInfo == null) {
                mSm.unhandledMessage(msg);
                if (isQuit(msg)) {
                    transitionTo(mQuittingState);
                }
                break;
            }
            if (mDbg) {
                Log.d(TAG, "processMsg: " + curStateInfo.state.getName());
            }
        }

        if (mSm.recordProcessedMessage(msg)) {
            if (curStateInfo != null) {
                State orgState = mStateStack[mStateStackTopIndex].state;
                mProcessedMessages.add(msg, mSm.getMessageInfo(msg), curStateInfo.state,
                        orgState);
            } else {
                mProcessedMessages.add(msg, mSm.getMessageInfo(msg), null, null);
            }
        }
    }

// wifi的各种状态

    class InitialState extends State {
        @Override
        //TODO: could move logging into a common class
        public void enter() {
            if (DBG) log(getName() + "\n");
            EventLog.writeEvent(EVENTLOG_WIFI_STATE_CHANGED, getName());

            if (mWifiNative.isDriverLoaded()) {
                transitionTo(mDriverLoadedState);
            }
            else {
                transitionTo(mDriverUnloadedState);
            }

            mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
            mWifiP2pChannel.connect(mContext, getHandler(), mWifiP2pManager.getMessenger());

            try {
                mNwService.disableIpv6(mInterfaceName);
            } catch (RemoteException re) {
                loge("Failed to disable IPv6: " + re);
            } catch (IllegalStateException e) {
                loge("Failed to disable IPv6: " + e);
            }
        }
    }

        @Override
        public boolean processMessage(Message message) {
            if (DBG) log(getName() + message.toString() + "\n");
            switch (message.what) {
                case CMD_LOAD_DRIVER_SUCCESS:
                    transitionTo(mDriverLoadedState);
                    break;
                case CMD_LOAD_DRIVER_FAILURE:
                    transitionTo(mDriverFailedState);
                    break;
                case CMD_LOAD_DRIVER:
                case CMD_UNLOAD_DRIVER:
                case CMD_START_SUPPLICANT:
                case CMD_STOP_SUPPLICANT:
                case CMD_START_AP:
                case CMD_STOP_AP:
                case CMD_START_DRIVER:
                case CMD_STOP_DRIVER:
                case CMD_SET_SCAN_MODE:
                case CMD_SET_SCAN_TYPE:
                case CMD_SET_COUNTRY_CODE:
                case CMD_SET_FREQUENCY_BAND:
                case CMD_START_PACKET_FILTERING:
                case CMD_STOP_PACKET_FILTERING:
                    deferMessage(message);
                    break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }
    }

    class DriverLoadedState extends State {
        @Override
        public void enter() {
            if (DBG) log(getName() + "\n");
            EventLog.writeEvent(EVENTLOG_WIFI_STATE_CHANGED, getName());
        }
        @Override
        public boolean processMessage(Message message) {
            if (DBG) log(getName() + message.toString() + "\n");
            switch(message.what) {
                case CMD_UNLOAD_DRIVER:
                    transitionTo(mDriverUnloadingState);
                    break;
                case CMD_START_SUPPLICANT:
                    //.....
                    break;
                case CMD_START_AP:
                    transitionTo(mSoftApStartingState);
                    break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }
    }

    class DriverUnloadingState extends State {
        @Override
        public void enter() {
            if (DBG) log(getName() + "\n");
            EventLog.writeEvent(EVENTLOG_WIFI_STATE_CHANGED, getName());

        }

        @Override
        public boolean processMessage(Message message) {
            if (DBG) log(getName() + message.toString() + "\n");
            switch (message.what) {
                case CMD_UNLOAD_DRIVER_SUCCESS:
                    transitionTo(mDriverUnloadedState);
                    break;
                case CMD_UNLOAD_DRIVER_FAILURE:
                    transitionTo(mDriverFailedState);
                    break;
                case CMD_LOAD_DRIVER:
                //....
                    deferMessage(message);
                    break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }
    }

    class DriverUnloadedState extends State {
        @Override
        public void enter() {
            if (DBG) log(getName() + "\n");
            EventLog.writeEvent(EVENTLOG_WIFI_STATE_CHANGED, getName());
        }
        @Override
        public boolean processMessage(Message message) {
            if (DBG) log(getName() + message.toString() + "\n");
            switch (message.what) {
                case CMD_LOAD_DRIVER:
                    transitionTo(mDriverLoadingState);
                    break;
                default:
                    return NOT_HANDLED;
            }
            return HANDLED;
        }
    }

