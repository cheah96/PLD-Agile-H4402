package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Démarrage de l'application." );
        
        Map map = new Map();
        Window window = new Window(map);
        // window.main();
    }
}
