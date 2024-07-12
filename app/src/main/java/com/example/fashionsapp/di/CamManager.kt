import android.util.LongSparseArray
import com.example.fashionsapp.data.PlaybackFileModel
import com.example.fashionsapp.data.VideoStreamData
import com.vnpttech.ipcamera.Constants
import com.vnpttech.model.DeviceInfo
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

interface VNTTCamManager {
    fun observeListVideoPlayback(): PublishSubject<LongSparseArray<ArrayList<PlaybackFileModel>>>

    fun observePlaybackVideoStream(): PublishSubject<VideoStreamData>

    fun observePlaybackAudioStream(): PublishSubject<ByteArray>

    fun observeTimestampPlayback(): PublishSubject<Long>


    fun observeStopPlayback(): PublishSubject<Boolean>


    /**
     * Initialize callback
     */
    fun initCallback()

    /**
     * Khởi tạo camera
     */
    fun createCameraManager(uid: String, pass: String)

    /**
     * Lấy session id hiện tại của camera
     */
    fun getCurrentSession(): Int

    /**
     * Lấy uid của camera
     */

    fun connectToCamera(instanceId: Int)

    /**
     * Reconnect to camera
     */
    fun reconnectToCamera()

    /**
     * Disconnect camera
     */
    fun disconnectCamera(): Int

    /**
     * Disconnect camera without clear composite disposable
     */
    fun disconnectCamWithoutClearComposite()

    /**
     * Set playback mode
     */
    fun setPlaybackMode(enable: Boolean)

    /**
     * Receive audio
     */
    fun receiveAudio(enable: Boolean): Int

    /**
     * Receive video
     */
    fun receiveVideo(enable: Boolean): Int

    /**
     * Start count downtime to destroy camera
     */
    fun startDestroyCameraTask()

    /**
     * Stop count downtime to destroy camera
     */
    fun stopDestroyCameraTask()


    fun getListVideoPlayBackSdCard(startDay: Long)


    fun playBackVideoStart(playbackFile: PlaybackFileModel, offset: Int): Single<Int>

    /**
     * Stop playback
     */
    fun stopPlayback(): Single<Int>

}



