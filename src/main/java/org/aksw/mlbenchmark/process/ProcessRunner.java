package org.aksw.mlbenchmark.process;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class will run a shell script
 */
public class ProcessRunner {
	static final Logger logger = LoggerFactory.getLogger(ProcessRunner.class);
	/*public ProcessRunner(Configuration config) {
		List<Object> systems = config.getList("learningsystems");



	}*/

	public ProcessRunner(String learningSystemDir, String command, List<String> args, Configuration configuration) throws IOException {
		DefaultExecutor e = new DefaultExecutor();

		e.setWorkingDirectory(new File(learningSystemDir));
		Map<String, String> environment = new ProcessBuilder().environment();
		if (configuration != null) {
			try {
				e.setStreamHandler(new PumpStreamHandler(new FileOutputStream(configuration.getString("data.workdir")+"/"+"lsoutput.log")));
			} catch (FileNotFoundException e1) {
				logger.warn("could not set log file output");
			}
			updateEnvironment(environment, configuration);
		}
		CommandLine cmd = new CommandLine("./run");
		if (args != null) {
			cmd.addArguments(args.toArray(new String[0]));
		}
		int rc = e.execute(cmd, environment);
	}

	private void updateEnvironment(Map<String, String> environment, Configuration configuration) {
		Iterator<String> keys = configuration.getKeys();

		while (keys.hasNext()) {
			String key = keys.next();
			Object property = configuration.getProperty(key);
			String val;
			if (property instanceof Collection) {
				StringBuilder valBuilder = new StringBuilder();
				Iterator iterator = ((Collection) property).iterator();
				while (iterator.hasNext()) {
					valBuilder.append(iterator.next());
					if (iterator.hasNext()) {
						valBuilder.append(":");
					}
				}
				val = valBuilder.toString();
			} else {
				val = property.toString();
			}
			environment.put("SMLB_" + key.toUpperCase().replaceAll("[.]+", "_"), val);
		}
	}

	public ProcessRunner(String learningSystemDir, String s) throws IOException {
		this(learningSystemDir, s, null, null);
	}
}
