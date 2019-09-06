# 1、 Builder 模式角色简介
* Product 产品类----产品的抽象类； 
* Builder----抽象Builder类，规范产品的组建，一般是由子类实现具体的组建过程;
* ConcreteBuilder----具体的Builder类；
* Director----统一组装过程；

Builder的简单实现可以查看Builder的代码

# 2、Android系统的 Builder 设计模式分析：
## 1、AlertDialog源码
        public class AlertDialog extends Dialog implements DialogInterface{
            private AlertController mAlert;
            // 构造函数，
            protected AlertDialog(Context context, int theme) {
                this(context, theme, true);
            }
            //实际构造的是AlertController对象
            AlertDialog(Context context, int theme, boolean createContextWrapper) {
                super(context, resolveDialogTheme(context, theme), createContextWrapper);
                mWindow.alwaysReadCloseOnTouchAttr();
                mAlert = new AlertController(getContext(), this, getWindow());
            }
            
            //实际调用的对象都是mAlert对象
            @Override
            public void setTitle(CharSequence title) {
                super.setTitle(title);
                mAlert.setTitle(title);
            }
                  
            public void setMessage(CharSequence message) {
                mAlert.setMessage(message);
            }
        
            /**
             * Set the view to display in that dialog.
             */
            public void setView(View view) {
                mAlert.setView(view);
            }
            
            // 创建视图的
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);  //父类是一个空的实现
                mAlert.installContent();             //
            }
            
            //构造者对象
            public static class Builder {
                //构造对象需要的参数，使用构造者将创建需要的参数存储到P对象中；
                private final AlertController.AlertParams P;
                private int mTheme;
                //构造者对象的构造函数，
                public Builder(Context context) {
                    this(context, resolveDialogTheme(context, 0));
                }
                public Builder(Context context, int theme) {
                    P = new AlertController.AlertParams(new ContextThemeWrapper(
                            context, resolveDialogTheme(context, theme)));
                    mTheme = theme;
                }
                //存储参数，然后再将构造者对象返回，方便链式调用。
                public Builder setTitle(int titleId) {
                    P.mTitle = P.mContext.getText(titleId);
                    return this;
                }
                
                public Builder setTitle(CharSequence title) {
                    P.mTitle = title;
                    return this;
                }
                
                //根据参数创建AlertDialog对象，
                public AlertDialog create() {
                    //调用AlertDialog的构造函数，
                    final AlertDialog dialog = new AlertDialog(P.mContext, mTheme, false);
                    //传递参数
                    P.apply(dialog.mAlert);
                    dialog.setCancelable(P.mCancelable);
                    if (P.mCancelable) {
                        dialog.setCanceledOnTouchOutside(true);
                    }
                    dialog.setOnCancelListener(P.mOnCancelListener);
                    if (P.mOnKeyListener != null) {
                        dialog.setOnKeyListener(P.mOnKeyListener);
                    }
                    return dialog;
                }
                
                //调用Dialog的show方法。
                public AlertDialog show() {
                    AlertDialog dialog = create();
                    dialog.show();
                    return dialog;
                }
            }
        
        }
        
## 2、AlertParams的apply方法
        public class AlertController {
            public static class AlertParams {
                //将参数信息传给dialog对象。
                public void apply(AlertController dialog) {
                    if (mCustomTitleView != null) {
                        dialog.setCustomTitle(mCustomTitleView);
                    } else {
                        if (mTitle != null) {
                            dialog.setTitle(mTitle);
                        }
                        if (mIcon != null) {
                            dialog.setIcon(mIcon);
                        }
                        if (mIconId >= 0) {
                            dialog.setIcon(mIconId);
                        }
                    }
                    if (mMessage != null) {
                        dialog.setMessage(mMessage);
                    }
                    if (mPositiveButtonText != null) {
                        dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText,
                                mPositiveButtonListener, null);
                    }
                    if (mNegativeButtonText != null) {
                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText,
                                mNegativeButtonListener, null);
                    }
                    if (mNeutralButtonText != null) {
                        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, mNeutralButtonText,
                                mNeutralButtonListener, null);
                    }
                    if (mForceInverseBackground) {
                        dialog.setInverseBackgroundForced(true);
                    }
                    // For a list, the client can either supply an array of items or an
                    // adapter or a cursor
                    if ((mItems != null) || (mCursor != null) || (mAdapter != null)) {
                        createListView(dialog);
                    }
                    if (mView != null) {
                        if (mViewSpacingSpecified) {
                            dialog.setView(mView, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                                    mViewSpacingBottom);
                        } else {
                            dialog.setView(mView);
                        }
                    }
                    
                    /*
                    dialog.setCancelable(mCancelable);
                    dialog.setOnCancelListener(mOnCancelListener);
                    if (mOnKeyListener != null) {
                        dialog.setOnKeyListener(mOnKeyListener);
                    }
                    */
                }
            
            }
        }
        
