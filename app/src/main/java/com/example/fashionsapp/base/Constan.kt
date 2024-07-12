package com.example.fashionsapp.base

object Constant {

    enum class PlayPlaybackFileEvent {
        FIRST_TIME,         // Thời điểm đầu tiên vào playback
        SEEK,               // Tua file
        NEXT_FILE,          // Next sang file mới trong cùng 1 ngày
        SELECT_DAY,         // Chọn ngày xem playback
        SCAN_PREVIOUS,      // Quét về quá khứ
        SCAN_NEXT,          // Quét về tương lai
        NEXT_DAY,            // Next sang ngày mới khi đã play hết ngày hiện tại
        PAUSE,
        NOTIFICATION         //Xem thông báo phát hiện chuyển động
    }

    enum class PlaybackSdCardStateUI {
        PLAYING,
        PAUSED,          // Hoàn tất pause
        CONTINUE_PLAY,  // Trạng thái play từ pause -> play lại
        PAUSING,        // Đang trong quá trình pause
        SEEK,           // Trạng thái tua
        NEXT_FILE,      // Trạng thái next sang file mới trong cùng 1 ngày
        SELECT_DAY,     // Chọn ngày xem playback
        EMPTY_FILE,      // Ngày phát lại không có dữ liệu
        SCAN,
        EMPTY_FILE_ALL_DAY,
        NEXT_DAY,
        COMPLETE,
        EMPTY_FILE_NOTIFICATION
    }

    enum class CameraState(val value: String) {
        Null("NULL"),
        Init("INIT"),
        CameraOnline("ONLINE"),
        CameraOffline("OFFLINE"),
        CameraSleep("SLEEP"),
        CameraReconnect("RECONNECT"),
        CameraNotInitialized("CameraNotInitialized"),
        CameraInvalidUid("INVALID_UID"),
        CameraConnectionTimeOut("CameraConnectionTimeOut"),
        CameraSessionClosed("CameraSessionClosed"),
        InvalidIDPrefix("InvalidIDPrefix"),
        CameraScreenShot("SCREEN_SHOT"),
        CameraRecordVideo("RECORD_VIDEO"),
        ScreenShotVideoManager("SCREEN_SHOT_VIDEO_MANAGER"),
        PlaybackSdCardStart("PLAYBACK_SDCARD_START"),
        PlaybackLoadmore("PLAYBACK_LOAD_MORE"),
        CameraIsUpdatingFirmware("IS_UPDATING_FIRM_WARE"),
        PlaybackVideoCloud("PLAYBACK_VIDEO_CLOUD"),
        PlaybackEmptySdcard("PLAYBACK_EMPTY_SDCARD"),
        CameraLossConnection("CAMERA_LOSS_CONNECTION"),
        PlaybackDebounceLoading("PLAYBACK_DEBOUNCE_LOADING"),
        PlaybackSdCardNoData("PLAYBACK_SDCARD_NOPLAYBACK_DATA"),
        CameraRecordVideoH265("H265_VIDEO"),
    }

    const val EMPTY_STRING = ""
}