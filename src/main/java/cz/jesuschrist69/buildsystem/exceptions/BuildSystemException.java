package cz.jesuschrist69.buildsystem.exceptions;

public class BuildSystemException extends RuntimeException {

    public BuildSystemException() {
        super();
    }

    public BuildSystemException(String msg) {
        super(msg);
    }

    public BuildSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BuildSystemException(Throwable cause) {
        super(cause);
    }

    public BuildSystemException(String msg, Object... vars) {
        super(format(msg, vars));
    }

    public BuildSystemException(String msg, Throwable cause, Object... vars) {
        super(format(msg, vars), cause);
    }

    private static String format(String msg, Object... vars) {
        for (int i = 0; i < vars.length; i++) {
            msg = msg.replace("{" + i + "}", vars[i].toString());
        }
        return msg;
    }

}
