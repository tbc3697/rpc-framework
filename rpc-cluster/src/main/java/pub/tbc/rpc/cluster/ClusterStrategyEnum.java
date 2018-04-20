package pub.tbc.rpc.cluster;

/**
 * 支持的负载算法枚举
 */
public enum ClusterStrategyEnum {
    RANDOM("random"),
    POLLING("polling"),
    WRANDOM("w-random"),
    WPOLLING("w-polling"),
    HASH("hash");

    private String code;

    ClusterStrategyEnum(String code) {
        this.code = code;
    }

    public static ClusterStrategyEnum queryByCode(String code) {
        switch (code) {
            case "random":
                return RANDOM;
            case "polling":
                return POLLING;
            case "w-random":
                return WRANDOM;
            case "w-polling":
                return WPOLLING;
            case "hash":
                return HASH;
            default:
                return null;
        }
    }
}
