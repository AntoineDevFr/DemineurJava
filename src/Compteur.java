class Compteur implements Runnable {

    private Thread processScores;

    Gui gui;

    Compteur(Gui gui) {
        this.gui = gui;
    }

    public void start() {
        processScores = new Thread(this);
        processScores.start();
    }

    public void run () {
        while (processScores != null) {
            try {
                Thread.sleep(1000);
                //affScores();
                gui.score++;
                gui.updateScoreValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void stop() {
        processScores = null;
    }

    public void reset() {
        gui.score = 0;
    }

}
