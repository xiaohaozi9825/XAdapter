package pw.xiaohaozi.xadapter

import pw.xiaohaozi.xadapter.fragment.node.AddNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.AddNodesFragment
import pw.xiaohaozi.xadapter.fragment.smart.CheckFragment
import pw.xiaohaozi.xadapter.fragment.smart.ClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.ConcatAdapterFragment
import pw.xiaohaozi.xadapter.fragment.smart.CoroutineScopeFragment
import pw.xiaohaozi.xadapter.fragment.node.CreateMultipleNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.CreateNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.EditNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.FriendFragment
import pw.xiaohaozi.xadapter.fragment.node.GoodsFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataDifferFragment
import pw.xiaohaozi.xadapter.fragment.smart.DataOperationFragment
import pw.xiaohaozi.xadapter.fragment.smart.DragSortFragment
import pw.xiaohaozi.xadapter.fragment.smart.GroupFragment
import pw.xiaohaozi.xadapter.fragment.smart.ImageSelectFragment
import pw.xiaohaozi.xadapter.fragment.smart.LongClickFragment
import pw.xiaohaozi.xadapter.fragment.smart.MultipleFragment
import pw.xiaohaozi.xadapter.fragment.node.Node2EditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeEditFragment
import pw.xiaohaozi.xadapter.fragment.node.NodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveAtNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveListNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveMultipleNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemoveNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.RemovePositionNodeFragment
import pw.xiaohaozi.xadapter.fragment.node.ReplaceNodeFragment
import pw.xiaohaozi.xadapter.fragment.smart.SingleFragment
import pw.xiaohaozi.xadapter.fragment.smart.SpecialLayoutFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeDeleteFragment
import pw.xiaohaozi.xadapter.fragment.smart.SwipeMenuFragment
import pw.xiaohaozi.xadapter.fragment.smart.TextChangeFragment
import pw.xiaohaozi.xadapter.info.HomeInfo

//smart模块
val smartMenuList = arrayListOf(
    "Adapter创建",
    HomeInfo("创建单布局", "单布局创建方式", R.mipmap.ic_launcher, SingleFragment::class.java),
    HomeInfo("创建多布局", "多布局创建方式", R.mipmap.ic_launcher, MultipleFragment::class.java),
    "选择操作",
    HomeInfo("选择操作", "Item选择操作", R.mipmap.ic_launcher, ImageSelectFragment::class.java),
    "Item事件监听",
    HomeInfo("点击事件", "item点击事件", R.mipmap.ic_launcher, ClickFragment::class.java),
    HomeInfo("长按事件", "item长按事件", R.mipmap.ic_launcher, LongClickFragment::class.java),
    HomeInfo("选中事件", "单选、多选等", R.mipmap.ic_launcher, CheckFragment::class.java),
    HomeInfo("文本变化", "EditText文本变化监听", R.mipmap.ic_launcher, TextChangeFragment::class.java),
    "特殊布局",
    HomeInfo("特殊布局", "如头布局，脚布局，空布局、缺省页", R.mipmap.ic_launcher, SpecialLayoutFragment::class.java),
    HomeInfo("分组布局", "允许Item撑满整行", R.mipmap.ic_launcher, GroupFragment::class.java),
    "拖拽与侧滑",
    HomeInfo("侧滑删除", "侧滑删除Item", R.mipmap.ic_launcher, SwipeDeleteFragment::class.java),
    HomeInfo("拖拽排序", "长按拖拽排序", R.mipmap.ic_launcher, DragSortFragment::class.java),
    HomeInfo("侧滑菜单", "类似QQ侧滑效果", R.mipmap.ic_launcher, SwipeMenuFragment::class.java),
    "数据操作",
    HomeInfo("常规操作", "数据增删改查", R.mipmap.ic_launcher, DataOperationFragment::class.java),
    HomeInfo("Differ", "使用Differ更新数据", R.mipmap.ic_launcher, DataDifferFragment::class.java),
    "其他",
    HomeInfo("协程测试", "", R.mipmap.ic_launcher, CoroutineScopeFragment::class.java),
    HomeInfo("ConcatAdapter", "结合ConcatAdapter使用", R.mipmap.ic_launcher, ConcatAdapterFragment::class.java),
)

