package com.im.vt;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.util.log.LogUtil;
import com.im.vt.app.config.AppConfig;
import com.im.vt.app.config.AppConstants;
import com.im.vt.utils.AppUtil;


/**
 * 应用程序管理者
 *
 * @author LiuFeng
 * @data 2022/5/12 19:34
 */
public class AppManager {

    /**
     * 是否是debug模式（日志打印控制）
     **/
    private static boolean mIsDebug = true;

    /**
     * 是否用于谷歌商店
     */
    private static boolean isGooglePlay = AppUtil.isGoogleChannel();

    /**
     * 服务器环境配置（根据打包配置自动匹配环境）
     **/
    public static ServerEnum serverConfig = ServerEnum.parse(BuildConfig.ServerEnv);

    /**
     * 是否打开通话响铃（调试时铃声太吵，不在开发环境响铃）
     **/
    private static boolean isOpenRinging = true;

    /**
     * 允许登录页显示环境切换的UI（允许在开发环境使用）
     */
    private static boolean allowEnvSwitch = (serverConfig == ServerEnum.DEVELOP);


    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        // 日志
        initLogger(context);
    }

    /**
     * 是否是debug模式
     *
     * @return
     */
    public static boolean isDebug() {
        return mIsDebug;
    }

    /**
     * 设置Debug
     *
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        mIsDebug = isDebug;
    }

    /**
     * 是否打开通话响铃
     *
     * @return
     */
    public static boolean isOpenRinging() {
        return isOpenRinging;
    }

    /**
     * 设置打开通话响铃
     *
     * @param isOpenRinging
     */
    public static void setOpenRinging(boolean isOpenRinging) {
        AppManager.isOpenRinging = isOpenRinging;
    }

    /**
     * 是否允许环境切换
     *
     * @return
     */
    public static boolean isAllowEnvSwitch() {
        return allowEnvSwitch;
    }

    /**
     * 设置允许环境切换
     *
     * @param allowEnvSwitch
     */
    public static void setAllowEnvSwitch(boolean allowEnvSwitch) {
        AppManager.allowEnvSwitch = allowEnvSwitch;
    }

    /**
     * 是否用于谷歌商店
     *
     * @return
     */
    public static boolean isIsGooglePlay() {
        return isGooglePlay;
    }

    /**
     * 设置用于谷歌商店
     *
     * @param isGooglePlay
     */
    public static void setGooglePlay(boolean isGooglePlay) {
        AppManager.isGooglePlay = isGooglePlay;
    }

    /**
     * 获取当前环境配置
     *
     * @return
     */
    public static ServerEnum getServerConfig() {
        String baseUrl = getBaseUrl();
        if (TextUtils.equals(baseUrl, AppConstants.Official.BASE_URL)) {
            return ServerEnum.OFFICIAL;
        } else if (TextUtils.equals(baseUrl, AppConstants.Uat.BASE_URL)) {
            return ServerEnum.UAT;
        } else if (TextUtils.equals(baseUrl, AppConstants.Beta.BASE_URL)) {
            return ServerEnum.BETA;
        } else if (TextUtils.equals(baseUrl, AppConstants.Develop.BASE_URL)) {
            return ServerEnum.DEVELOP;
        } else {
            return ServerEnum.UNKNOWN;
        }
    }

    /**
     * 设置当前环境配置
     *
     * @param serverEnum
     */
    public static void setServerConfig(ServerEnum serverEnum) {
        serverConfig = serverEnum;
    }

    /**
     * 获取base地址
     *
     * @return
     */
    public static String getBaseUrl() {
        return Config.baseUrl;
    }

    /**
     * 更新base地址
     *
     * @param baseUrl
     */
    public static void updateBaseUrl(String baseUrl) {
        Config.baseUrl = baseUrl;
    }

    /**
     * 获取富文本编辑器地址
     *
     * @return
     */
    public static String getRichEditorUrl() {
        return Config.richEditorUrl;
    }

    /**
     * 更新富文本编辑器地址
     *
     * @param url
     */
    public static void updateRichEditorUrl(String url) {
        Config.richEditorUrl = url;
    }

    /**
     * 获取以太坊地址
     *
     * @return
     */
    public static String getEthUrl() {
        return Config.ethUrl;
    }

    /**
     * 获取以太坊浏览器
     *
     * @return
     */
    public static String getEthScan() {
        return Config.ethScan;
    }

    /**
     * 获取红包合约
     *
     * @return
     */
    public static String getRedPacketContract() {
        return Config.redPacketContract;
    }

    /**
     * 获取会议地址
     *
     * @return
     */
    public static String getMeetingUrl() {
        return Config.meetingUrl;
    }


    /**
     * 获取语音sse地址
     *
     * @return
     */
    public static String getVoiceSseUrl() {
        return Config.voiceSseUrl;
    }


    /**
     * 获取语AI挑战地址
     *
     * @return
     */
    public static String getAiChallengeUrl() {
        return Config.aiChallengeUrl;
    }

    /**
     * 初始化日志工具
     */
    private static void initLogger(Context context) {
        LogUtil.init(AppConfig.getLogPath(), Log.DEBUG, "vtalk", mIsDebug);
    }


    /**
     * url配置类
     */
    private static class Config {
        static String baseUrl;
        static String richEditorUrl;
        static String ethUrl;
        static String ethScan;
        static String redPacketContract;
        static String meetingUrl;

        static String voiceSseUrl;

        static String aiChallengeUrl;


        // 类加载初始化
        static {
            initConfig();
        }

        /**
         * 初始化配置数据
         */
        public static void initConfig() {
            // 取不同环境地址
            if (serverConfig == ServerEnum.OFFICIAL) {
                baseUrl = AppConstants.Official.BASE_URL;
                richEditorUrl = AppConstants.Official.RICH_EDITOR_URL;
                ethUrl = AppConstants.Official.ETH_URL;
                ethScan = AppConstants.Official.ETH_SCAN;
                redPacketContract = AppConstants.Official.RED_PACKET_CONTRACT;
                meetingUrl = AppConstants.Official.MEETING_URL;
                voiceSseUrl = AppConstants.Official.VOICE_SSE_URL;
                aiChallengeUrl = AppConstants.Official.AI_CHALLENGE_URL;
            } else if (serverConfig == ServerEnum.UAT) {
                baseUrl = AppConstants.Uat.BASE_URL;
                richEditorUrl = AppConstants.Uat.RICH_EDITOR_URL;
                ethUrl = AppConstants.Uat.ETH_URL;
                ethScan = AppConstants.Uat.ETH_SCAN;
                redPacketContract = AppConstants.Uat.RED_PACKET_CONTRACT;
                meetingUrl = AppConstants.Uat.MEETING_URL;
                voiceSseUrl = AppConstants.Uat.VOICE_SSE_URL;
                aiChallengeUrl = AppConstants.Uat.AI_CHALLENGE_URL;
            } else if (serverConfig == ServerEnum.BETA) {
                baseUrl = AppConstants.Beta.BASE_URL;
                richEditorUrl = AppConstants.Beta.RICH_EDITOR_URL;
                ethUrl = AppConstants.Beta.ETH_URL;
                ethScan = AppConstants.Beta.ETH_SCAN;
                redPacketContract = AppConstants.Beta.RED_PACKET_CONTRACT;
                meetingUrl = AppConstants.Beta.MEETING_URL;
                voiceSseUrl = AppConstants.Beta.VOICE_SSE_URL;
                aiChallengeUrl = AppConstants.Beta.AI_CHALLENGE_URL;
            } else {
                baseUrl = AppConstants.Develop.BASE_URL;
                richEditorUrl = AppConstants.Develop.RICH_EDITOR_URL;
                ethUrl = AppConstants.Develop.ETH_URL;
                ethScan = AppConstants.Develop.ETH_SCAN;
                redPacketContract = AppConstants.Develop.RED_PACKET_CONTRACT;
                meetingUrl = AppConstants.Develop.MEETING_URL;
                voiceSseUrl = AppConstants.Develop.VOICE_SSE_URL;
                aiChallengeUrl = AppConstants.Develop.AI_CHALLENGE_URL;
            }


            // 取手动设置地址
            String domainUrl = AppConfig.getAppDomainUrl();
            if (!TextUtils.isEmpty(domainUrl) && isAllowEnvSwitch()) {
                baseUrl = domainUrl;
            }
        }
    }
}
