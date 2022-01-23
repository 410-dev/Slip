package localmsgr;

import javax.swing.JOptionPane;

public class SystemLogger {
    public static final short EXIT = 0;
    public static final short CONTINUE = 1;

    public static boolean VERBOSE = true;
    public static boolean DEBUG = false;
    private static boolean hasPrintedDebugOn = true;

    public static void printDebugOn() {
        if (!hasPrintedDebugOn) {
            System.out.println("[*] INFO [ " + DateManager.getTimestamp() + " ] [ printDebugOn ] : Debug mode enabled.");
            hasPrintedDebugOn = true;
        }
    }

    public static void log(String message) {
        if (VERBOSE) {
            printDebugOn();
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];
            System.out.println("[*] INFO [ " + DateManager.getTimestamp() + " ] [ " + e.getClassName() + "." + e.getMethodName() + " ] : " + message);
        }
    }

    public static void warning(String message) {
        if (VERBOSE) {
            printDebugOn();
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];
            System.out.println("[!] WARNING [ " + DateManager.getTimestamp() + " ] [ " + e.getClassName() + "." + e.getMethodName() + " ] : " + message);
        }
    }

    public static void error(String message, boolean showExceptionPrompt, short action, Exception exception) {
        if (VERBOSE) {
            printDebugOn();
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];
            System.out.println("[-] ERROR [ " + DateManager.getTimestamp() + " ] [ " + e.getClassName() + "." + e.getMethodName() + " ] : " + message);
            if (showExceptionPrompt) {
                JOptionPane.showMessageDialog(null, message, "Unhandled Exception Occurred", JOptionPane.ERROR_MESSAGE);
                switch(action) {
                    case EXIT:
                        System.exit(0);
                        break;
                    case CONTINUE:
                        break;
                }
            }
            if (DEBUG) {
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void debug(String message) {
        if (DEBUG) {
            printDebugOn();
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];
            System.out.println("[?] DEBUG [ " + DateManager.getTimestamp() + " ] [ " + e.getClassName() + "." + e.getMethodName() + " ] : " + message);
        }
    }
}
