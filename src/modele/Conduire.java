package modele;

public class Conduire extends Thread{
	private volatile boolean running = true;
	private Voiture voiture;
	
	public Conduire(Voiture voiture) {
		this.voiture = voiture;
	}
	
	@Override
	public void run() {
		while(running) {
			this.voiture.controler();
			synchronized(this.voiture.getTerrain().getRoute().getCourbes()) {
				this.voiture.controleVitesse();
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void arreter() {
		this.running = false;
	}

}
