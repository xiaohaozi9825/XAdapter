package pw.xiaohaozi.xadapter.fragment.node

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.databinding.FragmentFriendBinding
import pw.xiaohaozi.xadapter.databinding.ItemFriendBinding
import pw.xiaohaozi.xadapter.databinding.ItemFriendGroupBinding
import pw.xiaohaozi.xadapter.node.entity.ExpandedNodeEntity
import pw.xiaohaozi.xadapter.node.entity.NodeEntity
import pw.xiaohaozi.xadapter.node.ext.nodeAdapter
import pw.xiaohaozi.xadapter.utils.loadCircle


class FriendFragment : Fragment() {
    private lateinit var binding: FragmentFriendBinding
    private val TAG = "FriendFragment"
    private var ceilingMountedNode: FriendGroup? = null
    private val customAnimator = CustomAnimator()

    @SuppressLint("SetTextI18n")
    private val adapter = nodeAdapter()
        .withType<ItemFriendGroupBinding, FriendGroup> { (holder, data) ->
            val childs = data.getChildNodeEntityList()
            holder.binding.tvName.text = data.name
            holder.binding.tvCount.text = "${childs.filter { it.state >= 0 }.size}/${childs.size}"
            holder.binding.ivArrow.rotation = if (data.isExpanded()) 0f else -90f
        }
        .setOnClickListener { holder, data, position, view ->
            if (data.isExpanded()) adapter.collapse(position) else adapter.expand(position)
            val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
        .withType<ItemFriendBinding, FriendInfo> { (holder, data) ->
            holder.binding.ivHead.loadCircle(data.head)
            holder.binding.tvName.text = data.name
            holder.binding.tvState.text = data.description()
            holder.binding.tvState.isGone = data.description().isNullOrBlank()
        }
        .toAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater)
        binding.recycleView.adapter = adapter
        binding.recycleView.addOnScrollListener(FriendScrollListener())
        //设置item动画结束监听事件，item动画结束后需要重置吸顶的位置
        customAnimator.setOnAnimationsFinishedListener(AnimationsFinishedListener())
        binding.recycleView.itemAnimator = customAnimator
        adapter.refresh(dataList)
        setCeilingMounted(dataList.first())
        binding.layoutGroup.root.setOnClickListener {
            val data = ceilingMountedNode ?: return@setOnClickListener
            val position = adapter.getDataList().indexOf(data)
            if (position != -1) {
                if (data.isExpanded()) adapter.collapse(position) else adapter.expand(position)
                val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager
                val targetPosition = adapter.getDataList().indexOf(data)
                if (targetPosition != -1) layoutManager.scrollToPositionWithOffset(targetPosition, 0)
//                binding.layoutGroup.root.translationY = -binding.layoutGroup.root.height.toFloat()
            }
        }
        return binding.root
    }

    /**
     * 设置吸顶内容
     */
    @SuppressLint("SetTextI18n")
    fun setCeilingMounted(data: FriendGroup?) {
        data ?: return
        ceilingMountedNode = data
//        binding.layoutGroup.root.setBackgroundColor(Color.parseColor("#88ff0000"))
        binding.layoutGroup.tvName.text = data.name
        binding.layoutGroup.ivArrow.rotation = if (data?.isExpanded() == true) 0f else -90f
        val childs = data.getChildNodeEntityList()
        binding.layoutGroup.tvCount.text = "${childs.filter { it.state >= 0 }.size}/${childs.size}"

    }

    //获取当前位置View的Y坐标值
    private fun getY(position: Int): Int {
        val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager
        val itemView = layoutManager.findViewByPosition(position);
        if (itemView != null) {
            val itemLocation = IntArray(2);
            val recyclerLocation = IntArray(2);
            itemView.getLocationOnScreen(itemLocation); // Item 的屏幕坐标
            binding.recycleView.getLocationOnScreen(recyclerLocation); // RecyclerView 的屏幕坐标
            return itemLocation[1] - recyclerLocation[1] // 相对 Y 坐标
        }
        return 0
    }

    /**
     * recyclerView 滑动监听，实现吸顶效果
     */
    inner class FriendScrollListener : OnScrollListener() {
        //分组Item高度
        private val groupHeight by lazy { binding.layoutGroup.root.height }
        private val layoutManager by lazy { binding.recycleView.layoutManager as LinearLayoutManager }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val visibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val completelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            val visibleNode = adapter.getDataList()[visibleItemPosition]
            val completelyVisibleNode = adapter.getDataList()[completelyVisibleItemPosition]

            if (dy > 0) {//上滑
                if (visibleNode is FriendGroup) {
                    setCeilingMounted(visibleNode)
                }
                //第一个完全可见的item是分组类型，item滑到顶部时，吸顶跟随向上滑动，直至item到达顶部，吸顶会恢复到Y坐标为0的位置
                if (completelyVisibleNode is FriendGroup) {
                    val y = getY(completelyVisibleItemPosition)
                    if (y <= groupHeight) {//跟随上滑
                        binding.layoutGroup.root.translationY = (y - groupHeight).toFloat()
                    } else {//悬停
                        binding.layoutGroup.root.translationY = 0f
                    }
                } else {//悬停
                    binding.layoutGroup.root.translationY = 0f
                }

            } else if (dy < 0) {//下滑
                val friendGroup = when (visibleNode) {
                    is FriendGroup -> visibleNode
                    is FriendInfo -> visibleNode.getParentNodeEntity()
                    else -> null
                }
                setCeilingMounted(friendGroup)
                //第一个完全可见的item是分组类型，吸顶跟随item向下滑动，直至吸顶完全展示，最后停留在Y坐标为0的位置
                if (completelyVisibleNode is FriendGroup) {
                    val y = getY(completelyVisibleItemPosition)
                    if (y <= binding.layoutGroup.root.height) {//跟随
                        binding.layoutGroup.root.translationY = (y - groupHeight).toFloat()
                    } else {//悬停
                        binding.layoutGroup.root.translationY = 0f
                    }
                } else {//悬停
                    binding.layoutGroup.root.translationY = 0f
                }
            }
        }


    }

    class CustomAnimator : DefaultItemAnimator() {
        private var mListener: OnAnimationsFinishedListener? = null

        interface OnAnimationsFinishedListener {
            fun onAllAnimationsFinished() // 所有挂起动画完成
            fun onAddAnimationFinished(item: RecyclerView.ViewHolder?) // 单个添加动画完成
            fun onRemoveAnimationFinished(item: RecyclerView.ViewHolder?) // 单个移除动画完成
            fun onChangeAnimationFinished(item: RecyclerView.ViewHolder?) // 
            // ... 可根据需要添加其他类型
        }

        fun setOnAnimationsFinishedListener(listener: OnAnimationsFinishedListener?) {
            mListener = listener
        }

        override fun runPendingAnimations() {
            super.runPendingAnimations()
            // 可选：在这里监听动画开始
        }

        override fun onChangeFinished(item: RecyclerView.ViewHolder?, oldItem: Boolean) {
            super.onChangeFinished(item, oldItem)
            if (mListener != null) mListener!!.onChangeAnimationFinished(item)
        }

        override fun onAddFinished(item: RecyclerView.ViewHolder) {
            super.onAddFinished(item)
            if (mListener != null) mListener!!.onAddAnimationFinished(item)
        }

        override fun onRemoveFinished(item: RecyclerView.ViewHolder) {
            super.onRemoveFinished(item)
            if (mListener != null) mListener!!.onRemoveAnimationFinished(item)
        }

        override fun isRunning(): Boolean {
            val running = super.isRunning()
            // 当所有动画结束时触发回调
            if (!running && mListener != null) {
                mListener!!.onAllAnimationsFinished()
            }
            return running
        }
    }

    inner class AnimationsFinishedListener : CustomAnimator.OnAnimationsFinishedListener {
        //等待动画结束，修改吸顶位置和内容
        override fun onAllAnimationsFinished() {
            Log.i(TAG, "onAllAnimationsFinished: ")
            val layoutManager = binding.recycleView.layoutManager as LinearLayoutManager

            val visibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleNode = adapter.getDataList()[visibleItemPosition]
            val completelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            val completelyVisibleNode = adapter.getDataList()[completelyVisibleItemPosition]
            val groupHeight = binding.layoutGroup.root.height.toFloat()
            //如果第一个完全可见的是分组，吸顶位置根据该分组位置决定悬停或跟随。
            //如果第一个完全可见的是好友，则悬停
            if (completelyVisibleNode is FriendGroup) {
                val y = getY(completelyVisibleItemPosition)
                if (y <= groupHeight) {//跟随
                    binding.layoutGroup.root.translationY = (y - groupHeight).toFloat()
                } else {//悬停
                    binding.layoutGroup.root.translationY = 0f
                }
            } else {//悬停
                binding.layoutGroup.root.translationY = 0f
            }

            when (visibleNode) {
                is FriendInfo -> {
                    val friendGroup = visibleNode.getParentNodeEntity()
                    setCeilingMounted(friendGroup)
                }

                is FriendGroup -> {
                    setCeilingMounted(visibleNode)
                }

//                else -> {//不显示吸顶，原则此处代码不会被调用
//                    binding.layoutGroup.root.translationY = -groupHeight
//                }
            }
        }

        override fun onAddAnimationFinished(item: RecyclerView.ViewHolder?) {
            Log.i(TAG, "onAddAnimationFinished: ")
        }

        override fun onRemoveAnimationFinished(item: RecyclerView.ViewHolder?) {
            Log.i(TAG, "onRemoveAnimationFinished: ")
        }

        override fun onChangeAnimationFinished(item: RecyclerView.ViewHolder?) {
            Log.i(TAG, "onChangeAnimationFinished: ")
        }
    }

    val dataList = mutableListOf(
        FriendGroup(
            "商周时期", mutableListOf(
                FriendInfo(R.mipmap.snow1, "姜子牙", -1, "天地不仁，以万物为刍狗！"),
                FriendInfo(R.mipmap.snow2, "妲己", 0, "尾巴，不止可以用来挠痒痒哦"),
            )
        ),
        FriendGroup(
            "春秋战国", mutableListOf(
                FriendInfo(R.mipmap.snow3, "孙膑", 1, "失去双脚，得到穿越时间的力量，这就是等价交换"),
                FriendInfo(R.mipmap.t1, "扁鹊", -1, "命不是廉价品，治疗很昂贵"),
                FriendInfo(R.mipmap.t2, "庄周", 2, "蝴蝶是我，我就是蝴蝶"),
                FriendInfo(R.mipmap.t3, "墨子", -1, "保持距离，才是文明之间的礼仪"),
            )
        ),
        FriendGroup(
            "秦汉时期", mutableListOf(
                FriendInfo(R.mipmap.t4, "嬴政", 3, ""),
                FriendInfo(R.mipmap.t5, "项羽", -1, ""),
                FriendInfo(R.mipmap.t6, "刘邦", 1, ""),
            )
        ),
        FriendGroup(
            "魏国", mutableListOf(
                FriendInfo(R.mipmap.t7, "曹操", 2, "宁教我负天下人！"),
                FriendInfo(R.mipmap.t8, "司马懿", 1, ""),
                FriendInfo(R.mipmap.t9, "夏侯惇", 3, ""),
                FriendInfo(R.mipmap.t10, "甄姬", -1, ""),
            )
        ),
        FriendGroup(
            "蜀国", mutableListOf(
                FriendInfo(R.mipmap.y1, "刘备", 0, ""),
                FriendInfo(R.mipmap.y2, "关羽", -1, ""),
                FriendInfo(R.mipmap.y3, "张飞", 2, ""),
                FriendInfo(R.mipmap.y4, "赵云", -1, ""),
                FriendInfo(R.mipmap.y5, "黄忠", 0, ""),
                FriendInfo(R.mipmap.y6, "诸葛亮", 3, ""),
                FriendInfo(R.mipmap.y7, "马超", 0, "月映枪冷，战甲在身。"),
                FriendInfo(R.mipmap.y8, "刘禅", -1, ""),
            )
        ),
        FriendGroup(
            "吴国", mutableListOf(
                FriendInfo(R.mipmap.y9, "孙策", 2, ""),
                FriendInfo(R.mipmap.y10, "孙权", 1, ""),
                FriendInfo(R.mipmap.snow1, "周瑜", 0, ""),
                FriendInfo(R.mipmap.snow2, "孙尚香", -1, "大小姐驾到，通通闪开！"),
                FriendInfo(R.mipmap.snow3, "大乔", 3, ""),
                FriendInfo(R.mipmap.t1, "小乔", 0, "小乔，要努力变强"),
            )
        ),
        FriendGroup(
            "隋唐时期", mutableListOf(
                FriendInfo(R.mipmap.t2, "武则天", 1, ""),
                FriendInfo(R.mipmap.t3, "李白", 1, "马超：“月映枪冷，战甲在身。”"),
                FriendInfo(R.mipmap.t4, "狄仁杰", 2, ""),
                FriendInfo(R.mipmap.t5, "上官婉儿", 3, ""),
                FriendInfo(R.mipmap.t6, "程咬金", 0, "一个字，干！"),
            )
        ),
        FriendGroup(
            "其他朝代/神话", mutableListOf(
                FriendInfo(R.mipmap.t7, "女娲", -1, ""),
                FriendInfo(R.mipmap.t8, "盘古", 0, ""),
                FriendInfo(R.mipmap.t9, "哪吒", 1, ""),
                FriendInfo(R.mipmap.t10, "杨戬", 2, ""),
                FriendInfo(R.mipmap.y1, "猪八戒", 3, ""),
                FriendInfo(R.mipmap.y2, "孙悟空", 0, ""),
                FriendInfo(R.mipmap.y3, "金蝉", 1, ""),
                FriendInfo(R.mipmap.y3, "后羿", 1, "觉醒吧,猎杀时刻。"),
            )
        ),

        )

    data class FriendGroup(val name: String, val friendList: MutableList<FriendInfo>) : NodeEntity<Unit, FriendInfo>,
        ExpandedNodeEntity {
        override var xParentNodeEntity: Unit? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<FriendInfo> {
            return friendList
        }

        override var xIsExpanded: Boolean? = null
            get() {
                return if (field == null) false else field
            }

    }

    data class FriendInfo(val head: Int, val name: String, val state: Int, val signature: String?) : NodeEntity<FriendGroup, Unit> {
        override var xParentNodeEntity: FriendGroup? = null
        override var xNodeGrade: Int? = null

        override fun getChildNodeEntityList(): MutableList<Unit>? {
            return null
        }

        fun description(): Spanned? {
            val stateStr = when (state) {
                0 -> "<font color='#00FF00'>●</font>在线"
                1 -> "<font color='#00FF00'>●</font>WiFi在线"
                2 -> "<font color='#00FF00'>●</font>4G在线"
                3 -> "<font color='#00FF00'>●</font>5G在线"
                else -> null
            }
            val description = if (stateStr != null && !signature.isNullOrBlank())
                "${stateStr}|${signature}"
            else if (stateStr != null)
                "$stateStr"
            else if (!signature.isNullOrBlank())
                "$signature"
            else null
            return if (description == null) null else Html.fromHtml(description)
        }
    }
}