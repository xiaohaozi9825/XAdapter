package pw.xiaohaozi.xadapter.fragment.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import pw.xiaohaozi.xadapter.utils.LoadLocalMedia
import pw.xiaohaozi.xadapter.utils.LoadMediaFile

class ImageSelectedViewModel(application: Application) : AndroidViewModel(application) {

    // 获取全局 Application Context
    private val appContext = getApplication<Application>().applicationContext

    val curPosition = MutableStateFlow(0)
    val curMediaList = MutableStateFlow<MutableList<LoadMediaFile>>(mutableListOf())
    val allMediaList = MutableStateFlow<MutableList<MutableList<LoadMediaFile>>>(mutableListOf())
    suspend fun initData() = withContext(IO) {
        allMediaList.value = LoadLocalMedia().getLoadFiles(appContext)
        if (allMediaList.value.isNotEmpty()) {
            curMediaList.value = allMediaList.value[0]
        }
    }
}