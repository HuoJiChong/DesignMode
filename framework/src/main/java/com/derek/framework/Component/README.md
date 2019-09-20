# 组合模式

## 为什么ViewGroup有容器的功能
要回答这个问题，就要先了解View类与ViewGroup类的差别在哪，首先我们知道ViewGroup是继承于View类的。

    public abstract class ViewGroup extends View implements ViewParent, ViewManager {
    }
    
从继承的角度来说ViewGroup拥有View类的所有的非私有方法，既然如此，两者的差别就在于ViewGroup所实现的ViewParent和ViewManager接口上，
而事实也是如此，ViewManager接口定义了addView、removeView等对子视图操作的方法。
    
    public interface ViewParent {
        /**
         * 请求重新布局
         */
        public void requestLayout();
    
        /**
         * 是否已经请求布局，当我们调用requestLayout后，不是立即执行的
         * Android会将消息发送到主线程的Handler并分发
         */
        public boolean isLayoutRequested();
    
        /**
         * 
         */
        public void requestTransparentRegion(View child);
    
        /**
         * 无效化子视图
         */
        public void invalidateChild(View child, Rect r);
    
        /**
         *  无效化子视图的部分或全部区域
         */
        public ViewParent invalidateChildInParent(int[] location, Rect r);
    
        /**
         * Returns the parent if it exists, or null.
         *
         * @return a ViewParent or null if this ViewParent does not have a parent
         */
        public ViewParent getParent();
    
        /**
         * 请求子视图焦点
         */
        public void requestChildFocus(View child, View focused);
        
        // 省略
        
    }

ViewGroup出了锁实现的这两个接口与View不一样外，还有重要的一点就是ViewGroup是抽象类，
其将View中的onLayout方法重置为抽象方法，也就是说容器子类必须实现该方法来实现布局定位，
我们知道View中的该方法是个空实现，因为对于一个普通的View来说改方法并没有什么实现的价值，但是ViewGroup就不一样，
要必须实现。除此之外，在View中比较重要的两个测绘流程的方法onMeasure和onDraw在ViewGroup中都没有被重写，相对于onMeasure方法，
在ViewGroup中增加了计算字View的方法，如measureChildren、measureChildrenWithMargins等;而对于onDraw方法，
ViewGroup定义了衣蛾dispatchDraw方法来调用其每一个字View的onDraw方法，由此可见，ViewGroup真的就像一个容器一样，其职责只是负责对子元素的操作而非具体的个体行为。
