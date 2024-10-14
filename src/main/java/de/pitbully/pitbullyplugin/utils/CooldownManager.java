package de.pitbully.pitbullyplugin.utils;

public class CooldownManager extends Thread{
    private int counter = 0;
    private boolean running = true;

    @Override
    public void run() {
        while(running) {
            counter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCounter() {
        return counter;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
