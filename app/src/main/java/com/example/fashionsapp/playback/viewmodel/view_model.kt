package com.example.fashionsapp.playback.viewmodel

import VNTTCamManager
import android.graphics.Bitmap
import android.os.Build
import android.util.LongSparseArray
import androidx.core.util.containsKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fashionsapp.base.ColorData
import com.example.fashionsapp.base.Constant
import com.example.fashionsapp.data.PlaybackFileModel
import com.example.fashionsapp.data.VideoStreamData
import com.example.fashionsapp.data.getStartOfDay
import com.vnpttech.ipcamera.Constants
import com.vnpttech.model.DeviceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import vn.vnpt.ONEHome.core.base.BaseViewModel
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class PlaybackSdCardViewModel @Inject constructor(
    private var vnttCamManager: VNTTCamManager,
) : BaseViewModel() {

    private val _dateList: MutableLiveData<ArrayList<Long>> by lazy { MutableLiveData() }

    private val _snapShotImage: MutableLiveData<String> by lazy { MutableLiveData() }
    val snapShotImage: LiveData<String> get() = _snapShotImage

    private val _videoRecordQuality: MutableLiveData<Int> by lazy { MutableLiveData() }
    val videoRecordQuality: LiveData<Int> get() = _videoRecordQuality

    private val _cameraState: MutableLiveData<Constant.CameraState> by lazy { MutableLiveData() }
    val cameraState: LiveData<Constant.CameraState> get() = _cameraState

    private val _playbackState: MutableLiveData<Constant.PlaybackSdCardStateUI> by lazy { MutableLiveData() }
    val playbackState: LiveData<Constant.PlaybackSdCardStateUI> get() = _playbackState

    private val _colorDataTimeline: MutableLiveData<List<ColorData>> by lazy { MutableLiveData() }
    val colorDataTimeline: LiveData<List<ColorData>> get() = _colorDataTimeline

    private val _dataVideoStream: PublishSubject<VideoStreamData> by lazy { PublishSubject.create() }
    val dataVideoStream: PublishSubject<VideoStreamData> get() = _dataVideoStream

    private val _cursorTimebar: MutableLiveData<Pair<Boolean, Long>> by lazy { MutableLiveData() }          // Boolean: Có lấy giá trị observe hay không, Long: giá trị timestamp gán vào timeline
    val cursorTimebar: LiveData<Pair<Boolean, Long>> get() = _cursorTimebar



    private val _timerRecordPlayback: MutableLiveData<Int> by lazy { MutableLiveData(0) }
    val timerRecordPlayback: LiveData<Int> get() = _timerRecordPlayback

    private val _snapshotPathRecordVideo: MutableLiveData<String> by lazy { MutableLiveData() }
    val snapshotPathRecordVideo: LiveData<String> get() = _snapshotPathRecordVideo



    private val _cameraInfo: MutableLiveData<DeviceInfo> by lazy { MutableLiveData() }
    val cameraInfo: LiveData<DeviceInfo> get() = _cameraInfo

    var firstTimeComeIn =
        System.currentTimeMillis()  // Thời gian của thời điểm lần đầu vào xem playback

    var isReconnect: Boolean = false

    private var cameraStateDisposable: Disposable? = null

    private var startTimeOfSelectedDay =
        getStartOfDay(firstTimeComeIn)     // Thời gian đầu ngày (00:00:00) của ngày được chọn

    private var disposableVideoStream: Disposable? = null

    private var disposableAudioStream: Disposable? = null

    private var disposableTimeStamp: Disposable? = null

    private var disposableVideoRecordStream: Disposable? = null

    private var hasDataVideo: Boolean = false

    private var startSeekEvent: Boolean = false

    private var isNextFile: Boolean = false

    private var isNextDay: Boolean = false

    /**
     * emptyFileAllDay được gán = false khi vào playback lần đầu ngày hiện tại đang có dữ liệu sẽ gán false
     * nếu không có dữ liệu sẽ next sang ngày hôm qua để check đến khi có dữ liệu sẽ vào bước gán false
     * thứ 2 khi vào bằng thông báo PHCĐ nếu ngày đầu tiên không có dữ liệu sẽ không next sang ngày hôm qua nên sẽ không tự gán bằng flase
     * nên chỉ trường hợp notficaion mới gán = false để sử dụng đúng luồng
     */
    private var emptyFileAllDay: Boolean =
        true                 // Tất cả các ngày đều không có dữ liệu playback

    private var isEnableAudio: Boolean = false

    private var isPaused: Boolean =
        false                       // Sự kiện pause khi người dùng bấm nút play/pause

    private var currentFilePlay: PlaybackFileModel? = null      // File playback đang được play

    private var currentFileName: String = Constant.EMPTY_STRING      // File playback đang được play

    private var seekTimeValue: Long = 0


    private val stopTimerRecord = AtomicBoolean(false)

    // Cache lại danh sách video playback của các ngày khi lấy từ camera, với key: timestamp của ngày được chọn, value là danh sách video playback trong ngày đó
    private var listPlayback: LongSparseArray<ArrayList<PlaybackFileModel>> = LongSparseArray()

    private var h265Path = Constant.EMPTY_STRING

    private var h264Path = Constant.EMPTY_STRING

    private var idCam = 0

    private var uidCam = Constant.EMPTY_STRING

    private var passCam = Constant.EMPTY_STRING

    private var isCameraOnline = false

    private var isCancelledPlayback = false

    /**
     * Set first time
     */

    /**
     * Set state play video playback
     */
    private fun setPlaybackStateUI(state: Constant.PlaybackSdCardStateUI) {
        _playbackState.postValue(state)
    }

    /**
     * 1. Set playback mode for camera manager
     * 2. Get id, uid, password camera
     * 3. Initialize camera manager
     * 4. Set camera live data value
     */


    /**
     * Set camera state
     */
    private fun setCameraState(state: Constant.CameraState) {
        _cameraState.postValue(state)
    }

    /**
     * Set playback mode
     */
    private fun setPlaybackMode() {
        vnttCamManager.setPlaybackMode(true)
    }

    /**
     * Set liveview mode
     */
    fun setLiveviewMode() {
        isCancelledPlayback = true
        vnttCamManager.setPlaybackMode(false)
    }

    /**
     * Lấy thông tin id, uid, password camera
     */

}