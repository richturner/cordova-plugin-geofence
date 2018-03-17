package com.cowbell.cordova.geofence;

import java.util.LinkedList;
import java.util.Queue;

public class GoogleServiceCommandExecutor implements IGoogleServiceCommandListener {
    private Queue<AbstractGoogleServiceCommand> commandsToExecute;
    private boolean isExecuting = false;

    public GoogleServiceCommandExecutor() {
        commandsToExecute = new LinkedList<AbstractGoogleServiceCommand>();
    }

    public void QueueToExecute(AbstractGoogleServiceCommand command) {
        if (command == null) {
            return;
        }
        commandsToExecute.add(command);
        if (!isExecuting) ExecuteNext();
    }

    private void ExecuteNext() {
        AbstractGoogleServiceCommand command = commandsToExecute.poll();
        if (command != null) {
            isExecuting = true;
            command.addListener(this);
            command.Execute();
        }
    }

    @Override
    public void onCommandExecuted(Object error) {
        isExecuting = false;
        ExecuteNext();
    }
}
