package com.adryan.config;

public final class SleepConfig {

    // Aici setezi procentul de jucători care trebuie să doarmă
    // 0.1F = 10%, 0.5F = 50%, 1.0F = 100%
	
    private float sleepPercentage = 0.1F;

    public float getSleepPercentage() {
        return this.sleepPercentage;
    }

    public void setSleepPercentage(float sleepPercentage) {
        this.sleepPercentage = sleepPercentage;
    }
}