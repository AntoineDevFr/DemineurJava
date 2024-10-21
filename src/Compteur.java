class Compteur implements Runnable {
    private Thread processScores; // Thread pour exécuter le compteur
    private final Gui gui; // Référence à l'interface graphique (Gui)
    private volatile boolean running = false; // Indicateur pour savoir si le compteur est en cours d'exécution

    // Constructeur de la classe Compteur, qui prend l'interface graphique comme paramètre
    Compteur(Gui gui) {
        this.gui = gui; // Stocke la référence à l'interface graphique
    }

    // Démarre le compteur s'il n'est pas déjà en cours d'exécution
    public synchronized void start() {
        if (processScores == null || !processScores.isAlive()) { // Vérifie si le thread n'existe pas ou s'il est arrêté
            running = true; // Indique que le compteur doit être exécuté
            processScores = new Thread(this); // Crée un nouveau thread pour le compteur
            processScores.start(); // Démarre le thread
        }
    }

    // Méthode principale du thread, qui sera exécutée lorsqu'il démarre
    @Override
    public void run() {
        while (running) { // Boucle tant que le compteur doit tourner
            try {
                Thread.sleep(1000); // Attend 1 seconde
                updateScore(); // Met à jour le score chaque seconde
            } catch (InterruptedException e) { // Gestion des interruptions du thread
                Thread.currentThread().interrupt(); // Préserve l'état d'interruption
                break; // Sort de la boucle si le thread est interrompu
            }
        }
    }

    // Met à jour le score et rafraîchit l'affichage de l'interface graphique
    private synchronized void updateScore() {
        if (running) { // Si le compteur est toujours actif
            gui.score++; // Incrémente le score
            gui.updateScoreValue(); // Met à jour l'affichage du score dans l'interface
        }
    }

    // Arrête le compteur
    public synchronized void stop() {
        running = false; // Met fin à la boucle dans la méthode run
        if (processScores != null) {
            processScores.interrupt(); // Interrompt le thread s'il est en train de dormir
        }
        processScores = null; // Réinitialise la référence du thread pour pouvoir redémarrer plus tard
    }

    // Réinitialise le score à 0 et met à jour l'affichage de l'interface
    public synchronized void reset() {
        gui.score = 0; // Réinitialise le score à 0
        gui.updateScoreValue(); // Met à jour l'affichage du score dans l'interface
    }
}