## 3、Dialog的show方法分析
    public class Dialog implements DialogInterface, Window.Callback,
        KeyEvent.Callback, OnCreateContextMenuListener {
            //...
            public void show() {
                //1、已经是显示状态，返回
                if (mShowing) {
                    if (mDecor != null) {
                        if (mWindow.hasFeature(Window.FEATURE_ACTION_BAR)) {
                            mWindow.invalidatePanelMenu(Window.FEATURE_ACTION_BAR);
                        }
                        mDecor.setVisibility(View.VISIBLE);
                    }
                    return;
                }
        
                mCanceled = false;
                // 2、调用onCreate() 方法
                if (!mCreated) {
                    dispatchOnCreate(null);
                }
        
                onStart();
                //3、 获得Decor视图
                mDecor = mWindow.getDecorView();
                
                if (mActionBar == null && mWindow.hasFeature(Window.FEATURE_ACTION_BAR)) {
                    mActionBar = new ActionBarImpl(this);
                }
                // 4、 获得布局参数
                WindowManager.LayoutParams l = mWindow.getAttributes();
                if ((l.softInputMode & WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
                    WindowManager.LayoutParams nl = new WindowManager.LayoutParams();
                    nl.copyFrom(l);
                    nl.softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
                    l = nl;
                }
        
                try {
                    //  将mDecor添加到WindowManager中，
                    mWindowManager.addView(mDecor, l);
                    mShowing = true;
                    // 发送一个显示Dialog的消息
                    sendShowMessage();
                } finally {
                }
            }
        }
这是一系列典型的声明周期函数。
在onCreate方法主要是mAlert.installContent();

    public class AlertController {
        public void installContent() {
            /* We use a custom title so never request a window title */
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            
            if (mView == null || !canTextInput(mView)) {
                mWindow.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
            //调用mWindow的setContentView方法设置布局 这个和Activity的setContentView方法一样，mAlertDialogLayout的布局对象
            // mAlertDialogLayout = a.getResourceId(com.android.internal.R.styleable.AlertDialog_layout,com.android.internal.R.layout.alert_dialog);
            mWindow.setContentView(mAlertDialogLayout);
            setupView();
        }
        //
        private void setupView() {
            LinearLayout contentPanel = (LinearLayout) mWindow.findViewById(R.id.contentPanel);
            //内容布局，
            setupContent(contentPanel);
            boolean hasButtons = setupButtons();
            
            LinearLayout topPanel = (LinearLayout) mWindow.findViewById(R.id.topPanel);
            TypedArray a = mContext.obtainStyledAttributes(
                    null, com.android.internal.R.styleable.AlertDialog, com.android.internal.R.attr.alertDialogStyle, 0);
            boolean hasTitle = setupTitle(topPanel);
                
            View buttonPanel = mWindow.findViewById(R.id.buttonPanel);
            if (!hasButtons) {
                buttonPanel.setVisibility(View.GONE);
                mWindow.setCloseOnTouchOutsideIfNotSet(true);
            }
            
            // 自定义布局
            FrameLayout customPanel = null;
            if (mView != null) {
                customPanel = (FrameLayout) mWindow.findViewById(R.id.customPanel);
                FrameLayout custom = (FrameLayout) mWindow.findViewById(R.id.custom);
                custom.addView(mView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
                if (mViewSpacingSpecified) {
                    custom.setPadding(mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight,
                            mViewSpacingBottom);
                }
                if (mListView != null) {
                    ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
                }
            } else {
                mWindow.findViewById(R.id.customPanel).setVisibility(View.GONE);
            }
            
            /* Only display the divider if we have a title and a 
             * custom view or a message.
             */
            if (hasTitle) {
                View divider = null;
                if (mMessage != null || mView != null || mListView != null) {
                    divider = mWindow.findViewById(R.id.titleDivider);
                } else {
                    divider = mWindow.findViewById(R.id.titleDividerTop);
                }
    
                if (divider != null) {
                    divider.setVisibility(View.VISIBLE);
                }
            }
            
            setBackground(topPanel, contentPanel, customPanel, hasButtons, a, hasTitle, buttonPanel);
            a.recycle();
        }
        
    }



