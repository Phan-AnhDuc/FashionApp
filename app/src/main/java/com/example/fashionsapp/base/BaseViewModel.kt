package vn.vnpt.ONEHome.core.base

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.vnpttech.ipcamera.Constants
import io.reactivex.disposables.CompositeDisposable
import org.apache.commons.lang.StringUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import timber.log.Timber
import vn.vnpt.ONEHome.data.model.eventbus.ReceivedMqttDataEvent
import vn.vnpt.ONEHome.data.model.eventbus.TuyaDeviceListenerEvent
import vn.vnpt.ONEHome.data.network.model.response.ErrorResponse
import vn.vnpt.ONEHome.di.component.datamanager.DataManager
import vn.vnpt.ONEHome.di.component.mqttservice.MqttMessageProvider
import vn.vnpt.ONEHome.di.component.resource.ResourcesService
import vn.vnpt.ONEHome.di.component.scheduler.SchedulerProvider
import vn.vnpt.ONEHome.feature.uibundle.OHAppUIBundleManager
import vn.vnpt.ONEHome.utils.Constant
import vn.vnpt.ONEHome.utils.Constant.MQTT_MAPPING_DATA
import vn.vnpt.ONEHome.utils.Constant.MQTT_MESSAGE_DATA
import vn.vnpt.ONEHome.utils.ErrorUtils
import vn.vnpt.ONEHome.utils.updatefirmware.ShowFirmwarePopUp
import vn.vnpt.ONEHome.utils.SingleLiveEvent
import vn.vnpt.ONEHome.utils.extension.Event
import vn.vnpt.ONEHome.utils.extension.FirebaseEvent
import vn.vnpt.ONEHome.utils.extension.InternetEvent
import vn.vnpt.ONEHome.utils.extension.StateLoadingFailEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by @Author: Nam Luong Xuan
 * Project : ONEHome
 * Create Time : 10:02 - 22/10/2021
 * For all issue contact me : namlx@vnpt-technology.vn
 * Class BaseViewModel
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Inject MqttMessageProvider
     */
    @Inject
    lateinit var mqttMessageProvider: MqttMessageProvider

    private lateinit var _compositeDisposable: CompositeDisposable
    val compositeDisposable: CompositeDisposable
        get() {
            // Khi Xoay ngang màn hình thì compositeDisposable bị disposed đi => Các request sẽ ko Submit được
            // Trong trường hợp này khởi tạo lại
            if (_compositeDisposable.isDisposed) {
                _compositeDisposable = CompositeDisposable()
            }
            return _compositeDisposable
        }

    lateinit var schedulerProvider: SchedulerProvider
    lateinit var resourcesService: ResourcesService
    lateinit var dataManager: DataManager
    /**
     *  errorMessage: params
     *    + ErrId : id String
     *    + TypeErr:  SUCCESS,FAILURE,WARNING --> icon ( show icon Toast )/ null ( no icon )
     */
    open val errorMessage = SingleLiveEvent<Pair<Int, Constant.ToastStatus?>>()

    open val errorState = SingleLiveEvent<String>()
    open val loadingState = SingleLiveEvent<Boolean>()
    open val authorizationState = MutableLiveData<Boolean>()
    open val notifyObserve =
        MutableLiveData<Boolean>(false) // Notify Observe when fragment destroy the composite will destroy the observe
    open var isInitialized: Boolean = false
    open val eventNotification: LiveData<Event<Pair<Constant.NotificationEvent, Any>>> get() = FirebaseEvent.serviceEvent
    open val nameListDevicesAutoUpgrade: LiveData<Event<String>> get() = FirebaseEvent.nameListDevicesUpgrade
    open val leavingHomeNotification: LiveData<String> get() = FirebaseEvent.leavingHomeEvent
    open val stateLoadingFailEvent: LiveData<Event<Pair<Boolean, Constants.Command?>>> get() = StateLoadingFailEvent.statusFail
    open val updateFwPopup: LiveData<ShowFirmwarePopUp> get() = FirebaseEvent.updateFirmwarePopUp

    open val changeNetworkNotification: LiveData<Boolean> get() = InternetEvent.changeStateInternetEvent
    open val cameraPasswordChanged = MutableLiveData<String>()
    open val cameraSleepModeChanged = MutableLiveData<Pair<String, String>>()
    open val errorKey = MutableLiveData<Any?>()
    open val loadingCustom = SingleLiveEvent<Boolean>()

    private var countDownShowLoading: CountDownTimer? = null

    private var mqttCountdownTimeout: CountDownTimer? = null

    private var numberPage: Int = 0

    fun setLoading(isLoading: Boolean) {
        loadingState.value = isLoading
    }

    open fun setError(errorResponse: ErrorResponse) {
        val errorMessage =
            ErrorUtils.errorMessage(errorResponse, resourceServices = resourcesService)

        // Nếu không phải là lỗi hết phiên user (force logout) thì nhảy vào đây
        if (errorResponse.status != 401) {
            errorState.postValue(errorMessage)
        }

        when (errorResponse.status) {
            403 -> {
            }

            401 -> {
                // force logout
                authorizationState.postValue(false)
            }

            500 -> {

            }
        }
        errorKey.postValue(errorResponse)
    }

    fun setErrorString(errorMessage: String) {
        errorState.value = errorMessage
    }

    fun setErrorStringId(errorMessageId: Int, style: Constant.ToastStatus? = null) {
        errorMessage.value = Pair(errorMessageId, style)
    }

    internal fun init(
        schedulerProvider: SchedulerProvider,
        resourcesService: ResourcesService,
        dataManager: DataManager,
    ) {
        this.schedulerProvider = schedulerProvider
        this.resourcesService = resourcesService
        this.dataManager = dataManager
        _compositeDisposable = CompositeDisposable()
        isInitialized = true
    }

    open fun onDidBindViewModel() {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        _compositeDisposable.dispose()
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    /**
     * register to mqtt message receive data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onReceivedMqttData(mqttMessage: ReceivedMqttDataEvent) {
        mqttMessageReceived(mqttMessage.topic, mqttMessage.mqttMessage)
    }

    /**
     * unregister mqtt message receive data
     */
    open fun unregisterMqttService() {

    }

    /**
     * received mqtt data
     * override to use received data
     * @param topic
     * @param message
     */
    open fun mqttMessageReceived(topic: String?, message: JSONObject?) {
        message?.let { mqttMessage ->
            Timber.e("TopicMqtt: $topic ${mqttMessage.getString(Constant.MQTT_MESSAGE_TYPE)}")
            val userInfo: String
            when (mqttMessage.getString(Constant.MQTT_MESSAGE_TYPE)) {
                Constant.MessageType.ForceLogout.type -> {  // user has changed password
                    userInfo = topic?.split("/")?.get(1) ?: StringUtils.EMPTY

                    if (StringUtils.isNotBlank(userInfo) && dataManager.getUserId() == userInfo.toLong()) {
                        Timber.i("userInfo:  ${userInfo.toLong()}")
                        // force logout
                        authorizationState.value = false
                    }
                }

                Constant.MessageType.CameraPasswordChanged.type -> {    // camera has set password
                    userInfo = topic?.split("/")?.get(1) ?: StringUtils.EMPTY
                    if (StringUtils.isNotBlank(userInfo) && dataManager.getUserId() == userInfo.toLong()) {
                        Timber.i("userInfo:  ${userInfo.toLong()}")

                        val cameraUid = mqttMessage.getString(MQTT_MAPPING_DATA)
                        // force to main screen when camera password has been changed
                        cameraPasswordChanged.value = cameraUid
                    }
                }

                Constant.MessageType.CameraChangeSleepMode.type -> {
                    userInfo = topic?.split("/")?.get(1) ?: StringUtils.EMPTY
                    if (StringUtils.isNotBlank(userInfo) && dataManager.getUserId() == userInfo.toLong()) {
                        Timber.i("userInfo:  ${userInfo.toLong()}")
                        val value = mqttMessage.getString(MQTT_MESSAGE_DATA)
                        val cameraUid = mqttMessage.getString(MQTT_MAPPING_DATA)
                        cameraSleepModeChanged.postValue(Pair(cameraUid, value))
                    }
                }

                Constant.MessageType.AddAutomation.type -> {
                    val messageIdMqtt = mqttMessage.getString(Constant.MQTT_MESSAGE_ID)
                    if (messageIdMqtt == OHAppUIBundleManager.instance.timerMessageId) {
                        OHAppUIBundleManager.instance.apply {
                            cancelMqttTimeout()
                            callback?.onSuccess(
                                JSONObject(Gson().toJson(ruleResponseTimer))
                            )
                        }
                    }

                    if (messageIdMqtt == OHAppUIBundleManager.instance.countdownMessageId) {
                        OHAppUIBundleManager.instance.apply {
                            cancelMqttTimeout()
                            callback?.onSuccess(
                                JSONObject(Gson().toJson(ruleResponseCountdown))
                            )
                        }
                    }
                }

                Constant.MessageType.ToggleRule.type -> {
                    val messageIdMqtt = mqttMessage.getString(Constant.MQTT_MESSAGE_ID)
                    if (messageIdMqtt == OHAppUIBundleManager.instance.timerMessageId) {
                        OHAppUIBundleManager.instance.apply {
                            cancelMqttTimeout()
                            val result = JSONObject()
                            result.put("result", true)
                            callback?.onSuccess(result)
                        }
                    }

                    if (messageIdMqtt == OHAppUIBundleManager.instance.countdownMessageId) {
                        OHAppUIBundleManager.instance.apply {
                            cancelMqttTimeout()
                            val result = JSONObject()
                            result.put("result", true)
                            callback?.onSuccess(result)
                        }
                    }
                }

                else ->
                    Timber.i("Nothing todo")
            }
        }
    }

    fun notifyObserve() {
        notifyObserve.value = true
    }

    fun startTimeoutMqtt(
        timeout: Long = TimeUnit.SECONDS.toMillis(15),
        errorMessageId: Int? = null,
        callback: (() -> Unit)? = null
    ) {
        mqttCountdownTimeout?.cancel()
        mqttCountdownTimeout = object : CountDownTimer(timeout, TimeUnit.SECONDS.toMillis(1)) {
            override fun onTick(l: Long) {}

            override fun onFinish() {
                callback?.invoke()
                errorMessageId?.let { setErrorStringId(it) }
                cancelTimeoutMqtt()
            }
        }.start()
    }

    fun cancelTimeoutMqtt() {
        setLoading(false)
        mqttCountdownTimeout?.cancel()
    }

    fun timeoutShowLoading(
        timeout: Long = TimeUnit.SECONDS.toMillis(60),
        errorMessageId: Int? = null,
        callback: (() -> Unit)? = null,
        loading: Boolean
    ) {
        if (loading) {
            setLoading(true)
            countDownShowLoading?.cancel()
            countDownShowLoading = object : CountDownTimer(timeout, TimeUnit.SECONDS.toMillis(1)) {
                override fun onTick(l: Long) {}

                override fun onFinish() {
                    callback?.invoke()
                    errorMessageId?.let { setErrorStringId(it) }
                    cancelTimeOutLoading()
                }
            }.start()
        } else {
            countDownShowLoading?.onFinish()
        }
    }

    fun setLoadingCustom(enable: Boolean) {
        loadingCustom.value = enable
    }

    fun cancelTimeOutLoading() {
        setLoading(false)
        countDownShowLoading?.cancel()
    }

    /**
     * register to tuya message receive data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onReceivedTuyaDeviceData(tuyaDeviceListenerEvent: TuyaDeviceListenerEvent) {
        handleTuyaDeviceListenerEvent(tuyaDeviceListenerEvent.command, tuyaDeviceListenerEvent.devId)
    }

    open fun handleTuyaDeviceListenerEvent(command: String, data: String) {}

    open fun loadMoreItems() {
        numberPage += 1
    }

    open fun refreshItemList() {
        numberPage = 0
    }

    fun getCurrentPageNumber(): Int {
        return numberPage
    }

    fun setCurrentPageNumber(pageNumber: Int) {
        this.numberPage = pageNumber
    }
}