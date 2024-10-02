class Compteur implements Runnable {
    private Thread processScores;
    private final Gui gui;
    private volatile boolean running = false;

    Compteur(Gui gui) {
        this.gui = gui;
    }

    public synchronized void start() {
        if (processScores == null || !processScores.isAlive()) {
            running = true;
            processScores = new Thread(this);
            processScores.start();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000); // Wait for 1 second
                updateScore();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                break; // Break out of the loop if interrupted
            }
        }
    }

    private synchronized void updateScore() {
        if (running) {
            gui.score++;
            gui.updateScoreValue();
        }
    }

    public synchronized void stop() {
        running = false; // Stop the loop in the run method
        if (processScores != null) {
            processScores.interrupt(); // Interrupt the thread if it's sleeping
        }
        processScores = null; // Reset the thread reference for future starts
    }

    public synchronized void reset() {
        gui.score = 0;
        gui.updateScoreValue();
    }
}
