package modele;

/** cette classe represente une voiture cote modele. Une voiture peut : 
 *  - accelerer ou freiner par rapport a sa position a la route
 *  - aller a gauche ou a droite sans sortir du terrain
 *  - sauter jusqu'a une hauteur maximale
 *  - freiner si elle rencontre un obstacle
 */

public class Voiture {

	/** constantes */
	public final static int LARGEUR_VOITURE = 80;
	public final static int HAUTEUR_VOITURE = 100;
	public final static int VITESSE_MAXIMALE = 5; //vitesse verticale max
	private final static float ACCELERATION = 0.01f; //force de l'acceleration
	private final static float FREINAGE = 0.05f; //force du freinage
	private final static float VITESSE_LATERALE = 0.5f; //vitesse de deplacement laterale du vehicule
	private final static float VITESSE_VERTICALE = 0.5f; //vitesse de deplacement verticale du vehicule
	private final static int HAUTEUR_SAUT = 300; //hauteur max que peut atteindre un saut

	/** attributs */
	private int posX;
	private int posY;
	private float velociteLaterale;
	private float velociteVerticale;
	private float vitesse;
	private boolean gauche;
	private boolean droite;
	private boolean saute;
	private Terrain terrain;

	/** constructeur */
	public Voiture(int x, int y, int vitesse) {
		this.posX = x;
		this.posY = y;
		this.velociteLaterale = 0;
		this.velociteVerticale = 0;
		this.vitesse = vitesse;
		this.droite = false;
		this.gauche = false;
		this.saute = false;
	}

	/** cette procedure permet a la voiture de bouger si elle va a gauche ou a droite et de freiner sinon */
	public void controler() {
		if(this.gauche && !this.droite && !this.saute) {
			this.versLaGauche();
		}
		else if(this.droite && !this.gauche && !this.saute) {
			this.versLaDroite();
		}
		else{
			this.freinerLateralement();
		}
		this.sauter();
		this.posX += this.velociteLaterale; // mise a jour de la position
		this.posY += this.velociteVerticale;
		this.controleLimites(); //correction de la position si en dehors du terrain
	}
	
	/** cette methode permet de gerer le saut et la chutte de la voiture */
	private void sauter() {
		if(this.saute) { //si on est en train de sauter
			this.posY -= VITESSE_MAXIMALE;
		}
		else { //sinon la voiture tombe
			this.velociteVerticale += VITESSE_VERTICALE; 
		}
	}


	/** cette procedure permet de corriger la position et d'annuler la velocite si la voiture est hors des bornes du terrain (et donc de l'ecran)*/
	private void controleLimites() {
		if( this.posX < 0 ) {
			this.posX = 0;
			this.velociteLaterale = 0;
		}
		else if ( this.posX + LARGEUR_VOITURE > Terrain.LARGEUR_TERRAIN) { // il faut prendre en compte la largeur de la voiture
			this.posX = Terrain.LARGEUR_TERRAIN - LARGEUR_VOITURE;
			this.velociteLaterale = 0;
		}
		if(this.posY < HAUTEUR_SAUT) {
			this.posY = HAUTEUR_SAUT;
			this.saute = false;
			this.velociteVerticale = 0;
		}
		else if(this.posY > Terrain.HAUTEUR_SOL) {
			this.posY = Terrain.HAUTEUR_SOL;
			this.velociteVerticale = 0;
		}
	}

	/** cette procedure permet d'augmenter le poids de la vitesse vers la gauche */
	private void versLaGauche() {
		this.velociteLaterale -= VITESSE_LATERALE;	

	}

	/** cette procedure permet d'augmenter le poids de la vitesse vers la droite */
	private void versLaDroite() {
		this.velociteLaterale += VITESSE_LATERALE;
	}

	/** cette procedure permet d'inhiber la velocite vers la gauche ou la droite */
	public void freinerLateralement() {
		if(this.velociteLaterale < 0) {
			this.versLaDroite();
		}
		else if(this.velociteLaterale > 0) {
			this.versLaGauche();
		}
	}

	/** permet de freiner ou accelerer la voiture du joueur en fonction de sa position par rapport a la route */
	public void controleVitesse() {
		Courbe courbeCourante = this.terrain.getRoute().getCourbeCourante(posY);
		float x = courbeCourante.getXMilieuRoute(posY);
		if(posX+LARGEUR_VOITURE/2 < x-Route.LARGEUR || posX+LARGEUR_VOITURE/2 > x + Route.LARGEUR) {
			this.freiner(1);
		}
		else if (detecterCollision(courbeCourante)) {
			this.freiner(3);
		}
		else {
			this.accelerer();
		}
	}


	/** cette fonction retourne la vitesse de la voiture multipliee (pas vraiment en Km/H) */
	public int vitesseEnKmH() {
		return  (int)(vitesse*25);
	}

	/** permet d'augmenter la vitesse de la voiture sans depasser la vitesse maximale */
	private void accelerer() {
		this.vitesse += ACCELERATION;
		if(vitesse > VITESSE_MAXIMALE) {
			this.vitesse = VITESSE_MAXIMALE;
		}
	}

	/** permet de diminuer la vitesse de la voiture sans depasser la vitesse minimale */
	private void freiner(int force) {
		this.vitesse -= FREINAGE*force;
		if(this.vitesse < 0) {
			this.vitesse = 0;
		}

	}
	
	/** renvoie vrai si le rectangle entourant la voiture est superpose au rectangle entourant l'obstacle le plus proche */
	private boolean detecterCollision(Courbe courbeCourante) {
		Obstacle obstacleCourant = courbeCourante.getObstacle();
		if (obstacleCourant != null && this.posY == Terrain.HAUTEUR_SOL &&
				this.posX < obstacleCourant.getX() + obstacleCourant.getLargeur() && this.posX + LARGEUR_VOITURE > obstacleCourant.getX() &&
				this.posY < obstacleCourant.getY() + obstacleCourant.getHauteur() && this.posY + HAUTEUR_VOITURE/2 > obstacleCourant.getY()) {
			return true;
		}
		return false;
	}
	
	/** permet de placer la voiture au centre de la courbe courante a partir de sa position Y */
	private void sePositionnerAuCentreDeLaRoute() {
		this.posX = (int)this.terrain.getRoute().getCourbeCourante(posY).getXMilieuRoute(posY);
	}

	/** getters et setters */

	public int getPosX() {
		return this.posX;
	}
	public int getPosY() {
		return this.posY;
	}

	public void setGauche(boolean gauche) {
		this.gauche = gauche;
	}

	public void setDroite(boolean droite) {
		this.droite = droite;
	}

	public void setSaute(boolean saute) {
		this.saute = saute;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
		this.sePositionnerAuCentreDeLaRoute();
	}

	public float getVitesse() {
		return this.vitesse;
	}

	public Terrain getTerrain() {
		return this.terrain;
	}

	public boolean isGauche() {
		return this.gauche;
	}

	public boolean isDroite() {
		return this.droite;
	}

	public boolean isSaute() {
		return this.saute;
	}

}

