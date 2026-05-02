package pw.xiaohaozi.xadapter.utils

import android.content.Context
import android.provider.MediaStore

class LoadMediaGroup(
    val bucketId: Long?,
    val name: String,
    val mutableList: MutableList<LoadMediaFile>,
)

class LoadMediaFile(
    val bucketId: Long?,
    val bucketDisplayName: String?,
    val mimeType: String?,
    val id: Long?,
    val name: String?,
    val path: String?,
    val duration: Long?,
    /**
     * 上传状态0上传中，1成功，2失败，3审核失败
     */
    var uploadStatus: Int? = null
)

class LoadLocalMedia {
    val TAG = "LoadLocalMedia"
    fun getLoadFiles(context: Context): MutableList<MutableList<LoadMediaFile>> {
        var groupList = mutableListOf<MutableList<LoadMediaFile>>()
        val cursor = context.contentResolver.query(
            QUERY_URI,
            PROJECTION /*  ALL_PROJECTION*/,
            "(media_type=? AND (mime_type!='image/gif') OR media_type=? AND 0 <= duration and duration <= 9223372036854775807) AND 0 <= _size and _size <= 9223372036854775807",
            arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
            ),
            ORDER_BY
        ) ?: return mutableListOf()

//            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME};

        //asc 按升序排列
        //    desc 按降序排列
        //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};
//                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc");
        try {
            val count = cursor.count
            if (cursor.columnCount > 0) {
                val first = mutableListOf<LoadMediaFile>()
                while (cursor.moveToNext()) {
                    val imageId = try {
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID))
                    } catch (e: IllegalArgumentException) {
                        null
                    }

                    val fileName = try {
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                    } catch (e: IllegalArgumentException) {
                        null
                    }

                    val filePath = try {
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                    } catch (e: IllegalArgumentException) {
                        null
                    }

                    val bucketId = try {
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_BUCKET_ID))
                    } catch (e: IllegalArgumentException) {
                        null
                    }

                    val bucketDisplayName =
                        try {
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUCKET_DISPLAY_NAME))
                        } catch (e: IllegalArgumentException) {
                            null
                        }

                    val mimeType =
                        try {
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                        } catch (e: IllegalArgumentException) {
                            null
                        }

                    val duration =
                        try {
                            cursor.getLong(cursor.getColumnIndexOrThrow("duration"))
                        } catch (e: IllegalArgumentException) {
                            null
                        }

                    first.add(
                        LoadMediaFile(
                            bucketId,
                            bucketDisplayName,
                            mimeType,
                            imageId,
                            fileName,
                            filePath,
                            duration,
                        )
                    )
//                            Logger.i("查询媒体文件", imageId + " -- " + fileName + " -- " + filePath + " --- " + bucketId);
//                    Logger.i("查询媒体文件", "$bucketId -- $bucketDisplayName -- $mimeType")
                }
                val group = first.groupByTo(LinkedHashMap()) { it.bucketId }
                    .values
                    .filterTo(mutableListOf()) { it.size > 0 }
                group.add(0, first)
                groupList = group
            }
        } catch (e: Exception) {
        } finally {
            if (!cursor.isClosed) {
                cursor.close()
            }
        }
        return groupList
    }

    companion object {
        protected val QUERY_URI = MediaStore.Files.getContentUri("external")
        protected const val ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"
        protected const val NOT_GIF =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif')"
        protected const val NOT_WEBP =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/webp')"
        protected const val NOT_BMP =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/bmp')"
        protected const val NOT_XMS_BMP =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/x-ms-bmp')"
        protected const val NOT_VND_WAP_BMP =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/vnd.wap.wbmp')"
        protected const val NOT_HEIC =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/heic')"
        protected const val GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id"
        protected const val DISTINCT_BUCKET_Id = "DISTINCT bucket_id"
        protected const val COLUMN_COUNT = "count"
        protected const val COLUMN_BUCKET_ID = "bucket_id"
        protected const val COLUMN_DURATION = "duration"
        protected const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
        protected const val COLUMN_ORIENTATION = "orientation"
        protected const val MAX_SORT_SIZE = 60

        /**
         * A list of which columns to return. Passing null will return all columns, which is inefficient.
         */
        protected val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION
        )

        /**
         * A list of which columns to return. Passing null will return all columns, which is inefficient.
         */
        protected val ALL_PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION,
            "COUNT(*) AS " + COLUMN_COUNT
        )
    }
}