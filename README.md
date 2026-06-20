# 🎮 Java Game Stuff (But Not Actually Games)

**Clarification:** I am not making playable games here.
- ✅ Just code that *helps* to beat games (or pretends to).

## 🤔 What Is This?

This is a collection of Java scripts that I made my self that handle the boring or hard math behind java or non-java games 

## 📋 What's Included

- **GameDebugger.java** - A comprehensive debugging utility for tracking performance, memory, threads, and more

## 🚀 How to Run This Mess

### Step 1: Requirements

- Latest or pretty new version of Java (Java 11+)
- A Java game installed (Minecraft Java Edition, etc.) - *optional, but recommended*
- A brain (very important for using computer code)

### Step 2: How to Actually Run It

#### Option A: Use GameDebugger in Your Own Project

1. **Copy GameDebugger.java** to your project
2. **Import it in your game code:**
```java
public class MyGame {
    public static void main(String[] args) {
        // Enable the debugger
        GameDebugger.setEnabled(true);
        
        // Start your game loop
        gameLoop();
    }
    
    private static void gameLoop() {
        GameDebugger.startTimer("frame");
        
        // Your game rendering code here
        render();
        update();
        
        GameDebugger.endTimer("frame");
        GameDebugger.logMemoryUsage();
    }
}
```

#### Option B: Run GameDebugger as a Standalone Demo

1. **Compile it:**
```bash
javac GameDebugger.java
```

2. **Create a test file (TestGameDebugger.java):**
```java
public class TestGameDebugger {
    public static void main(String[] args) {
        GameDebugger.setEnabled(true);
        
        // Test timers
        GameDebugger.startTimer("test");
        try { Thread.sleep(500); } catch (Exception e) {}
        GameDebugger.endTimer("test");
        
        // Test memory tracking
        GameDebugger.logMemoryUsage();
        
        // Test call tracking
        for (int i = 0; i < 5; i++) {
            GameDebugger.trackCall("update");
        }
        GameDebugger.printCallCounts();
        
        // Print full report
        GameDebugger.printDebugReport();
    }
}
```

3. **Compile and run:**
```bash
javac TestGameDebugger.java
java TestGameDebugger
```

### Step 3: Integration Examples

#### Track Render Performance
```java
GameDebugger.measure("renderFrame", () -> {
    graphics.clear();
    renderEntities();
    graphics.display();
});
```

#### Monitor Memory During Gameplay
```java
if (frameCount % 60 == 0) {  // Every 60 frames
    GameDebugger.logMemoryUsage();
}
```

#### Debug Object State
```java
GameDebugger.inspectObject(player);
GameDebugger.inspectObject(enemy);
```

#### Assert Game Logic
```java
GameDebugger.Assert(player.getHealth() >= 0, "Player health cannot be negative!");
```

#### Check Active Threads
```java
GameDebugger.printThreadInfo();
```

## 🎯 Features

- ⏱️ **Performance Timing** - Measure execution time
- 💾 **Memory Tracking** - Monitor heap usage
- 🔍 **Object Inspection** - View object fields via reflection
- 📍 **Stack Traces** - Print stack traces on demand
- 🧵 **Thread Monitoring** - See active threads
- 📞 **Call Counting** - Track function calls
- 🎨 **Colored Output** - Easy-to-read console output with colors
- ✅ **Assertions** - Assert conditions with helpful error messages

## 📝 License

Apache License 2.0

