package vue;

import modele.Terrain;

import java.awt.*;

import javax.swing.JPanel;

public class Affichage extends JPanel {
	private static final long serialVersionUID = 6883630088377101348L;

	/** Attribut */
	private TerrainVue terrainVue;
	
	
	
	/** constructeur */
	public Affichage(TerrainVue terrainVue) {
		this.terrainVue = terrainVue;
		setPreferredSize(new Dimension(Terrain.LARGEUR_TERRAIN, Terrain.HAUTEUR_TERRAIN));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g); //permet de nettoyer l'image
		this.terrainVue.afficherTerrain(g);
	}

	public TerrainVue getTerrainVue() {
		return terrainVue;
	}

}