//node模块
val nodeMenuList = arrayListOf(
    "Node测试",
    HomeInfo("node测试", "", R.mipmap.ic_launcher, NodeFragment::class.java),
    HomeInfo("node编辑", "", R.mipmap.ic_launcher, NodeEditFragment::class.java),
    HomeInfo("node编辑2", "", R.mipmap.ic_launcher, Node2EditFragment::class.java),
    "Node创建",
    HomeInfo("单类型创建", "单类型Node创建", R.mipmap.ic_launcher, CreateNodeFragment::class.java),
    HomeInfo("多类型创建", "多类型Node创建", R.mipmap.ic_launcher, CreateMultipleNodeFragment::class.java),
    "Node展开与折叠",
    HomeInfo("展开与折叠", "Node展开与折叠", R.mipmap.ic_launcher),

    "Node数据操作",
    HomeInfo("添加一个节点", "", R.mipmap.ic_launcher, AddNodeFragment::class.java),
    HomeInfo("添加多个节点", "", R.mipmap.ic_launcher, AddNodesFragment::class.java),

    HomeInfo(
        "修改节点",
        "修改指定节点内容",
        R.mipmap.ic_launcher,
        EditNodeFragment::class.java
    ),//updateNode(node: D, payload: Any? = null)、updateNode(oldNode: D, newNode: D, payload: Any? = null)、updateChildNode(parent: D, oldNode: D, newNode: D, payload: Any? = null)
    HomeInfo(
        "替换Node",
        "替换节点以及子节点",
        R.mipmap.ic_launcher,
        ReplaceNodeFragment::class.java
    ),//replaceNode(oldNode: D, newNode: D)

    HomeInfo(
        "删除一个节点",
        "删除一个根节点或子节点",
        R.mipmap.ic_launcher,
        RemoveNodeFragment::class.java
    ),//removeNode(node: D) 、removeChildNode(parent: D, node: D)
    HomeInfo(
        "按索引删除节点",
        "",
        R.mipmap.ic_launcher,
        RemoveAtNodeFragment::class.java
    ),//removeNodeAt(index: Int)、removeChildNodeAt(parent: D, index: Int)
    HomeInfo(
        "删除多个连续节点",
        "",
        R.mipmap.ic_launcher,
        RemoveMultipleNodeFragment::class.java
    ),//removeNode(start: Int, count: Int)、removeChildNode(parent: D, start: Int, count: Int)
    HomeInfo(
        "删除多个不连续节点",
        "",
        R.mipmap.ic_launcher,
        RemoveListNodeFragment::class.java
    ),//removeNodeList(nodes: List<D>)、 removeChildNodeList(parent: D, nodes: List<D>)
    HomeInfo(
        "按列表所在位置删除",
        "",
        R.mipmap.ic_launcher,
        RemovePositionNodeFragment::class.java
    ),//removeNodePosition(adapterPosition: Int)、
)

//示例模块
val exampleMenuList = arrayListOf<Any>(
    HomeInfo("好友列表", "仿QQ好友列表", R.mipmap.ic_launcher, FriendFragment::class.java),
    HomeInfo("分类列表联动", "商品分类用与商品列表联动", R.mipmap.ic_launcher, GoodsFragment::class.java),
    HomeInfo("多级分类列表", "商品分类多级菜单", R.mipmap.ic_launcher),
    HomeInfo("图片选择器", "", R.mipmap.ic_launcher),
    HomeInfo("底部导航栏", "", R.mipmap.ic_launcher),
    HomeInfo("TabBar", "", R.mipmap.ic_launcher),

    )