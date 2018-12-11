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
    
    public CommandList() {
        commandList = new LinkedList<>();
        currentIndex = -1;
    }
    
    public void addCommand(Command cmd) {
        int i = currentIndex + 1;
        while (i < commandList.size()) {
            commandList.remove(i);
        }
        currentIndex++;
        commandList.add(currentIndex, cmd);
        cmd.doCmd();
    }
    
    public void undo() {
        if(canUndo()) {
            Command cmd = commandList.get(currentIndex);
            currentIndex--;
            cmd.undoCmd();
        }
    }
    
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
