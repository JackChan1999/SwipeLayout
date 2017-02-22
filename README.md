# SwipeLayout 侧拉删除

- 掌握ViewDragHelper 的用法
- 掌握平滑动画的原理及状态更新事件回调

应用场景：QQ 聊天记录，邮件管理，需要对条目进行功能扩展的场景，效果图：

![](http://img.blog.csdn.net/20170218112811591?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYXhpMjk1MzA5MDY2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

# ViewDragHelper 初始化
创建自定义控件SwipeLayout 继承FrameLayout
```java
public class SwipeLayout extends FrameLayout {
    private ViewDragHelper mHelper;
    public SwipeLayout(Context context) {
        this(context,null);
    }
    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //1.创建ViewDragHelper
        mHelper = ViewDragHelper.create(this, mCallback);
    }
    //2.转交触摸事件，拦截判断，处理触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mHelper.shouldInterceptTouchEvent(ev);
    };
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            //多点触摸有一些小bug，最好catch 一下
            mHelper.processTouchEvent(event);
        } catch (Exception e) {
        }
        //消费事件，返回true
        return true;
    };
    //3.处理回调事件
    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }
    };
}
```
第3-8 行通过构造方法互调，将三个构造方法串连起来，这样初始化代码只需要写在第三个构造方法中即可
第11-37 行ViewDragHelper 使用三步曲
# 界面初始化
将SwipeLayout 布局到activity_main.xml 中
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                tools:context=".MainActivity" >
    <com.example.swipe.SwipeLayout
        android:layout_width="match_parent"
        android:layout_height="60dp" >
        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_gravity="right"
            android:layout_height="match_parent" >
            <TextView
                android:layout_width="60dp"
                android:layout_height="match_parent"
                android:background="#666666"
                android:gravity="center"
                android:text="Call"
                android:textColor="#FFFFFF" />
            <TextView
                android:layout_width="60dp"
                android:layout_height="match_parent"
                android:background="#FF0000"
                android:gravity="center"
                android:text="Delete"
                android:textColor="#FFFFFF" />
        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#33000000"
            android:gravity="center_vertical" >
            <ImageView
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:layout_marginLeft="10dp"
                android:src="@drawable/head_1" />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="10dp"
                android:text="宋江" />
        </LinearLayout>
    </com.example.swipe.SwipeLayout>
</RelativeLayout>
```
重写SwipeLayout 中mCallback 方法，实现简单的拖拽
```java
//3.处理回调事件
ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
    //返回值决定了child 是否可以被拖拽
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
        //child 被用户拖拽的孩子
        return true;
    }
    //返回值决定将要移动到的位置，此时还没有发生真正的移动
    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        //left 建议移动到的位置
        return left;
    }
};
```
# 拖拽事件的传递
## 限定拖拽范围
第一个子view 命名为后布局，第二个子view 命名为前布局
```java
private View mBackView;
private View mFrontView;
//此方法中查找控件
@Override
protected void onFinishInflate() {
    super.onFinishInflate();
    mBackView = getChildAt(0);
    mFrontView = getChildAt(1);
};
```
获取控件宽高及拖拽范围
```java
private int mRange;
private int mWidth;
private int mHeight;
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    //mBackView 的宽度就是mFrontView 的拖拽范围
    mRange = mBackView.getMeasuredWidth();
    //控件的宽
    mWidth = getMeasuredWidth();
    //控件的高
    mHeight = getMeasuredHeight();
}
```
重写mCallback 回调方法
```java
ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
    //返回值决定了child 是否可以被拖拽
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
        return true;
    }
    //返回拖拽的范围，返回一个大于0 的值，计算动画执行的时长，水平方向是否可以被滑开
    @Override
    public int getViewHorizontalDragRange(View child) {
        return mRange;
    }
    //返回值决定将要移动到的位置，此时还没有发生真正的移动
    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        // left 建议移动到位置
        if (child == mFrontView) {
            //限定前布局的拖拽范围
            if (left < -mRange) {
                //前布局最小的左边位置不能小于-mRange
                left = -mRange;
            } else if (left > 0) {
                //前布局最大的左边位置不能大于0
                left = 0;
            }
        } else if (child == mBackView) {
            //限定后布局的拖拽范围
            if (left < mWidth - mRange) {
                //后布局最小左边位置不能小于mWidth - mRange
                left = mWidth - mRange;
            } else if (left > mWidth) {
                //后布局最大的左边位置不能大于mWidth
                left = mWidth;
            }
        }
        return left;
    }
};
```
第7-11 行需要返回一个大于0 的拖拽范围
第14-37 行通过mRange 分别计算前后布局的拖拽范围
## 传递拖拽事件
初始化前后布局的位置，重写SwipeLayout 的onLayout()方法
```java
@Override
protected void onLayout(boolean changed, int left, int top, int right,
                        int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    //默认是关闭状态
    layoutContent(false);
};
private void layoutContent(boolean isOpen) {
    //设置前布局位置
    Rect rect = computeFrontRect(isOpen);
    mFrontView.layout(rect.left, rect.top, rect.right, rect.bottom);
    //根据前布局位置计算后布局位置
    Rect backRect = computeBackRectViaFront(rect);
    mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
}

