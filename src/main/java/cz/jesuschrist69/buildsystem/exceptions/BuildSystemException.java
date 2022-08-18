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

    /**
     * It replaces all occurrences of {i} in the string msg with the string representation of the i-th element of the array
     * vars
     *
     * @param msg The message to be formatted.
     * @return The method is returning a string that is formatted with the variables passed in.
     */
    private static String format(String msg, Object... vars) {
        for (int i = 0; i < vars.length; i++) {
            msg = msg.replace("{" + i + "}", vars[i].toString());
        }
        return msg;
    }

}
