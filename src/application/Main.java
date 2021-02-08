package application;
import javax.swing.JFrame;

import controleur.Controleur;
import modele.Avancer;
import modele.Conduire;
import modele.Route;
import modele.Terrain;
import modele.Voiture;
import vue.Affichage;
import vue.Afficher;
import vue.TerrainVue;


public class Main {

	public static void main(String[] args) {
		Voiture voiture = new Voiture(50, 400, 3);
		Route route = new Route(voiture);
		Terrain terrain = new Terrain(route);
		TerrainVue terrainVue = new TerrainVue(terrain);
		
		Affichage affichage = new Affichage(voiture, terrainVue);
		Controleur controleur = new Controleur(voiture);
		affichage.setFocusable(true);
		affichage.addKeyListener(controleur);
		
		//lancement des threads
		new Conduire(voiture).start();
		new Avancer(route).start();
		new Afficher(affichage).start();
		
		
		JFrame fenetre = new JFrame("MagneticRoad"); //instancie une fenetre avec un titre
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //permet de quitter le programme quand on clique sur la croix
		fenetre.add(affichage); //ajoute le component a la fenetre
		fenetre.pack();
		fenetre.setVisible(true); //rend la fenetre visible
	}

}
