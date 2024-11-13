package com.lumko.teachme.presenterImpl

import com.lumko.teachme.manager.App
import com.lumko.teachme.manager.Utils
import com.lumko.teachme.manager.listener.ItemCallback
import com.lumko.teachme.manager.net.ApiService
import com.lumko.teachme.manager.net.CustomCallback
import com.lumko.teachme.manager.net.OnDownloadProgressListener
import com.lumko.teachme.manager.net.RetryListener
import com.lumko.teachme.model.*
import com.lumko.teachme.presenter.Presenter
import com.lumko.teachme.ui.frag.BaseCourseChapterItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CourseChapterItemPresenterImpl(private val frag: BaseCourseChapterItem) :
    Presenter.CourseChapterItemPresenter {

    companion object {
        val downloadingRequests: HashMap<Int, Call<*>> = HashMap()
    }

    override fun getSessionItemDetails(url: String, callback: ItemCallback<ChapterSessionItem>) {
        val sessionChapterItemDetails = ApiService.apiClient!!.getSessionChapterItemDetails(url)
        frag.addNetworkRequest(sessionChapterItemDetails)
        sessionChapterItemDetails.enqueue(object : CustomCallback<Data<ChapterSessionItem>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getSessionItemDetails(url, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<ChapterSessionItem>>,
                response: Response<Data<ChapterSessionItem>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getFileItemDetails(url: String, callback: ItemCallback<ChapterFileItem>) {
        val fileChapterItemDetails = ApiService.apiClient!!.getFileChapterItemDetails(url)
        frag.addNetworkRequest(fileChapterItemDetails)
        fileChapterItemDetails.enqueue(object : CustomCallback<Data<ChapterFileItem>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getFileItemDetails(url, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<ChapterFileItem>>,
                response: Response<Data<ChapterFileItem>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }

//    override fun getTextLessonItemDetails(url: String, callback: ItemCallback<ChapterTextItem>) {
//        ApiService.apiClient!!.getTextLessonChapterItemDetails(url)
//            .enqueue(object : CustomCallback<Data<ChapterTextItem>> {
//                override fun onStateChanged(): RetryListener {
//                    return RetryListener {
//                        getTextLessonItemDetails(url, callback)
//                    }
//                }
//
//                override fun onResponse(
//                    call: Call<Data<ChapterTextItem>>,
//                    response: Response<Data<ChapterTextItem>>
//                ) {
//                    if (response.body() != null) {
//                        callback.onItem(response.body()!!.data!!)
//                    }
//                }
//            })
//    }

    override fun changeItemStatus(chapterItemMark: ChapterItemMark) {
        ApiService.apiClient!!.changeLessonItemStatus(chapterItemMark.courseId, chapterItemMark)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changeItemStatus(chapterItemMark)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onItemStatusChanged(response.body()!!, chapterItemMark)
                    }
                }
            })
    }

    override fun downloadFile(
        fileItem: ChapterFileItem,
        progressListener: OnDownloadProgressListener
    ) {
        val fileId = fileItem.id

        val baseUrlAndHost = Utils.getBaseUrlAndHostFromUrl(fileItem.file) ?: return

        val downloadRequest =
            ApiService.getDownloadApiClient(baseUrlAndHost[0], progressListener, fileId)
                .download(baseUrlAndHost[1])

        downloadingRequests[fileId] = downloadRequest

        downloadRequest.enqueue(object : CustomCallback<ResponseBody> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    downloadFile(fileItem, progressListener)
                }
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    frag.context?.let {

                        val fileExtension =
                            if (fileItem.fileType.uppercase() == ChapterFileItem.ARCHIVE_FILE_TYPE) {
                                Utils.extractFileExtensionFromString(baseUrlAndHost[1])
                            } else {
                                fileItem.fileType
                            }

                        Thread {
                            val byteStream = response.body()!!.byteStream()
                            val filePath = Utils.saveFile(
                                frag.requireContext(),
                                App.Companion.Directory.DOWNLOADS.value(),
                                "${fileId}.${fileExtension}",
                                byteStream
                            )

                            if (filePath != null) {
                                Utils.copyFileToDownloads(
                                    frag.requireContext(),
                                    File(filePath),
                                    "${fileItem.title}.${fileExtension}"
                                )
                            }

                        }.start()
                    }
                }
            }
        })
    }

    override fun cancelDownload(fileId: Int) {
        if (downloadingRequests.containsKey(fileId)) {
            downloadingRequests[fileId]!!.cancel()
            downloadingRequests.remove(fileId)
        }
    }

    override fun getTextLessons(textLessonId: Int, callback: ItemCallback<List<ChapterTextItem>>) {
        val textLessons = ApiService.apiClient!!.getTextLessons(textLessonId)
        frag.addNetworkRequest(textLessons)
        textLessons.enqueue(object :
            CustomCallback<Data<List<ChapterTextItem>>> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getTextLessons(textLessonId, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<List<ChapterTextItem>>>,
                response: Response<Data<List<ChapterTextItem>>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}