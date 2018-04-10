package cbp.marketlist.parse;

/**
 * 数据解析异常
 *
 * @author cbp
 */
@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}

