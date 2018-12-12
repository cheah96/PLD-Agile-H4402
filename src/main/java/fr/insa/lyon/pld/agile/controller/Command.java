package fr.insa.lyon.pld.agile.controller;

public interface Command {
    /**
     * Perform an action using the design pattern Command.
     */
    void doCmd();
    /**
     * Revert an action using the design pattern Command.
     */
    void undoCmd();
}
