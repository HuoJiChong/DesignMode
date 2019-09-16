# 备忘录模式

备忘录模式是一种行为模式。该模式用于保存对象的当前状态，并且在之后可以再次回复到此状态。

## 使用场景
* 1、需要保存一个对象在某一时刻的状态或部分状态
* 2、如果用一个接口来让其他对象得到这些状态，将会暴露对象的实现细节并破坏对象的封装性，一个对象不希望外界直接访问其内部状态，通过中间对象可以间接访问其内部状态。

# Android 中的备忘录模式

onSaveInstanceState 和 onRestoreInstanceState

onSaveInstanceState相关代码：

    protected void onSaveInstanceState(Bundle outState) {
        // 1、存储当前窗口的视图树的状态
        outState.putBundle(WINDOW_HIERARCHY_TAG, mWindow.saveHierarchyState());
        // 2、存储Fragment的状态
        Parcelable p = mFragments.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
        // 3、如果用户还设置了Activity的ActivityLifecycleCallbacks
        // 那么调用这些ActivityLifecycleCallbacks的onActivitySaveInstanceState 进行状态的存储
        getApplication().dispatchActivitySaveInstanceState(this, outState);
    }

1、将Window对象中的视图树中各个View状态存储到Bundle中，这样一来，当用户重新加入到该Activity时，用户UI的结构、状态才会被重新恢复，
以此来保证用户界面的一致性。 实现是PhoneWindow

    /** {@inheritDoc} */
    @Override
    public Bundle saveHierarchyState() {
        Bundle outState = new Bundle();
        if (mContentParent == null) {
            return outState;
        }

        SparseArray<Parcelable> states = new SparseArray<Parcelable>();
        mContentParent.saveHierarchyState(states);
        outState.putSparseParcelableArray(VIEWS_TAG, states);

        // save the focused view id
        View focusedView = mContentParent.findFocus();
        if (focusedView != null) {
            if (focusedView.getId() != View.NO_ID) {
                outState.putInt(FOCUSED_ID_TAG, focusedView.getId());
            } else {
                if (false) {
                    Log.d(TAG, "couldn't save which view has focus because the focused view " + focusedView + " has no id.");
                }
            }
        }

        // save the panels
        SparseArray<Parcelable> panelStates = new SparseArray<Parcelable>();
        savePanelState(panelStates);
        if (panelStates.size() > 0) {
            outState.putSparseParcelableArray(PANELS_TAG, panelStates);
        }

        if (mActionBar != null) {
            SparseArray<Parcelable> actionBarStates = new SparseArray<Parcelable>();
            mActionBar.saveHierarchyState(actionBarStates);
            outState.putSparseParcelableArray(ACTION_BAR_TAG, actionBarStates);
        }

        return outState;
    }

// .....
