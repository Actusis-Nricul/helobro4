import java.util.*;
import java.lang.management.MemoryMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GameDebugger - A comprehensive debugging utility for Java games
 * Provides performance profiling, memory tracking, object inspection, and more
 */
public class GameDebugger {
    
    private static final Map<String, Long> timers = new HashMap<>();
    private static final Map<String, Integer> callCounts = new HashMap<>();
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static boolean enabled = true;
    
    // Color codes for console output
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    
    /**
     * Start a named timer for performance measurement
     * @param timerName The name of the timer
     */
    public static void startTimer(String timerName) {
        if (!enabled) return;
        timers.put(timerName, System.nanoTime());
    }
    
    /**
     * End a named timer and print the elapsed time
     * @param timerName The name of the timer
     * @return elapsed time in milliseconds
     */
    public static long endTimer(String timerName) {
        if (!enabled) return 0;
        
        if (!timers.containsKey(timerName)) {
            logWarn("Timer '" + timerName + "' was never started!");
            return 0;
        }
        
        long elapsed = (System.nanoTime() - timers.get(timerName)) / 1_000_000;
        timers.remove(timerName);
        
        logSuccess(timerName + " took " + elapsed + "ms");
        return elapsed;
    }
    
    /**
     * Measure the execution time of a lambda
     * @param actionName Name of the action
     * @param action The code to execute
     */
    public static void measure(String actionName, Runnable action) {
        if (!enabled) {
            action.run();
            return;
        }
        
        long start = System.nanoTime();
        try {
            action.run();
        } catch (Exception e) {
            logError("Exception in " + actionName + ": " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            logInfo(actionName + " executed in " + elapsed + "ms");
        }
    }
    
    /**
     * Track function call counts
     * @param functionName The name of the function
     */
    public static void trackCall(String functionName) {
        if (!enabled) return;
        callCounts.put(functionName, callCounts.getOrDefault(functionName, 0) + 1);
    }
    
    /**
     * Get the call count for a function
     * @param functionName The name of the function
     * @return number of times called
     */
    public static int getCallCount(String functionName) {
        return callCounts.getOrDefault(functionName, 0);
    }
    
    /**
     * Print all tracked call counts
     */
    public static void printCallCounts() {
        if (callCounts.isEmpty()) {
            logWarn("No call counts tracked");
            return;
        }
        
        logInfo("=== CALL COUNTS ===");
        callCounts.forEach((func, count) -> 
            System.out.println(CYAN + "  " + func + ": " + count + RESET)
        );
    }
    
    /**
     * Get current memory usage
     * @return memory usage in MB
     */
    public static double getMemoryUsageMB() {
        long used = memoryBean.getHeapMemoryUsage().getUsed();
        return used / (1024.0 * 1024.0);
    }
    
    /**
     * Get max memory available
     * @return max memory in MB
     */
    public static double getMaxMemoryMB() {
        long max = memoryBean.getHeapMemoryUsage().getMax();
        return max / (1024.0 * 1024.0);
    }
    
    /**
     * Log current memory usage
     */
    public static void logMemoryUsage() {
        if (!enabled) return;
        
        double used = getMemoryUsageMB();
        double max = getMaxMemoryMB();
        double percent = (used / max) * 100;
        
        String color = percent > 80 ? RED : (percent > 50 ? YELLOW : GREEN);
        logInfo("Memory: " + color + String.format("%.2f / %.2f MB (%.1f%%)", used, max, percent) + RESET);
    }
    
    /**
     * Inspect an object's fields and values (using reflection)
     * @param obj The object to inspect
     */
    public static void inspectObject(Object obj) {
        if (!enabled || obj == null) return;
        
        logInfo("=== OBJECT INSPECTION: " + obj.getClass().getSimpleName() + " ===");
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                String valueStr = value == null ? "null" : value.toString();
                System.out.println(CYAN + "  " + field.getName() + " (" + field.getType().getSimpleName() + "): " + valueStr + RESET);
            } catch (IllegalAccessException e) {
                logWarn("Could not access field: " + field.getName());
            }
        }
    }
    
    /**
     * Print the current stack trace
     */
    public static void printStackTrace() {
        if (!enabled) return;
        
        logInfo("=== STACK TRACE ===");
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        
        for (int i = 2; i < Math.min(stack.length, 15); i++) {
            StackTraceElement element = stack[i];
            System.out.println(CYAN + "  at " + element.getClassName() + "." + element.getMethodName() + 
                             "(" + element.getFileName() + ":" + element.getLineNumber() + ")" + RESET);
        }
    }
    
    /**
     * Get information about active threads
     */
    public static void printThreadInfo() {
        if (!enabled) return;
        
        logInfo("=== THREAD INFO ===");
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }
        
        Thread[] threads = new Thread[rootGroup.activeCount()];
        int count = rootGroup.enumerate(threads);
        
        System.out.println(CYAN + "Active Threads: " + count + RESET);
        for (int i = 0; i < count; i++) {
            Thread t = threads[i];
            System.out.println(CYAN + "  [" + i + "] " + t.getName() + " (State: " + t.getState() + ")" + RESET);
        }
    }
    
    /**
     * Assert a condition and log if it fails
     * @param condition The condition to check
     * @param message Error message if condition is false
     */
    public static void Assert(boolean condition, String message) {
        if (!enabled) return;
        
        if (!condition) {
            logError("ASSERTION FAILED: " + message);
            printStackTrace();
            throw new AssertionError(message);
        }
    }
    
    /**
     * Log an info message
     */
    public static void logInfo(String message) {
        System.out.println(BLUE + "[" + getTimeStamp() + " INFO] " + message + RESET);
    }
    
    /**
     * Log a success message
     */
    public static void logSuccess(String message) {
        System.out.println(GREEN + "[" + getTimeStamp() + " ✓] " + message + RESET);
    }
    
    /**
     * Log a warning message
     */
    public static void logWarn(String message) {
        System.out.println(YELLOW + "[" + getTimeStamp() + " WARN] " + message + RESET);
    }
    
    /**
     * Log an error message
     */
    public static void logError(String message) {
        System.out.println(RED + "[" + getTimeStamp() + " ERROR] " + message + RESET);
    }
    
    /**
     * Get current timestamp
     */
    private static String getTimeStamp() {
        return LocalDateTime.now().format(timeFormat);
    }
    
    /**
     * Enable or disable all debugging output
     */
    public static void setEnabled(boolean enable) {
        enabled = enable;
    }
    
    /**
     * Clear all tracked data
     */
    public static void clear() {
        timers.clear();
        callCounts.clear();
        logInfo("Debugger data cleared");
    }
    
    /**
     * Print a comprehensive debug report
     */
    public static void printDebugReport() {
        if (!enabled) return;
        
        System.out.println(CYAN + "\n╔════════════════════════════════════════╗");
        System.out.println("║        DEBUG REPORT - " + getTimeStamp() + "        ║");
        System.out.println("╚════════════════════════════════════════╝" + RESET);
        
        logMemoryUsage();
        System.out.println();
        printThreadInfo();
        System.out.println();
        
        if (!callCounts.isEmpty()) {
            printCallCounts();
            System.out.println();
        }
        
        System.out.println();
    }
}
