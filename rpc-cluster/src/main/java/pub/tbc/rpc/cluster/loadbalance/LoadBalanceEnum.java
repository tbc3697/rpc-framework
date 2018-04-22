package pub.tbc.rpc.cluster.loadbalance;

/**
 * 支持的负载算法枚举
 */
public enum LoadBalanceEnum {
    RANDOM("random"),
    POLLING("polling"),
    WRANDOM("w-random"),
    WPOLLING("w-polling"),
    HASH("hash");

    private String code;

    LoadBalanceEnum(String code) {
        this.code = code;
    }

    public static LoadBalanceEnum queryByCode(String code) {
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
