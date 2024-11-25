package pw.xiaohaozi.xadapter.dialog

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import pw.xiaohaozi.xadapter.R


class InputDialog(context: Context) : CenterPopupView(context) {

    var btnConfirm: TextView? = null
    var btnCancel: TextView? = null
    var tvTitle: TextView? = null
    var tvMsg: TextView? = null
    var onCancelListener1: OnClickListener? = null
    var onConfirmListener1: ((View: View, content: String) -> Unit)? = null
    var onDismiss: (() -> Unit)? = null

    var title: String = "温馨提示"
        private set
    var msg: String? = null
        private set
    var hint: String? = null
        private set
    var confirm = "确定"
        private set
    var cancel = "取消"
        private set

    // 返回自定义弹窗的布局
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_input
    }

    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    override fun onCreate() {
        super.onCreate()
        btnConfirm = findViewById(R.id.btn_confirm)
        btnCancel = findViewById(R.id.btn_cancel)
        tvTitle = findViewById(R.id.tv_title)
        tvMsg = findViewById(R.id.tv_msg)

        tvTitle?.text = title
        tvMsg?.text = msg
        tvMsg?.hint = if (hint == null) msg else hint
        btnConfirm?.text = confirm
        btnCancel?.text = cancel

        btnConfirm?.visibility = if (onConfirmListener1 == null) GONE else VISIBLE
        btnCancel?.visibility = if (onCancelListener1 == null) GONE else VISIBLE

        btnConfirm?.setOnClickListener {
            dismiss() // 关闭弹窗
            onConfirmListener1?.invoke(it, tvMsg!!.text.toString())
        }
        btnCancel?.setOnClickListener {
            dismiss() // 关闭弹窗
            onCancelListener1?.onClick(it)
        }
    }


    fun onConfirm(onClickListener: (View: View, content: String) -> Unit): InputDialog {
        this.onConfirmListener1 = onClickListener
        confirm = "确定"
        return this
    }

    fun onConfirm(
        text: String,
        onClickListener: ((View: View, content: String) -> Unit)?
    ): InputDialog {
        this.onConfirmListener1 = onClickListener
        confirm = text
        return this
    }

    fun onCancel(onClickListener: OnClickListener?): InputDialog {
        this.onCancelListener1 = onClickListener
        cancel = "取消"
        return this
    }

    fun onCancel(text: String, onClickListener: OnClickListener?): InputDialog {
        this.onCancelListener1 = onClickListener
        cancel = text
        return this
    }

    fun setMsg(msg: String): InputDialog {
        this.msg = msg
        return this
    }

    fun setTitle(title: String): InputDialog {
        this.title = title
        return this
    }

    fun toXPopup(): BasePopupView {
        return XPopup.Builder(context)
//                    .enableShowWhenAppBackground(true)
//                    .dismissOnBackPressed(false) // 按返回键是否关闭弹窗，默认为true
//                    .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
            .asCustom(this)
    }

    //    @Deprecated("必须使用 toXPopup().show()", ReplaceWith("this.toXPopup().show()"), DeprecationLevel.ERROR)
    override fun show(): InputDialog {
        toXPopup()
        super.show()
        return this
    }

    //    companion object {
//        @JvmStatic
//        fun create(): Dialog {
//            return Dialog(BaseApplication.getInstance().currentActivity)
//        }
//    }
    fun onDismiss(dismiss: () -> Unit): InputDialog {
        this.onDismiss = dismiss
        return this
    }

    override fun onDismiss() {
        super.onDismiss()
        onDismiss?.invoke()
    }
}
