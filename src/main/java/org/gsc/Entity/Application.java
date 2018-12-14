package org.gsc.Entity;

import lombok.extern.slf4j.Slf4j;
import org.gsc.config.Args;
import org.gsc.task.Task;
import org.gsc.task.TransferTask;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Application {

  private List<Task> taskList;

  private Args args;

  public Application(Args args) {
    this.args = args;
  }

  public void init() {
    logger.info("Init application.");

    this.taskList = new ArrayList<>();
    if (args.isTransfer()) {
      TransferTask transferTask = new TransferTask();
      this.taskList.add(transferTask);
    }
  }

  public void start() {
    logger.info("Start application.");

    for (Task task : taskList) {
      task.init(args);
      task.start();
      task.shutdown();
    }
  }
}
