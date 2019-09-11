责任链模式，是行为型设计模式之一。我们将多个节点首尾相连所构成的模型成为链，比如生活中常见的锁链，就是由一个个圆角长方形的铁环串起来的结构。

# Android 源码中的责任链模式 - 事件分发

ViewGroup 的执行事件派发的方法是 dispatchTouchEvent，在该方法中其对事件进行了统一的分发。

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mInputEventConsistencyVerifier != null) {
            mInputEventConsistencyVerifier.onTouchEvent(ev, 1);
        }

        boolean handled = false;
        if (onFilterTouchEventForSecurity(ev)) {
            final int action = ev.getAction();
            final int actionMasked = action & MotionEvent.ACTION_MASK;

            // 处理原始的DOWN事件
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                // 处理完上一个事件
                cancelAndClearTouchTargets(ev);
                resetTouchState();
            }

            // 检查事件拦截
            final boolean intercepted;
            if (actionMasked == MotionEvent.ACTION_DOWN
                    || mFirstTouchTarget != null) {
                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {
                    // 是否拦截 onInterceptTouchEvent() 子元素 重写
                    intercepted = onInterceptTouchEvent(ev);
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;
                }
            } else {
                intercepted = true;
            }

            // 检查是否取消
            final boolean canceled = resetCancelNextUpFlag(this)
                    || actionMasked == MotionEvent.ACTION_CANCEL;

            final boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
            TouchTarget newTouchTarget = null;
            boolean alreadyDispatchedToNewTouchTarget = false;
            // 事件没有取消也没有拦截
            if (!canceled && !intercepted) {
                if (actionMasked == MotionEvent.ACTION_DOWN
                        || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
                        || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
                    final int actionIndex = ev.getActionIndex(); // always 0 for down
                    final int idBitsToAssign = split ? 1 << ev.getPointerId(actionIndex)
                            : TouchTarget.ALL_POINTER_IDS;

                    removePointersFromTouchTargets(idBitsToAssign);

                    final int childrenCount = mChildrenCount;
                    if (childrenCount != 0) {
                        final View[] children = mChildren;
                        final float x = ev.getX(actionIndex);
                        final float y = ev.getY(actionIndex);
                        
                        //  遍历子节点
                        for (int i = childrenCount - 1; i >= 0; i--) {
                            final View child = children[i];
                            
                            // 如果这个子元素无法接收Pointer Event或者这个事件点压根就没有落在子元素的边界范围内
                            if (!canViewReceivePointerEvents(child)
                                    || !isTransformedTouchPointInView(x, y, child, null)) {
                                    // 结束该次循环
                                continue;
                            }
                            
                            // 找到Event该由哪个子元素持有
                            newTouchTarget = getTouchTarget(child);
                            if (newTouchTarget != null) {
                                newTouchTarget.pointerIdBits |= idBitsToAssign;
                                break;
                            }

                            resetCancelNextUpFlag(child);
                            // 投递事件执行触摸操作
                            // 如果子元素还是一个ViewGroup，则地柜调用重复此过程
                            // 如果子元素是一个View，那么则会调用View的dispatchTouchEvent，并最终由onTouchEvent处理
                            if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                                mLastTouchDownTime = ev.getDownTime();
                                mLastTouchDownIndex = i;
                                mLastTouchDownX = ev.getX();
                                mLastTouchDownY = ev.getY();
                                newTouchTarget = addTouchTarget(child, idBitsToAssign);
                                alreadyDispatchedToNewTouchTarget = true;
                                break;
                            }
                        }
                    }

                    // 如果没有发现子元素可以持有该次事件
                    if (newTouchTarget == null && mFirstTouchTarget != null) {
                        newTouchTarget = mFirstTouchTarget;
                        while (newTouchTarget.next != null) {
                            newTouchTarget = newTouchTarget.next;
                        }
                        newTouchTarget.pointerIdBits |= idBitsToAssign;
                    }
                }
            }
        
        // .....
        return handled;
    }
    
下面看一下dispatchTransformedTouchEvent方法是如何调度子元素dispatchTouchEvent方法的：

    private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
            View child, int desiredPointerIdBits) {
        final boolean handled;

        final int oldAction = event.getAction();
        // 如果事件被取消，
        if (cancel || oldAction == MotionEvent.ACTION_CANCEL) {
            event.setAction(MotionEvent.ACTION_CANCEL);
            // 如果没有子元素
            if (child == null) {
                // 调用父类的dispatchTouchEvent，注意，这里的父类终会为View类
                handled = super.dispatchTouchEvent(event);
            } else {
                // 如果有子元素则传递cancel事件
                handled = child.dispatchTouchEvent(event);
            }
            event.setAction(oldAction);
            return handled;
        }

        // 计算即将被传递的点的数量
        final int oldPointerIdBits = event.getPointerIdBits();
        final int newPointerIdBits = oldPointerIdBits & desiredPointerIdBits;
        
        // 如果没有响应的点的数量
        if (newPointerIdBits == 0) {
            return false;
        }
        // 声明临时变量保存坐标变换后的MotionEvent
        final MotionEvent transformedEvent;
        // 如果事件点的数量一致
        if (newPointerIdBits == oldPointerIdBits) {
            // 子元素为空，或者子元素有一个单位矩阵
            if (child == null || child.hasIdentityMatrix()) {
                if (child == null) {
                    // 子元素为空，调用父类的dispatchTouchEvent
                    handled = super.dispatchTouchEvent(event);
                } else {
                    final float offsetX = mScrollX - child.mLeft;
                    final float offsetY = mScrollY - child.mTop;
                    // 将MotionEvent进行坐标变换
                    event.offsetLocation(offsetX, offsetY);
                    // 变换后的MotionEvent传递给子元素，最终会调用到子元素的 onTouchEvent 方法。
                    handled = child.dispatchTouchEvent(event);
                    // 将MotionEvent还原
                    event.offsetLocation(-offsetX, -offsetY);
                }
                // 通过判断，当前事件被持有则可以直接返回
                return handled;
            }
            transformedEvent = MotionEvent.obtain(event);
        } else {
            transformedEvent = event.split(newPointerIdBits);
        }

        if (child == null) {
            handled = super.dispatchTouchEvent(transformedEvent);
        } else {
            final float offsetX = mScrollX - child.mLeft;
            final float offsetY = mScrollY - child.mTop;
            transformedEvent.offsetLocation(offsetX, offsetY);
            if (! child.hasIdentityMatrix()) {
                transformedEvent.transform(child.getInverseMatrix());
            }

            handled = child.dispatchTouchEvent(transformedEvent);
        }

        transformedEvent.recycle();
        return handled;
    }
    
    
