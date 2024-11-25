package pw.xiaohaozi.xadapter.dialog

import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView


fun Context.loadingDialog(
    title: CharSequence? = null,
    style: LoadingPopupView.Style = LoadingPopupView.Style.Spinner
): LoadingPopupView {
    val loadingPopupView = XPopup.Builder(this).animationDuration(120).asLoading(title, style)
    loadingPopupView.show()
    return loadingPopupView
}