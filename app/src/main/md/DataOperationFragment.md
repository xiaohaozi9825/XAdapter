## 数据操作
> 对数据增删改查操作


#### 核心方法

```kotlin
    /**
     * 设置数据
     * 会替换原来的数组对象
     * Differ模式下不可用
     */
    fun <L : MutableList<D>> setList(list: L)

    /**
     * 刷新数据
     * 会保留原数组对象
     * Differ模式下不可用
     */
    fun <L : Collection<D>> refresh(list: L)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun <L : Collection<D>> add(list: L)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun add(data: D)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun add(index: Int, data: D)

    /**
     * 添加数据
     * Differ模式下不可用
     */
    fun <L : Collection<D>> add(index: Int, list: L)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun removeAt(index: Int)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun remove(start: Int, count: Int)

    /**
     * 删除数据
     * Differ模式下不可用
     */
    fun remove(data: D)

    /**
     * 删除数据
     * 改方法会刷新整个列表
     * Differ模式下不可用
     */
    fun <L : Collection<D>> remove(list: L)

    /**
     * 删除所有数据
     * Differ模式下不可用
     */
    fun remove()

    /**
     * 修改数据
     */
    fun updateAt(index: Int, data: D, payload: Any? = null)

    /**
     * 按索引修改数据
     */
    fun updateAt(index: Int, payload: Any? = null)

    /**
     * 修改数据
     */
    fun update(data: D, payload: Any? = null)

    /**
     * 修改数据
     */
    fun <L : Collection<D>> update(list: L, payload: Any? = null)

    /**
     * 交换数据
     * Differ模式下不可用
     */
    fun swap(fromPosition: Int, toPosition: Int)

```





