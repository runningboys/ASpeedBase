package com.common.utils.media

import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.common.utils.resource.ServiceUtil


object AudioHelper {

    /**
     * 监听耳机连接状态，切换通道
     */
    fun changeSpeaker() {
        if (hasHeadset()) {
            closeSpeaker()
        } else {
            openSpeaker()
        }
    }

    /**
     * 设置麦克风是否静音
     *
     * @param mute
     */
    fun switchMic(mute: Boolean) {
        try {
            val audioManager = ServiceUtil.getAudioManager()
            if (audioManager != null) {
                audioManager.isMicrophoneMute = mute
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun isMicrophoneMute(): Boolean {
        try {
            val audioManager = ServiceUtil.getAudioManager()
            return audioManager.isMicrophoneMute
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 打开扬声器
     */
    fun openSpeaker() {
        try {
            val audioManager = ServiceUtil.getAudioManager()
            if (audioManager != null) {
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isBluetoothScoOn = false
                audioManager.stopBluetoothSco()
                audioManager.isSpeakerphoneOn = true
                /*int currVolume = audioManager.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, currVolume, AudioManager.MODE_IN_COMMUNICATION);*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭扬声器  必须在非正在响铃时
     */
    fun closeSpeaker() {
        closeSpeaker(hasBluetoothHeadset())
    }

    /**
     * 关闭扬声器  必须在非正在响铃时
     */
    fun closeSpeaker(bluetooth: Boolean) {
        try {
            val audioManager = ServiceUtil.getAudioManager()
            if (audioManager != null) {
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                audioManager.isSpeakerphoneOn = false
                audioManager.isBluetoothScoOn = bluetooth
                if (bluetooth) {
                    if (audioManager.isMicrophoneMute) {
                        audioManager.isMicrophoneMute = true
                    }
                    audioManager.startBluetoothSco()
                } else {
                    audioManager.stopBluetoothSco()
                }
                /*int currVolume = audioManager.getStreamVolume(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, currVolume, AudioManager.MODE_IN_COMMUNICATION);*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isSpeakerphoneOn(): Boolean {
        return ServiceUtil.getAudioManager().isSpeakerphoneOn
    }

    fun hasBluetoothHeadset(): Boolean {
        val devices = ServiceUtil.getAudioManager().getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val deviceType = device.type
            if (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                return true
            }
        }
        return false
    }

    fun hasHeadset(): Boolean {
        val devices = ServiceUtil.getAudioManager().getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val deviceType = device.type
            if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET || deviceType == AudioDeviceInfo.TYPE_USB_HEADSET || deviceType == AudioDeviceInfo.TYPE_BLE_HEADSET || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
//                LogUtil.e("AudioSwitch", "has hasHeadset：" + deviceType);
                return true
            }
        }
        return false
    }
}