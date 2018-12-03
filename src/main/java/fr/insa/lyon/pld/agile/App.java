package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.Map;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Démarrage de l'application." );
        
        Map map = new Map();
        MainController controller = new MainController(map);
    }
}
