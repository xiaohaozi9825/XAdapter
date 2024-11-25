package pw.xiaohaozi.xadapter.dialog

import android.content.Context
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import pw.xiaohaozi.xadapter.R
import kotlin.coroutines.resume


class Dialog(context: Context) : CenterPopupView(context) {

    var btnConfirm: TextView? = null
    var btnCancel: TextView? = null
    var tvTitle: TextView? = null
    var tvMsg: TextView? = null
    var onCancelListener1: OnClickListener? = null
    var onConfirmListener1: OnClickListener? = null
    var onDismiss: (() -> Unit)? = null

    var title: String = "温馨提示"
        private set
    var msg: String? = null
        private set
    var confirm = "确定"
        private set
    var cancel = "取消"
        private set

    // 返回自定义弹窗的布局
    override fun getImplLayoutId(): Int {
        return R.layout.dialog
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

        btnConfirm?.text = confirm
        btnCancel?.text = cancel

        btnConfirm?.visibility = if (onConfirmListener1 == null) GONE else VISIBLE
        btnCancel?.visibility = if (onCancelListener1 == null) GONE else VISIBLE

        btnConfirm?.setOnClickListener {
            dismiss() // 关闭弹窗
            onConfirmListener1?.onClick(it)
        }
        btnCancel?.setOnClickListener {
            dismiss() // 关闭弹窗
            onCancelListener1?.onClick(it)
        }
    }


    fun onConfirm(onClickListener: OnClickListener): Dialog {
        this.onConfirmListener1 = onClickListener
        confirm = "确定"
        return this
    }

    fun onConfirm(text: String, onClickListener: OnClickListener?): Dialog {
        this.onConfirmListener1 = onClickListener
        confirm = text
        return this
    }

    fun onCancel(onClickListener: OnClickListener?): Dialog {
        this.onCancelListener1 = onClickListener
        cancel = "取消"
        return this
    }

    fun onCancel(text: String, onClickListener: OnClickListener?): Dialog {
        this.onCancelListener1 = onClickListener
        cancel = text
        return this
    }

    fun setMsg(msg: String): Dialog {
        this.msg = msg
        return this
    }

    fun setTitle(title: String): Dialog {
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
    override fun show(): Dialog {
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
    fun onDismiss(dismiss: () -> Unit): Dialog {
        this.onDismiss = dismiss
        return this
    }

    override fun onDismiss() {
        super.onDismiss()
        onDismiss?.invoke()
    }
}

/**
 * 只有点击确定，会执行后续代码
 */
suspend fun Context.showDialogForConfirm(

    msg: String,
    title: String = "温馨提示",
    confirm: String = "确定",
    cancel: String? = null,
) {
    callbackFlow {
        val dialog =
            Dialog(this@showDialogForConfirm)
                .setTitle(title)
                .setMsg(msg)
                .onConfirm(confirm) { trySend(Unit) }
                .onCancel(cancel ?: "取消", if (cancel == null) null else OnClickListener { })
                .onDismiss { cancel() }
                .show()
        awaitClose { dialog.dismiss() }
    }.first()
}

suspend fun Context.showDialog(
    msg: String,
    title: String = "温馨提示",
    confirm: String = "确定",
    cancel: String? = null,
) = suspendCancellableCoroutine { continuation ->
    Dialog(this)
        .setTitle(title)
        .setMsg(msg)
        .onConfirm(confirm) { continuation.resume(Unit) }
        .onCancel(cancel ?: "取消", if (cancel == null) null else OnClickListener { })
        .onDismiss { continuation.cancel() }
        .show()

}

/**
 * 只有点击取消，会执行后续代码
 */
suspend fun Context.showDialogForCancel(
    msg: String,
    title: String = "温馨提示",
    confirm: String? = null,
    cancel: String = "取消",
) {
    callbackFlow {
        val dialog =
            Dialog(this@showDialogForCancel)
                .setTitle(title)
                .setMsg(msg)
                .onConfirm(confirm ?: "确定", if (confirm == null) null else OnClickListener { })
                .onCancel(cancel) { trySend(Unit) }
                .onDismiss { cancel() }
                .show()
        awaitClose { dialog.dismiss() }
    }.first()
}

/**
 * @return 点击确定，返回true；点击取消，返回false；点空白处或返回触发弹窗关闭，返回null
 */
suspend fun Context.showDialogForResult(
    msg: String,
    title: String = "温馨提示",
    confirm: String = "确定",
    cancel: String = "取消",
): Boolean? {
    return callbackFlow {
        val dialog =
            Dialog(this@showDialogForResult)
                .setTitle(title)
                .setMsg(msg)
                .onConfirm(confirm) { trySend(true) }
                .onCancel(cancel) { trySend(false) }
                .onDismiss { trySend(null) }
                .show()
        awaitClose { dialog.dismiss() }
    }.first()
}

suspend fun Fragment.showDialogForConfirm(
    msg: String,
    title: String = "温馨提示",
    confirm: String = "确定",
    cancel: String? = null,
) {
    return requireActivity().showDialogForConfirm(msg, title, confirm, cancel)
}

suspend fun Fragment.showDialogForCancel(
    msg: String,
    title: String = "温馨提示",
    confirm: String? = null,
    cancel: String = "取消",
) {
    return requireActivity().showDialogForCancel(msg, title, confirm, cancel)
}

suspend fun Fragment.showDialogForResult(
    msg: String,
    title: String = "温馨提示",
    confirm: String = "确定",
    cancel: String = "取消",
): Boolean? {
    return requireActivity().showDialogForResult(msg, title, confirm, cancel)
}