private Rect computeBackRectViaFront(Rect rect) {
    int left = rect.right;
    return new Rect(left, 0, left + mRange, mHeight);
}
/**
 * 计算布局所在矩形区域
 * @param isOpen
 * @return
 */
private Rect computeFrontRect(boolean isOpen) {
    int left = 0;
    if(isOpen){
        left = -mRange;
    }
    return new Rect(left, 0, left + mWidth, mHeight);
}
```
第2-7 行重新摆放子view 的位置
第8-15 行由于后布局是连接在前布局后面一起滑动的，所以可以通过前布局的位置计算后布局的位置
前后布局在拖拽过程中互相传递变化量
```java
ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
    // 返回值决定了child 是否可以被拖拽
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
        return true;
    }
    // 返回拖拽的范围，返回一个大于0 的值，计算动画执行的时长，水平方向是否可以被滑开
    @Override
    public int getViewHorizontalDragRange(View child) {
        return mRange;
    }
    // 返回值决定将要移动到的位置，此时还没有发生真正的移动
    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        // left 建议移动到位置
        if (child == mFrontView) {
            // 限定前布局的拖拽范围
            if (left < -mRange) {
                // 前布局最小的左边位置不能小于-mRange
                left = -mRange;
            } else if (left > 0) {
                // 前布局最大的左边位置不能大于0
                left = 0;
            }
        } else if (child == mBackView) {
            // 限定后布局的拖拽范围
            if (left < mWidth - mRange) {
                // 后布局最小左边位置不能小于mWidth - mRange
                left = mWidth - mRange;
            } else if (left > mWidth) {
                // 后布局最大的左边位置不能大于mWidth
                left = mWidth;
            }
        }
        return left;
    }
    //位置发生改变时，前后布局的变化量互相传递
    @Override
    public void onViewPositionChanged(View changedView, int left, int top,
                                      int dx, int dy) {
        super.onViewPositionChanged(changedView, left, top, dx, dy);
        //left 最新的水平位置
        //dx 刚刚发生的水平变化量
        //位置变化时，把水平变化量传递给另一个布局
        if(changedView == mFrontView){
            //拖拽的是前布局，把刚刚发生的变化量dx 传递给后布局
            mBackView.offsetLeftAndRight(dx);
        }else if(changedView == mBackView){
            //拖拽的是后布局，把刚刚发生的变化量dx 传递给前布局
            mFrontView.offsetLeftAndRight(dx);
        }
        //兼容低版本，重绘一次界面
        invalidate();
    }
};
```
第38-54 行拖拽前布局时，将前布局的变化量传递给后布局，拖拽后布局时，把后布局的变化量传递给前布局，这样前后布局就可以连动起来
# 结束动画
## 跳转动画
```java
// 3.处理回调事件
ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
    // 返回值决定了child 是否可以被拖拽
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
        return true;
    }
    // 返回拖拽的范围，返回一个大于0 的值，计算动画执行的时长，水平方向是否可以被滑开
    @Override
    public int getViewHorizontalDragRange(View child) {
        return mRange;
    }
    // 返回值决定将要移动到的位置，此时还没有发生真正的移动
    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
        // left 建议移动到位置
        if (child == mFrontView) {
            // 限定前布局的拖拽范围
            if (left < -mRange) {
                // 前布局最小的左边位置不能小于-mRange
                left = -mRange;
            } else if (left > 0) {
                // 前布局最大的左边位置不能大于0
                left = 0;
            }
        } else if (child == mBackView) {
            // 限定后布局的拖拽范围
            if (left < mWidth - mRange) {
                // 后布局最小左边位置不能小于mWidth - mRange
                left = mWidth - mRange;
            } else if (left > mWidth) {
                // 后布局最大的左边位置不能大于mWidth
                left = mWidth;
            }
        }
        return left;
    }
    @Override
    public void onViewPositionChanged(View changedView, int left, int top,
                                      int dx, int dy) {
        super.onViewPositionChanged(changedView, left, top, dx, dy);
        //left 最新的水平位置
        //dx 刚刚发生的水平变化量
        //位置变化时，把水平变化量传递给另一个布局
        if(changedView == mFrontView){
            //拖拽的是前布局，把刚刚发生的变化量dx 传递给后布局
            mBackView.offsetLeftAndRight(dx);
        }else if(changedView == mBackView){
            //拖拽的是后布局，把刚刚发生的变化量dx 传递给前布局
            mFrontView.offsetLeftAndRight(dx);
        }
        //兼容低版本，重绘一次界面
        invalidate();
    }
    //松手时会被调用
    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
        super.onViewReleased(releasedChild, xvel, yvel);
        //xvel 水平方向上的速度，向左为-，向右为+
        if(xvel == 0 && mFrontView.getLeft() < -mRange * 0.5f){
            //xvel 变0 时,并且前布局的左边位置小于-mRange 的一半
            open();
        }else if (xvel < 0){
            //xvel 为-时，打开
            open();
        }else{
            //其它情况为关闭
            close();
        }
    }
};
public void close() {
    //调用之前布局子view 的方法直接跳转到关闭位置
    layoutContent(false);
}

