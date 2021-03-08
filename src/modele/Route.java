package modele;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

public class Route {
	
	/** constantes */
	public final static int BORNE_INF_X = 300; //position X min des points de la courbe de gauche
	public final static int LARGEUR = 150; //largeur de la route
	private final static int DISTANCE_Y = 70; //distance qui separe chaque point de chaque courbe
	public final static int BORNE_SUP_X = Terrain.LARGEUR_TERRAIN-BORNE_INF_X-LARGEUR; //position X max des points de la courbe de gauche
	
	/** attributs */
	private ArrayList<QuadCurve2D> courbes;
	private Voiture voiture;
	private PointControle pointControle;
	private CompteARebour compteARebour;
	
	/** constructeur */
	public Route(Voiture voiture) {
		this.voiture = voiture;
		this.initCoteGauche();
		this.pointControle = new PointControle(this.voiture);
	}
	
	/** cette methode initialise la liste de courbe de bas en haut jusqu'a ce que le dernier point de la derniere courbe soit en dehors de l'ecran */
	private void initCoteGauche() {
		this.courbes = new ArrayList<>();
		QuadCurve2D premiereCourbe = new QuadCurve2D.Double();
		premiereCourbe.setCurve(new Point2D.Double(BORNE_INF_X,Terrain.HAUTEUR_TERRAIN), 
				new Point2D.Double(BORNE_INF_X, Terrain.HAUTEUR_TERRAIN-DISTANCE_Y), 
				new Point2D.Double(BORNE_INF_X+(BORNE_SUP_X-BORNE_INF_X)/2, Terrain.HAUTEUR_TERRAIN-2*DISTANCE_Y));
		this.courbes.add(premiereCourbe);
		while(this.courbes.get(this.courbes.size()-1).getP2().getY() > 0) {
			this.courbes.add(ajouterCourbeCoteGauche());
		}
	}
	
	/** cette methode permet d'ajouter une courbe a la liste */
	private QuadCurve2D ajouterCourbeCoteGauche() {
		Point2D dernierPointControle = this.courbes.get(this.courbes.size()-1).getCtrlPt();
		Point2D dernierPointFinal = this.courbes.get(this.courbes.size()-1).getP2();
		Point2D nouveauControle;
		Point2D nouveauDernier;
		if(dernierPointControle.getX() != dernierPointFinal.getX()) {
			if(dernierPointControle.getX() < dernierPointFinal.getX()) {
				nouveauControle = new Point2D.Double(BORNE_SUP_X,dernierPointFinal.getY()-DISTANCE_Y); 
			}
			else {
				nouveauControle = new Point2D.Double(BORNE_INF_X, dernierPointFinal.getY()-DISTANCE_Y); 
			}
			nouveauDernier = new Point2D.Double(nouveauControle.getX(),nouveauControle.getY()-DISTANCE_Y); 
		}
		else {
			nouveauControle = new Point2D.Double(dernierPointFinal.getX(),dernierPointFinal.getY()-DISTANCE_Y); 
			nouveauDernier = new Point2D.Double(BORNE_INF_X+(BORNE_SUP_X-BORNE_INF_X)/2, nouveauControle.getY()-DISTANCE_Y); 
		}
		QuadCurve2D nouvelleCourbe = new QuadCurve2D.Double();
		nouvelleCourbe.setCurve(dernierPointFinal, nouveauControle, nouveauDernier);
		return nouvelleCourbe;
		
	}
	
	/** cette procedure modifie la coordonnee Y de chaque point de chaque et met a jour la liste (suppression/ajout de courbe) dans certaines conditions */
	public void avancer() {
		//on incremente les coordonnees Y de chaque points des courbes de la route
		this.courbes.forEach(c -> {
			Point2D premier = new Point2D.Double(c.getP1().getX(),c.getP1().getY()+this.voiture.getVitesse());
			Point2D controle = new Point2D.Double(c.getCtrlX(),c.getCtrlY()+this.voiture.getVitesse());
			Point2D dernier = new Point2D.Double(c.getP2().getX(),c.getP2().getY()+this.voiture.getVitesse());
			c.setCurve(premier, controle, dernier);
		});
		//partie qui ajoute et supprime des courbes au fur et a mesure de l'avancement
		if(this.courbes.get(this.courbes.size()-1).getP2().getY() > Terrain.HAUTEUR_HORIZON) {
			this.courbes.add(ajouterCourbeCoteGauche());
		}
		if(this.courbes.get(1).getCtrlY()>Terrain.HAUTEUR_TERRAIN) {
			this.courbes.remove(0);
			
		}
		this.pointControle.avancer();
	}
	
	/** retourne la courbe la plus proche en Y de la voiture depuis la liste de courbes */
	public QuadCurve2D getCourbeCourante(int y) {
		for(QuadCurve2D courbe : this.courbes) {
			if(courbe.getP2().getY() <= y && courbe.getP1().getY() >= y) {
				return courbe;
			}
		}
		return null;
	}
	
	/** retourne la coordonnee X du segment trace entre les points P1 et P2 de la courbe courante (+ largeur de route/2) */
	public float getXMilieuRoute(int y) {
		QuadCurve2D courbeCouranteGauche = this.getCourbeCourante(y);
		
		int p1x = (int)courbeCouranteGauche.getP1().getX(); //definition ligne milieu de route
		int p1y = (int)courbeCouranteGauche.getP1().getY();
		int p2x = (int)courbeCouranteGauche.getP2().getX();
		int p2y = (int)courbeCouranteGauche.getP2().getY();
		
		float pente = (p1y-p2y)/(float)(p1x-p2x); //calcul de la pente de cette ligne
		return -((p1y-y)/pente)+p1x+Route.LARGEUR/2; 
	}
	
	/** getter et setters */
	
	public PointControle getPointControle() {
		return this.pointControle;
	}

	
	public ArrayList<QuadCurve2D> getCourbes() {
		return this.courbes;
	}

	

}
