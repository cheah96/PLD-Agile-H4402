package fr.insa.lyon.pld.agile.controller;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author scheah
 */
public class CommandList {
    private final List<Command> commandList; 
    private int currentIndex;
    /**
     * Create a new empty commandList to store the list of actions performed to
     * implement the design pattern Command.
     */
    public CommandList() {
        commandList = new LinkedList<>();
        currentIndex = -1;
    }
    
    /**
     * Add a new command into the commandList, increment the current index and 
     * perform the action
     * @param cmd the command to be added
     */
    public void addCommand(Command cmd) {
        int i = currentIndex + 1;
        while (i < commandList.size()) {
            commandList.remove(i);
        }
        currentIndex++;
        commandList.add(currentIndex, cmd);
        cmd.doCmd();
    }
    
    /**
     * Decrement the current index and revert the action, the command is not
     * removed from the command list to allow redoing
     */
    public void undo() {
        if(canUndo()) {
            Command cmd = commandList.get(currentIndex);
            currentIndex--;
            cmd.undoCmd();
        }
    }
    
    /**
     * Decrement the current index and revert the action, the command is not
     * removed from the command list to allow redoing
     */
    public void redo() {
        if(canRedo()) {
            currentIndex++;
            Command cmd = commandList.get(currentIndex);
            cmd.doCmd();
        }
    }
    
    public void reset() {
        currentIndex = -1;
        commandList.clear();
    }
    
    public boolean canUndo() {
        return currentIndex >= 0;
    }
    
    public boolean canRedo() {
        return currentIndex < commandList.size() - 1;
    }
}
