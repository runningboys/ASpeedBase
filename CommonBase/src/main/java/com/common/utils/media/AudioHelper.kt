package com.common.utils.media

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.common.base.BaseApp
import com.common.utils.log.LogUtil


object AudioHelper {
    private var isCommunicationMode = true

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
     */
    fun switchMic(mute: Boolean) {
        try {
            getAudioManager().isMicrophoneMute = mute
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }


    fun isMicrophoneMute(): Boolean {
        try {
            return getAudioManager().isMicrophoneMute
        } catch (e: Exception) {
            LogUtil.e(e)
        }
        return false
    }

    /**
     * 打开扬声器
     */
    fun openSpeaker() {
        try {
            val audioManager = getAudioManager()
            audioManager.mode = getAudioMode()
            audioManager.isBluetoothScoOn = false
            audioManager.stopBluetoothSco()
            audioManager.isSpeakerphoneOn = true
            /*val currVolume = audioManager.getStreamVolume(getAudioMode())
            audioManager.setStreamVolume(AudioManager.MODE_IN_COMMUNICATION, currVolume, AudioManager.FLAG_PLAY_SOUND)*/
        } catch (e: Exception) {
            LogUtil.e(e)
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
            val audioManager = getAudioManager()
            audioManager.mode = getAudioMode()
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
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }

    fun isSpeakerphoneOn(): Boolean {
        return getAudioManager().isSpeakerphoneOn
    }

    fun hasBluetoothHeadset(): Boolean {
        val devices = getAudioManager().getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val deviceType = device.type
            if (deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
            ) {
                return true
            }
        }
        return false
    }

    fun hasHeadset(): Boolean {
        val devices = getAudioManager().getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val deviceType = device.type
            if (deviceType == AudioDeviceInfo.TYPE_WIRED_HEADSET
                || deviceType == AudioDeviceInfo.TYPE_USB_HEADSET
                || deviceType == AudioDeviceInfo.TYPE_BLE_HEADSET
                || deviceType == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                || deviceType == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                return true
            }
        }
        return false
    }


    fun setCommunicationDevice(audioType: AudioType, devices: List<AudioDeviceInfo>) {
        val audioManager = getAudioManager()
        audioManager.mode = getAudioMode()
        when(audioType) {
            AudioType.Earpiece -> {
                audioManager.stopBluetoothSco()
                audioManager.isBluetoothScoOn = false
                audioManager.isWiredHeadsetOn = false
                audioManager.isSpeakerphoneOn = false
            }
            AudioType.Speakerphone -> {
                audioManager.stopBluetoothSco()
                audioManager.isBluetoothScoOn = false
                audioManager.isWiredHeadsetOn = false
                audioManager.isSpeakerphoneOn = true
            }
            AudioType.WiredHeadset -> {
                audioManager.stopBluetoothSco()
                audioManager.isBluetoothScoOn = false
                audioManager.isWiredHeadsetOn = true
                audioManager.isSpeakerphoneOn = false
            }
            AudioType.BluetoothHeadset -> {
                audioManager.startBluetoothSco()
                audioManager.isBluetoothScoOn = true
                audioManager.isWiredHeadsetOn = false
                audioManager.isSpeakerphoneOn = false
            }
        }
    }


    private fun getAudioMode(): Int {
        return if (isCommunicationMode) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
    }


    fun updateAudioMode(isCommunicationMode: Boolean) {
        /*this.isCommunicationMode = isCommunicationMode
        if (isCommunicationMode) {
            getAudioManager().mode = AudioManager.MODE_IN_COMMUNICATION
        } else {
            getAudioManager().mode = AudioManager.MODE_NORMAL
        }*/
    }


    fun getAudioManager(): AudioManager {
        return BaseApp.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

}


// 音频设备类型
enum class AudioType(val value: Int) {

    // 听筒
    Earpiece(0),

    // 扬声器
    Speakerphone(1),

    // 有线耳机
    WiredHeadset(2),

    // 蓝牙耳机
    BluetoothHeadset(3),

}