public void open() {
    //调用之前布局子view 的方法直接跳转到打开位置
    layoutContent(true);
}
```
第55-70 行重写Callback 的onViewReleased()方法，该方法在松手后被调用，结束动画需要在此处做

## 平滑动画
```java
public void close() {
    close(true);
}
public void open() {
    open(true);
}
public void close(boolean isSmooth) {
    int finalLeft = 0;
    if(isSmooth){
        if(mHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        };
    }else{
        layoutContent(false);
    }
}
public void open(boolean isSmooth) {
    int finalLeft = -mRange;
    if (isSmooth) {
        //mHelper.smoothSlideViewTo(child, finalLeft, finalTop)开启一个平滑动画将child
        //移动到finalLeft,finalTop 的位置上。此方法返回true 说明当前位置不是最终位置需要重绘
        if(mHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)){
            //调用重绘方法
            //invalidate();可能会丢帧,此处推荐使用ViewCompat.postInvalidateOnAnimation()
            //参数一定要传child 所在的容器，因为只有容器才知道child 应该摆放在什么位置
            ViewCompat.postInvalidateOnAnimation(this);
        };
    } else {
        layoutContent(true);
    }
}
//重绘时computeScroll()方法会被调用
@Override
public void computeScroll() {
    super.computeScroll();
    //mHelper.continueSettling(deferCallbacks)维持动画的继续，返回true 表示还需要重绘
    if(mHelper.continueSettling(true)){
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
```
第1-31 行重载open()，close()方法，保留跳转动画，添加平滑动画
第32-39 行重写computeScroll()方法维持动画的继续，此处必须重写，否则没有动画效果

# 监听回调
## 定义回调接口
在SwipeLayout 中定义公开的接口
```java
//控件有三种状态
public enum Status{
    Open,Close,Swiping
}
//初始状态为关闭
private Status status = Status.Close;
public Status getStatus() {
    return status;
}

public void setStatus(Status status) {
    this.status = status;
}

public interface OnSwipeListener{
    //通知外界已经打开
    public void onOpen();
    //通知外界已经关闭
    public void onClose();
    //通知外界将要打开
    public void onStartOpen();
    //通知外界将要关闭
    public void onStartClose();
}
private OnSwipeListener onSwipeListener;
public OnSwipeListener getOnSwipeListener() {
    return onSwipeListener;
}
public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
    this.onSwipeListener = onSwipeListener;
}
```
第20-23 行SwipeLayout 做为ListView 的item 时将要打开或关闭时需要通知其它item 做相应的处理，所以增加这两个方法
## 更新状态及回调监听
修改Callback 的onViewPositionChanged()方法
```java
@Override
public void onViewPositionChanged(View changedView, int left, int top,
                                  int dx, int dy) {
    super.onViewPositionChanged(changedView, left, top, dx, dy);
    //left 最新的水平位置
    //dx 刚刚发生的水平变化量
    //位置变化时，把水平变化量传递给另一个布局
    if(changedView == mFrontView){
        //拖拽的是前布局，把刚刚发生的变化量dx 传递给后布局
        mBackView.offsetLeftAndRight(dx);
    }else if(changedView == mBackView){
        //拖拽的是后布局，把刚刚发生的变化量dx 传递给前布局
        mFrontView.offsetLeftAndRight(dx);
    }
    //更新状态及调用监听
    dispatchDragEvent();
    //兼容低版本，重绘一次界面
    invalidate();
}
```
第15-16 行调用更新状态及回调监听的方法
dispatchDragEvent()方法
```java
/**
 * 更新状态回调监听
 */
protected void dispatchDragEvent() {
    //需要记录一下上次的状态，对比当前状态和上次状态，在状态改变时调用监听
    Status lastStatus = status;
    //获取更新状态
    status = updateStatus();
    //在状态改变时调用监听
    if(lastStatus != status && onSwipeListener != null){
        if(status == Status.Open){
            onSwipeListener.onOpen();
        }else if(status == Status.Close){
            onSwipeListener.onClose();
        }else if(status == Status.Swiping){
            if(lastStatus == Status.Close){
                //如果上一次状态为关闭，现在是拖拽状态，说明正在打开
                onSwipeListener.onStartOpen();
            }else if(lastStatus == Status.Open){
                //如果上一次状态为打开，现在是拖拽状态，说明正在关闭
                onSwipeListener.onStartClose();
            }
        }
    }
}
private Status updateStatus() {
    //通过前布局左边的位置可以判断当前的状态
    int left = mFrontView.getLeft();
    if(left == 0){
        return Status.Close;
    }else if(left == -mRange){
        return Status.Open;
    }
    return Status.Swiping;
}
```
修改activity_main.xml，给SwipeLayout 加上id
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                tools:context=".MainActivity" >
    <com.example.swipe.SwipeLayout
        android:id="@+id/sl"
        android:layout_width="match_parent"
        android:layout_height="60dp" >

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_gravity="right"
            android:layout_height="match_parent" >
            <TextView
                android:layout_width="60dp"
                android:layout_height="match_parent"
                android:background="#666666"
                android:gravity="center"
                android:text="Call"
                android:textColor="#FFFFFF" />
            <TextView
                android:layout_width="60dp"
                android:layout_height="match_parent"
                android:background="#FF0000"
                android:gravity="center"
                android:text="Delete"
                android:textColor="#FFFFFF" />
        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#33000000"
            android:gravity="center_vertical" >
            <ImageView
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:layout_marginLeft="10dp"
                android:src="@drawable/head_1" />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="10dp"
                android:text="宋江" />
        </LinearLayout>
    </com.example.swipe.SwipeLayout>
</RelativeLayout>
```
MainActivity 中设置监听回调
```java
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.sl);
        swipeLayout.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onStartOpen() {
                Utils.showToast(getApplicationContext(), "要去打开了");
            }

            @Override
            public void onStartClose() {
                Utils.showToast(getApplicationContext(), "要去关闭了");
            }

            @Override
            public void onOpen() {
                Utils.showToast(getApplicationContext(), "已经打开了");
            }

            @Override
            public void onClose() {
                Utils.showToast(getApplicationContext(), "已经关闭了");
            }
        });
    }
}
```
Utils 提供单例Toast 方法
```java
public class Utils {
    private static Toast toast;
    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
```
# 整合到ListView
修改activity_main.xml
```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                tools:context=".MainActivity" >
    <ListView
        android:id="@+id/lv"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    </ListView>
</RelativeLayout>
```
ListView 需要的数据
```java
public class Cheeses {
    public static final String[] NAMES = new String[]{"宋江", "卢俊义", "吴用",
            "公孙胜", "关胜", "林冲", "秦明", "呼延灼", "花荣", "柴进", "李应", "朱仝", "鲁智 深",
            "武松", "董平", "张清", "杨志", "徐宁", "索超", "戴宗", "刘唐", "李逵", "史进", " 穆弘",
            "雷横", "李俊", "阮小二", "张横", "阮小五", " 张顺", "阮小七", "杨雄", "石秀", " 解珍",
            " 解宝", "燕青", "朱武", "黄信", "孙立", "宣赞", "郝思文", "韩滔", "彭玘", "单廷珪 ",
            "魏定国", "萧让", "裴宣", "欧鹏", "邓飞", " 燕顺", "杨林", "凌振", "蒋敬", "吕方 ",
            "郭盛", "安道全", "皇甫端", "王英", "扈三娘", "鲍旭", "樊瑞", "孔明", "孔亮", " 项充",
            "李衮", "金大坚", "马麟", "童威", "童猛", "孟康", "侯健", "陈达", "杨春", "郑天寿 ",
            "陶宗旺", "宋清", "乐和", "龚旺", "丁得孙", "穆春", "曹正", "宋万", "杜迁", "薛永 ", "施恩",
            "周通", "李忠", "杜兴", "汤隆", "邹渊", "邹润", "朱富", "朱贵", "蔡福", "蔡庆", " 李立",
            "李云", "焦挺", "石勇", "孙新", "顾大嫂", "张青", "孙二娘", " 王定六", "郁保四", " 白胜",
            "时迁", "段景柱"};
}
```
修改SwipeLayout 的OnSwipeListener 接口，在回调接口方法时把自己传出去
```java
public interface OnSwipeListener{
    //通知外界已经打开
    public void onOpen(SwipeLayout swipeLayout);
    //通知外界已经关闭
    public void onClose(SwipeLayout swipeLayout);
    //通知外界将要打开
    public void onStartOpen(SwipeLayout swipeLayout);
    //通知外界将要关闭
    public void onStartClose(SwipeLayout swipeLayout);
}
```
ListView 的Adapter

```java
public class MyAdapter extends BaseAdapter {
    private Context     context;
    //记录上一次被打开item
    private SwipeLayout lastOpenedSwipeLayout;
    public MyAdapter(Context context) {
        super();
        this.context = context;
    }
    @Override
    public int getCount() {
        return Cheeses.NAMES.length;
    }
    @Override
    public Object getItem(int position) {
        return Cheeses.NAMES[position];
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.list_item, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(Cheeses.NAMES[position]);
        SwipeLayout swipeLayout = (SwipeLayout) convertView;
        swipeLayout.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onOpen(SwipeLayout swipeLayout) {
                //当前item 被打开时，记录下此item
                lastOpenedSwipeLayout = swipeLayout;
            }
            @Override
            public void onClose(SwipeLayout swipeLayout) {
            }
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
                //当前item 将要打开时关闭上一次打开的item
                if(lastOpenedSwipeLayout != null){
                    lastOpenedSwipeLayout.close();
                }
            }
            @Override
            public void onStartClose(SwipeLayout swipeLayout) {
            }
        });
        return convertView;
    }
}
```

# 关于我

- Email：<815712739@qq.com>
- CSDN博客：[Allen Iverson](http://blog.csdn.net/axi295309066)
- 新浪微博：[AndroidDeveloper](http://weibo.com/u/1848214604?topnav=1&amp;wvr=6&amp;topsug=1&amp;is_all=1)

# License

    Copyright 2015 AllenIverson

    Copyright 2012 Jake Wharton
    Copyright 2011 Patrik Åkerfeldt
    Copyright 2011 Francisco Figueiredo Jr.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
