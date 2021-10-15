package Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

/**
 * This class runs a javafx thread and allows the user to registers behaviors that can be run in this thread
 * the thread is ran about 60 times a second, to provide a smooth update.
 * this class is mainly used for graphics, like the graphics that are seen on the sides of most GUIs in this project,
 * this class can be used for other things than graphics too.
 * 
 * @author Bshara
 * */
public class AppManager {
	public static double time = 0;
	public static double deltaTime = 0.016666;
	public static double startTime;
	private static double lastTime;

	private static List<Func> functions;
	private static Map<String, Func> uniqueFunctions;
	private static Queue<String> functionRemovalQueue;
	private static Queue<NamedFunction> uniqueFunctionsAddingQueue;
	private static Map<String, TimelineAndFunc> globalTimelines;
	private static Map<String, FuncAndDouble> timeTriggers;
	private static Queue<String> timeTriggersRemovalQueue;
	private static List<Func> initialFunctions;

	private static Random rnd;
	public static Timeline timeline;
	
	static {
		initialFunctions = Collections.synchronizedList(new ArrayList<Func>());
		functions = Collections.synchronizedList(new ArrayList<Func>());
		uniqueFunctions = Collections.synchronizedMap(new HashMap<String, Func>());
		functionRemovalQueue = new PriorityQueue<String>();
		uniqueFunctionsAddingQueue = new PriorityQueue<NamedFunction>();
		globalTimelines = Collections.synchronizedMap(new HashMap<String, TimelineAndFunc>());
		timeTriggers = Collections.synchronizedMap(new HashMap<String, FuncAndDouble>());
		timeTriggersRemovalQueue = new PriorityQueue<String>();
		startTime = new Date().getTime();
		rnd = new Random();
		
		
		lastTime = System.nanoTime();

		timeline = new Timeline(new KeyFrame(Duration.millis(16.6666), event -> {

			time = new Date().getTime() - startTime;
			time /= 1000;

			long time = System.nanoTime();
			deltaTime = ((time - lastTime) / 1000000000);
			lastTime = time;

			callback();

			
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		
	}

	// This class has to be static in order to be used in a static function
	private static class FuncAndDouble {
		public double countdownTime;
		public Func func;

		public FuncAndDouble(double countdownTime, Func func) {
			this.countdownTime = countdownTime;
			this.func = func;
		}

	}
	
	// This class has to be static in order to be used in a static function
	private static class TimelineAndFunc {
		
		public Timeline timeline;
		public Func onFinish;
		
		public TimelineAndFunc(Timeline timeline, Func onFinish) {
			this.timeline = timeline;
			this.onFinish = onFinish;
		}
	}
	
		
	/**
	 * Adds a function to the first callback loop, needs some work to make it more robust
	 * for now you can only add functions.
	 * */
	public static void addInitialFunction(Func f) {
		initialFunctions.add(f);

	}

	public static void update(Func f) {
		functions.add(f);

	}

	public static void removeFunction(Func f) {
		functions.remove(f);
	}

	
	private static void callback() {
		
		synchronized (initialFunctions) {
			for (Func func : initialFunctions) {
				if (func != null)
					func.execute();

			}
		}
		
		
		synchronized (functions) {
			for (Func func : functions) {
				if (func != null)
					func.execute();

			}
		}
		synchronized (uniqueFunctions) {

			for (Func func : uniqueFunctions.values()) {
				if (func != null)
					func.execute();

			}
		}

		synchronized (functionRemovalQueue) {
			for (String key : functionRemovalQueue) {
				uniqueFunctions.remove(key);
			}
			
			
			// empty the queue after removing all of the functions
			functionRemovalQueue.clear();
		}

		synchronized (uniqueFunctionsAddingQueue) {
			for (NamedFunction nf : uniqueFunctionsAddingQueue) {
				uniqueFunctions.put(nf.getName(), nf.getFunc());
			}
			// empty the queue after adding all the functions
			uniqueFunctionsAddingQueue.clear();
		}

		synchronized (timeTriggers) {

			for (Entry<String, FuncAndDouble> entry : timeTriggers.entrySet()) {
				FuncAndDouble fd = entry.getValue();
				fd.countdownTime -= AppManager.deltaTime;
				if (fd.countdownTime < 0) {
					timeTriggersRemovalQueue.add(entry.getKey());
					if (fd.func != null)
						fd.func.execute();
				}
			}
		}

		synchronized (timeTriggersRemovalQueue) {
			for (String key : timeTriggersRemovalQueue) {
				timeTriggers.remove(key);
			}

			// empty the queue after removing all of the functions
			timeTriggersRemovalQueue.clear();
		}
		
		

	}

	/**
	 * Executes a function after <b>time</b>. A unique name has to be given in order
	 * to add this function to the map.
	 * The time trigger is automatically removed after its execution, so a unique name can be reused.
	 */
	public static void addTimeTrigger(Func executeOnTimeEnd, double durationInSeconds, String uniqueName) {

		timeTriggers.put(uniqueName, new FuncAndDouble(durationInSeconds, executeOnTimeEnd));
	}

	/**
	 * Removes the timer trigger corresponding to the unique name.
	 */
	public static void removeTimeTrigger(String uniqueName) {
		// Add the name of the function to the removal queue
		timeTriggersRemovalQueue.add(uniqueName);
	}

	/**
	 * Calls the given function 60 times per second.<br>
	 * This function can be manually removed using {@link removeUnique}.
	 * 
	 * @param f   The function to be updated.
	 * @param key This key is required to identify the function when
	 *            {@link removeUnique} is called.
	 */
	public static void updateUnique(String key, Func f) {
		synchronized (uniqueFunctionsAddingQueue) {
			// add iff the key doesn't already exist in the unique functions map
			if (!uniqueFunctions.containsKey(key))
				uniqueFunctionsAddingQueue.add(new NamedFunction(key, f));

		}

	}
	
	
	/**
	 * Calls the given function 60 times per second.<br>
	 * This function can be manually removed using {@link removeUnique}.
	 * 
	 * @param f   The function to be updated.
	 * @param key This key is required to identify the function when
	 *            {@link removeUnique} is called.
	 */
	public static void updateUnique(String key, Func callbackFunc, double durationInSeconds, Func executeOnFinish) {
		synchronized (uniqueFunctionsAddingQueue) {
			// add iff the key doesn't already exist in the unique functions map
			if (!uniqueFunctions.containsKey(key)) {
				uniqueFunctionsAddingQueue.add(new NamedFunction(key, callbackFunc));
				Func combinedFunc = () -> {
					removeUnique(key);
					if (executeOnFinish != null)
						executeOnFinish.execute();
				};
				addTimeTrigger(combinedFunc, durationInSeconds, key);
			}

		}

	}

	
	public static void safeUpdate(String key, Func f) {
		AppManager.removeUnique(key);
		AppManager.addTimeTrigger(() -> {
			AppManager.updateUnique(key, f);	
		}, 0.1, key + "dd");
	}
	
	/**
	 * Removes the function that has been added using {@link updateUnique}.
	 * 
	 * @param key This key is used to identify the function added by calling
	 *            {@link updateUnique}.
	 */
	public static void removeUnique(String key) {
		synchronized (functionRemovalQueue) {
			functionRemovalQueue.add(key);
		}

	}

	// TODO: fix
	public static void updateUniqueWithDelay(String key, Func f, long delay) {
		synchronized (uniqueFunctionsAddingQueue) {
			Util.ExecuteThreadAfterDelay(() -> {
				uniqueFunctionsAddingQueue.add(new NamedFunction(key, f));
			}, delay);

		}
	}

	private static void addTimeline(String key, Timeline timeline, Func onFinish) {
		globalTimelines.put(key, new TimelineAndFunc(timeline, onFinish));
	}

	public static void removeTimeline(String key) {
		if (globalTimelines.containsKey(key)) {
			Func onFinish = globalTimelines.get(key).onFinish;
			if (onFinish != null)
				onFinish.execute();
			globalTimelines.remove(key).timeline.stop();
		}
	
	}

	
	/**
	 * @exception NullPointerException Throws an exception if the time line does not exist.
	 * */
	public static void pauseTimeline(String key) {
		Timeline timeline = globalTimelines.get(key).timeline;
		try {
			throwExceptionOnNull(timeline, "Error, no such timeline exists in globalTimelines");
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeline.pause();
	}


	/**
	 * @exception NullPointerException Throws an exception if the time line does not exist.
	 * */
	public static void playTimeline(String key) {
		Timeline timeline = globalTimelines.get(key).timeline;
		try {
			throwExceptionOnNull(timeline, "Error, no such timeline exists in globalTimelines");
		} catch (Exception e) {
			e.printStackTrace();
		}
		timeline.play();
	}
	
	

	private static void throwExceptionOnNull(Object obj, String msg) throws Exception {
		if (obj == null) {
			throw new NullPointerException(msg);
		}
	}
	

	public static void executeTimeline(String key, Func func, double frameLengthInMillis) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameLengthInMillis), event -> {
			func.execute();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		
		addTimeline(key, timeline, null);

		timeline.play();

	}
	public static void executeTimeline(String key, Func func, double frameLengthInMillis, Func onFinish) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameLengthInMillis), event -> {
			func.execute();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		
		addTimeline(key, timeline, onFinish);

		timeline.play();

	}
	
	public static Random getRnd() {
		return rnd;
	}

	

}
