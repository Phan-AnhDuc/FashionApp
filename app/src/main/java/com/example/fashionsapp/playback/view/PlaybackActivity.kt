package com.example.fashionsapp.playback.view

import VNTTCamManager
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.LongSparseArray
import android.util.TimeUtils
import android.view.View
import androidx.core.util.forEach
import androidx.lifecycle.MutableLiveData
import com.example.fashionsapp.R
import com.example.fashionsapp.base.ColorData
import com.example.fashionsapp.base.Constant
import com.example.fashionsapp.data.PlaybackFileModel
import com.example.fashionsapp.data.ResourcesService
import com.example.fashionsapp.databinding.ActivityPlaybackBinding
import com.example.fashionsapp.databinding.FragmentAccountBinding
import com.example.fashionsapp.di.SchedulerProvider
import com.vnpttech.ipcamera.VNPTCamera
import com.vnpttech.model.DeviceInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import timber.log.Timber

class PlaybackActivity : AppCompatActivity() {

    private var _binding: ActivityPlaybackBinding? = null
    private val binding get() = _binding!!

    private var startDayQueryPlayback: Long = Long.MIN_VALUE


    private lateinit var camera: VNPTCamera

    private lateinit var vnttCamManager: VNTTCamManager

    private lateinit var compositeDisposable: CompositeDisposable

    lateinit var schedulerProvider: SchedulerProvider

    private val _cameraInfo: MutableLiveData<DeviceInfo> by lazy { MutableLiveData() }

    var currentFilePlay: PlaybackFileModel? = null

    private val _dateList: MutableLiveData<ArrayList<Long>> by lazy { MutableLiveData() }

    private val _playbackState: MutableLiveData<Constant.PlaybackSdCardStateUI> by lazy { MutableLiveData() }

    lateinit var resourcesService: ResourcesService

    private var startTimeOfSelectedDay =
        getStartOfDay(System.currentTimeMillis())

    private var emptyFileAllDay: Boolean =
        true

    private var eventToPlay: Constant.PlayPlaybackFileEvent =
        Constant.PlayPlaybackFileEvent.FIRST_TIME

