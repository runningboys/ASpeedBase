package com.im.vt.app.config;

/**
 * 常量信息
 *
 * @author LiuFeng
 * @data 2022/5/23 16:17
 */
public interface AppConstants {

    /**
     * 正式服环境
     */
    public interface Official {
        // 基础接口地址
        String BASE_URL = "https://vtalkb.luxmeta.net";

        String OSS_URL = "https://vtalk-beta.oss-accelerate.aliyuncs.com";

        String RICH_EDITOR_URL = "https://vtalk.luxmeta.net/RTE/?token=";
        String ETH_URL = "https://mainnet.infura.io/v3/9c3782c0ec2540bf8a842387c4fecefd";
        String ETH_SCAN = "https://etherscan.io";
        String RED_PACKET_CONTRACT = "0x0888bd7be4874ca9b150405ad241f17b7235f961";

        String MEETING_URL = "ws://192.168.2.55:7880";

        String VOICE_SSE_URL = "http://124.221.49.229:8010/";

        String AI_CHALLENGE_URL = "https://qinqi.luxmeta.net";
    }

    /**
     * 预生产环境
     */
    public interface Uat {
        // 基础接口地址
        String BASE_URL = "https://vtalkb.luxmeta.net";

        String OSS_URL = "https://vtalk-beta.oss-accelerate.aliyuncs.com";

        String RICH_EDITOR_URL = "https://vtalk.luxmeta.net/RTE/?token=";
        String ETH_URL = "https://mainnet.infura.io/v3/9c3782c0ec2540bf8a842387c4fecefd";
        String ETH_SCAN = "https://etherscan.io";
        String RED_PACKET_CONTRACT = "0x0888bd7be4874ca9b150405ad241f17b7235f961";

        String MEETING_URL = "ws://192.168.2.55:7880";

        String VOICE_SSE_URL = "http://124.221.49.229:8010/";

        String AI_CHALLENGE_URL = "https://qinqi.luxmeta.net";
    }

    /**
     * 测试服环境
     */
    public interface Beta {
        // 基础接口地址
        String BASE_URL = "http://192.168.2.81:8881";

        String OSS_URL = "https://vtalk-test.oss-accelerate.aliyuncs.com";
        String RICH_EDITOR_URL = "http://vtalk-web-test.luxmeta.net/RTE/?token=";
        String ETH_URL = "https://goerli.infura.io/v3/9c3782c0ec2540bf8a842387c4fecefd";
        String ETH_SCAN = "https://goerli.etherscan.io";
        String RED_PACKET_CONTRACT = "0xa7EF4E520138D266652B3810749c6ca90Cd27fF6";

        String MEETING_URL = "ws://192.168.2.55:7880";

        String VOICE_SSE_URL = "http://124.221.49.229:8010/";

        String AI_CHALLENGE_URL = "https://qinqi.luxmeta.net";
    }

    /**
     * 开发服环境
     */
    public interface Develop {
        // 基础接口地址
        String BASE_URL = "http://192.168.2.71:8881";

        String OSS_URL = "https://vtalk-dev.oss-accelerate.aliyuncs.com";
        String RICH_EDITOR_URL = "http://vtalk-web-test.luxmeta.net/RTE/?token=";
        String ETH_URL = "https://goerli.infura.io/v3/9c3782c0ec2540bf8a842387c4fecefd";
        String ETH_SCAN = "https://goerli.etherscan.io";
        String RED_PACKET_CONTRACT = "0xa7EF4E520138D266652B3810749c6ca90Cd27fF6";

        String MEETING_URL = "ws://192.168.2.55:7880";

        String VOICE_SSE_URL = "http://192.168.2.53:8010/";

        String AI_CHALLENGE_URL = "http://192.168.100.184:5173/";
    }
}