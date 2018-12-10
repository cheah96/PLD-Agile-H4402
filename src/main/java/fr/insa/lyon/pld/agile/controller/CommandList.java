package fr.insa.lyon.pld.agile.controller;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author scheah
 */
public class CommandList {
    private List<Command> commandList; 
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
        if(currentIndex >= 0) {
            Command c = commandList.get(currentIndex);
            currentIndex--;
            c.undoCmd();
        }
    }
    
    public void redo() {
        if(currentIndex < commandList.size() -1) {
            currentIndex++;
            Command cmd = commandList.get(currentIndex);
            cmd.doCmd();
        }
    }
    
    public void reset() {
        currentIndex = -1;
        commandList.clear();
    }
}
