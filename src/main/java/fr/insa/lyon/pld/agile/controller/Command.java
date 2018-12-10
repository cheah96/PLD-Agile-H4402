package fr.insa.lyon.pld.agile.controller;

/**
 *
 * @author scheah
 */
public interface Command {
    void doCmd();
    
    void undoCmd();
}