    private var listPlayback: LongSparseArray<ArrayList<PlaybackFileModel>> = LongSparseArray()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlaybackBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_playback)
        setContentView(binding.root)

        val uid = intent.getStringExtra("UID")
        val pass = intent.getStringExtra("PASS")

        createCameraManager(uid.toString(),pass.toString())

        camera.connect(0,(0x7A).toChar())

        binding.buttonGetList.setOnClickListener {
            getListVideoPlayBack(Long.MIN_VALUE)
           camera.getDeviceInfo()
            val test1 =  camera.getCameraUID()
            val test3 = camera.getRssi()
            Log.d("testtt", "$test1")
            Log.d("testtt", "$test3")
        }

    }


    private fun createCameraManager(uid: String, pass: String) {
        if (!::camera.isInitialized) {
            camera = VNPTCamera(uid, pass)
        }
    }

    @SuppressLint("CheckResult")
    private fun getListVideoPlayBack(startDay: Long){
        val startTime = TimeUnit.MILLISECONDS.toSeconds(startDay).toInt()
        val endTime = TimeUnit.MILLISECONDS.toSeconds(getEndOfDay(startDay)).toInt()
        Observable.create<Int> { emitter ->
            emitter.onNext(
                camera.getAllVideoTime(
                    startTime,
                    endTime,
                    -1,
                    0
                )
            )
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                startDayQueryPlayback = startDay
            }, {})
    }

    private fun observeListVideoPlayback() {
        compositeDisposable.add(
            vnttCamManager.observeListVideoPlayback()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .map { data ->
                    data.forEach { _, listVideoInDay ->
                        // Phải là isNullOrEmpty() -> không được dùng isNotEmpty()
                        if (!listVideoInDay.isNullOrEmpty()) {
                            listVideoInDay.forEach { file ->
                                val decreaseDuration = file.duration - 1
                                file.duration = decreaseDuration
                            }
                        }
                    }

                    return@map data
                }
                .subscribe({ playbackData ->
                    processPlaybackData(playbackData)
                },
                    {

                })
        )
    }




    fun processPlaybackData(
        playbackData: LongSparseArray<ArrayList<PlaybackFileModel>>
    ) {
        val timeKeyIndex = playbackData.indexOfKey(startTimeOfSelectedDay)
        val timeKey = playbackData.keyAt(timeKeyIndex)
        val dataOfDay = playbackData.get(timeKey)
        if (dataOfDay.isNullOrEmpty()) {
            listPlayback.put(timeKey, arrayListOf())
            currentFilePlay = PlaybackFileModel("", "", 0, 0)

            when (eventToPlay) {
                Constant.PlayPlaybackFileEvent.SELECT_DAY -> {
                    setPlaybackStateUI(Constant.PlaybackSdCardStateUI.EMPTY_FILE)
                }


//                Constant.PlayPlaybackFileEvent.SEEK -> {
//                    processSeekVideoInEmptyFile()
//                }

//                Constant.PlayPlaybackFileEvent.SCAN_NEXT -> {
//                    if (isNextDay && isCurrentDay()) {
//                        // Trường hợp đã play hết ngày đang play -> tự động nhảy sang ngày mới để play
//                        // Nếu quét đến ngày hiện tại để lấy data mới nhất, nhưng không có dữ liệu
//                        // ==> play complete (hoàn thành hết tất cả các ngày)
//                        setPlaybackStateUI(Constant.PlaybackSdCardStateUI.COMPLETE)
//                        isPaused =
//                            true //Khi hoàn thành phải set ispaused = true k thì vào hàm stop playback là chết luôn
//                    } else {
//                        scanNextDay()
//                    }
//                }

                else -> {
                    // Trường hợp lần đầu vào playback
                    // Khi query file playback từ cam, nếu ngày đó không có dữ liệu
                    // Quét tất cả các ngày, ngày nào có dữ liệu thì play
                    _dateList.value?.let { dates ->
                        if (dates.indexOf(startTimeOfSelectedDay) == 0) {
                            // Đã quét đến ngày cuối cùng mà vẫn không có data playback
                            if (emptyFileAllDay) {
                                setPlaybackStateUI(Constant.PlaybackSdCardStateUI.EMPTY_FILE_ALL_DAY)
                            }
                        } else {
                            scanPreviousDay()
                        }
                    }
                }
            }
        } else {
            listPlayback.put(timeKey, dataOfDay)
            emptyFileAllDay = false
            setDataToTimelineAndPlayFile(dataOfDay)
        }
    }
    private fun scanPreviousDay() {
        _dateList.value?.let { dates ->
            val indexDay = dates.indexOf(startTimeOfSelectedDay)
            if (indexDay != 0) {
                // Ngày hiện tại -> bắt đầu quét về quá khứ
                startTimeOfSelectedDay = dates[indexDay - 1]
                setEventToPlay(Constant.PlayPlaybackFileEvent.SCAN_PREVIOUS)
                setPlaybackStateUI(Constant.PlaybackSdCardStateUI.SCAN)
//                startQueryFileAfterScan()
            }
        }
    }

    private fun setDataToTimelineAndPlayFile(
        dataOfDay: ArrayList<PlaybackFileModel>
    ) {
        compositeDisposable.add(
            Observable.create<ArrayList<ColorData>> { emitter ->
                val listColorData = ArrayList<ColorData>()
                var start: Long
                var end: Long
                val color = resourcesService.getColor(R.color.orange)

                dataOfDay.forEach { playbackFile ->
                    start = convertSecondToMillis(playbackFile.timestamp)
                    end = convertSecondToMillis(playbackFile.timestamp) + convertSecondToMillis(
                        playbackFile.duration
                    )

                    listColorData.add(
                        ColorData(start, end, color)
                    )
                }

                emitter.onNext(listColorData)
            }.subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe { colorData ->
                    _colorDataTimeline.postValue(colorData)
                    playVideoPlayback(eventToPlay)
                }
        )
    }

    private fun convertSecondToMillis(time: Int): Long {
        return TimeUnit.SECONDS.toMillis(time.toLong())
    }

//    private fun startQueryFileAfterScan() {
//        if (checkDataExist()) {
//            // Trong trường hợp đã play hết ngày, và tự động nhảy sang ngày mới
//            // Nếu đã nhảy tới ngày hiện tại thì query tới cam để lấy data mới nhất
//            if (isNextDay && isCurrentDay()) {
//                getListVideoPlaybackFromCamera(startTimeOfSelectedDay)
//            } else {
//                // Nếu ngày được chọn đã có trong listPlayback, kiểm tra xem ngày đó đã query hết tất cả các page hay chưa,
//                // Nếu đã query hết tất cả các page thì chỉ việc lấy ra và play
//                // Nếu chưa query hết được tất cả các page thì query lại từ đầu với page = 0
//                if (isGetFullPage()) {
//                    processPlaybackDataAvailable()
//                } else {
//                    getListVideoPlaybackFromCamera(startTimeOfSelectedDay)
//                }
//            }
//        } else {
//            getListVideoPlaybackFromCamera(startTimeOfSelectedDay)
//        }
//    }

    fun setEventToPlay(event: Constant.PlayPlaybackFileEvent) {
        eventToPlay = event
    }

    private fun processSeekVideoInEmptyFile() {
        if (emptyFileAllDay) {
            setPlaybackStateUI(Constant.PlaybackSdCardStateUI.EMPTY_FILE_ALL_DAY)
        } else {
            scanPreviousDay()
        }
    }

    private fun setPlaybackStateUI(state: Constant.PlaybackSdCardStateUI) {
        _playbackState.postValue(state)
    }


    private fun getEndOfDay(currentTime: Long): Long {
        val start = getStartOfDay(currentTime)
        return start + 24 * 60 * 60 * 1000 - 1
    }

    private fun getStartOfDay(currentTime: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(currentTime)
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }
}