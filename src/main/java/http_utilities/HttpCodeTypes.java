package http_utilities;

public class HttpCodeTypes {
    public static boolean isInfo(int code) {
        return code > 99 && code < 104;
    }

    public static boolean isSuccess(int code) {
        return code > 199 && code < 226;
    }

    public static boolean isRedirection(int code) {
        return code > 299 && code < 309;
    }

    public static boolean isClientError(int code) {
        return code > 399 && code < 500;
    }

    public static boolean isServerError(int code) {
        return code > 500 && code < 527;
    }
}
