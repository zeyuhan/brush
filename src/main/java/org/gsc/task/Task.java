package org.gsc.task;

import org.gsc.config.Args;

public interface Task {
  void init(Args args);
  void start();
  void shutdown();
}
