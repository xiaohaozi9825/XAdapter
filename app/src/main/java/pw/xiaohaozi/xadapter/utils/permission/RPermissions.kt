package pw.xiaohaozi.myvideo.utils.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import com.hjq.permissions.IPermissionInterceptor
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import pw.xiaohaozi.xadapter.R
import pw.xiaohaozi.xadapter.dialog.Dialog
import pw.xiaohaozi.xadapter.utils.permission.JumpPermissionManagement


class RPermissions(val context: Context) {
    private val xxPermissions: XXPermissions = XXPermissions.with(context)
    private var use: Array<out String>? = null
    fun permissions(vararg group: Array<String>): RPermissions {
        xxPermissions.permission(*group)
        return this
    }

    fun permissions(vararg permission: String): RPermissions {
        xxPermissions.permission(*permission)
        return this
    }

    fun permissions(list: List<String>): RPermissions {
        xxPermissions.permission(list)
        return this
    }

    /**
     * 权限用途
     * @param use 用途描述
     */
    fun use(vararg use: String): RPermissions {
        this.use = use
        return this
    }

    fun request(call: Runnable) {
        xxPermissions.unchecked().interceptor(object : IPermissionInterceptor {
            override fun launchPermissionRequest(
                activity: Activity,
                allPermissions: MutableList<String>,
                callback: OnPermissionCallback?
            ) {
//                AlertDialog.Builder(activity).setMessage("cadfasdfasdfasddf").show()
                val msg = "“${context.getString(R.string.app_name)}”想访问您的${
                    allPermissions.map { permission: String -> permissionName(permission) }
                        .joinToString("、")
                }权限，用于${use?.joinToString("、")}功能"
                Dialog(activity).setMsg(msg).onConfirm {
                    super.launchPermissionRequest(activity, allPermissions, callback)
                }
                    .setTitle("应用授权说明")
                    .onCancel {}
                    .show()

            }
        }).request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                if (!allGranted) {
//                    "获取部分权限成功，但部分权限未正常授予".toastLong()
                    return
                }
                call.run()
//                "获取录音和日历权限成功".toastLong()
            }

            override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                if (doNotAskAgain) {
//                        "被永久拒绝授权，请手动授予${hint(permissions)}权限".toastLong()
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    showLackPermissionDialog(
                        context,
                        permissions.map { permission: String -> permissionName(permission) })
                } else {
//                    "获取${permissions.map { permission: String -> permissionName(permission) }}限失败".toastLong()
                }
            }
        })
    }


    private fun permissionName(permission: String): String {
        return when (permission) {
            Permission.GET_INSTALLED_APPS -> "读取应用列表"
            Manifest.permission.SCHEDULE_EXACT_ALARM -> "闹钟"
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> "文件管理"
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> "安装应用"
            Permission.PICTURE_IN_PICTURE -> "画中画"
            Manifest.permission.SYSTEM_ALERT_WINDOW -> "悬浮窗"
            Manifest.permission.WRITE_SETTINGS -> "系统设置"
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> "请求忽略电池优化选项"
            Manifest.permission.ACCESS_NOTIFICATION_POLICY -> "勿扰"
            Manifest.permission.PACKAGE_USAGE_STATS -> " 查看应用使用情况"
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE -> "通知栏监听"
            Manifest.permission.BIND_VPN_SERVICE -> "VPN"
            Permission.NOTIFICATION_SERVICE -> "通知栏"
            Permission.POST_NOTIFICATIONS -> "发送通知"
            Permission.NEARBY_WIFI_DEVICES -> "WIFI"
            Permission.BODY_SENSORS_BACKGROUND -> "后台传感器"
            Permission.READ_MEDIA_IMAGES -> "读取图片"
            Permission.READ_MEDIA_VIDEO -> "读取视频"
            Permission.READ_MEDIA_AUDIO -> "读取音频"
            Manifest.permission.BLUETOOTH_SCAN -> "蓝牙扫描"
            Manifest.permission.BLUETOOTH_CONNECT -> "蓝牙连接"
            Manifest.permission.BLUETOOTH_ADVERTISE -> "蓝牙广播"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "在后台获取位置"
            Manifest.permission.ACTIVITY_RECOGNITION -> "获取活动步数"
            Manifest.permission.ACCESS_MEDIA_LOCATION -> "读取照片中的地理位置"
            Manifest.permission.ACCEPT_HANDOVER -> "呼叫应用继续在另一个应用中启动的呼叫"
            Manifest.permission.READ_PHONE_NUMBERS -> "读取手机号码"
            Manifest.permission.ANSWER_PHONE_CALLS -> "接听电话"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "读取外部存储"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "写入外部存储"
            Manifest.permission.CAMERA -> "相机"
            Manifest.permission.RECORD_AUDIO -> "麦克风"
            Manifest.permission.ACCESS_FINE_LOCATION -> "获取精确位置"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "获取粗略位置"
            Manifest.permission.READ_CONTACTS -> "读取联系人"
            Manifest.permission.WRITE_CONTACTS -> "修改联系人"
            Manifest.permission.GET_ACCOUNTS -> "访问账户列表"
            Manifest.permission.READ_CALENDAR -> "读取日历"
            Manifest.permission.WRITE_CALENDAR -> "修改日历"
            Manifest.permission.READ_PHONE_STATE -> "读取电话状态"
            Manifest.permission.CALL_PHONE -> "拨打电话"
            Manifest.permission.READ_CALL_LOG -> "读取通话记录"
            Manifest.permission.WRITE_CALL_LOG -> "修改通话记录"
            Manifest.permission.ADD_VOICEMAIL -> "添加语音邮件"
            Manifest.permission.USE_SIP -> "使用SIP视频"
            Manifest.permission.PROCESS_OUTGOING_CALLS -> "处理拨出电话"
            Manifest.permission.BODY_SENSORS -> "使用传感器"
            Manifest.permission.SEND_SMS -> "发送短信"
            Manifest.permission.RECEIVE_SMS -> "接收短信"
            Manifest.permission.READ_SMS -> "读取短信"
            Manifest.permission.RECEIVE_WAP_PUSH -> "接收 WAP 推送消息"
            Manifest.permission.RECEIVE_MMS -> "接收彩信"

            Manifest.permission_group.STORAGE/*,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE*/ -> "本地存储"

            Manifest.permission_group.CAMERA/*,
            Manifest.permission.CAMERA*/ -> "相机"

//            Manifest.permission.RECORD_AUDIO -> "麦克风"
//            Manifest.permission.READ_PHONE_NUMBERS -> "拨打电话和管理通话"
            else -> permission
        }
    }


    /**
     * 提示缺少必要权限对话框
     */
    private fun showLackPermissionDialog(context: Context, hint: List<String?>) {
        Dialog(context)
            .setTitle("${hint.joinToString("、")}权限申请")
            .setMsg("您拒绝了" + hint.joinToString("、") + "权限，请前往“设置-权限管理”开启")
            .onCancel("取消") { v -> }
            .onConfirm("去设置") { v ->
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    intent.setData(Uri.parse("package:" + context.getPackageName()));
//                    context.startActivity(intent);
                JumpPermissionManagement.GoToSetting(context)
            }
            .toXPopup().show()
    }

}


