package com.vik.playground.jaamsim.batch;

import com.jaamsim.basicsim.JaamSimModel;
import com.jaamsim.events.EventManager;
import com.jaamsim.events.EventTimeListener;
import com.vik.playground.jaamsim.domain.MyItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class MyItemProcessor implements ItemProcessor<MyItem, MyItem> {

	private static final Logger logger = LoggerFactory.getLogger(MyItemProcessor.class);

	@Override
	public MyItem process(MyItem item) throws Exception {

//		ProcessBuilder pb = new ProcessBuilder("java", "-jar", "JaamSim.jar", "test.cfg", "-s", "-h");
//		Process proc = pb.start();
//
//		BufferedReader stdInput = new BufferedReader(new
//			InputStreamReader(proc.getInputStream()));
//
//		BufferedReader stdError = new BufferedReader(new
//			InputStreamReader(proc.getErrorStream()));
//
//		// Read the output from the command
//		System.out.println("Here is the standard output of the command:\n");
//		String s = null;
//		while ((s = stdInput.readLine()) != null) {
//			System.out.println(s);
//		}
//
//		// Read any errors from the attempted command
//		System.out.println("Here is the standard error of the command (if any):\n");
//		while ((s = stdError.readLine()) != null) {
//			System.out.println(s);
//		}
//
//
//		proc.waitFor();


		final JaamSimModel sim = new JaamSimModel("test");

		sim.autoLoad();

		sim.configure(Paths.get("./TransportationLoopTimeEvent.cfg").toFile());

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		//Set some input for the run
//		sim.setInput("Simulation", "RunDuration", item.getRunDuration() + " y");

		EventTimeListener listener = new EventTimeListener() {
			@Override
			public void tickUpdate(long tick) {
			}
			@Override
			public void timeRunning() {
				if (EventManager.current().isRunning()) {
					return;
				}

				countDownLatch.countDown();
			}
			@Override
			public void handleError(Throwable t) {
				logger.error(t.getMessage(), t);
				countDownLatch.countDown();
			}
		};

		sim.setTimeListener(listener);

		sim.start();

		final boolean completed = countDownLatch.await(10, TimeUnit.MINUTES);

		if (!completed) {
			logger.info("Simulation timeout occurred for item: {}", item.getId());
		} else {
			logger.info("Finished sim for item: {}", item.getId());
		}

		return item;
	}
}
