package com.dimotim.kubsolver.kub;

import com.dimotim.kubsolver.SimpleShaderProgram;
import com.dimotim.kubsolver.kub.controller.KubController;
import com.dimotim.kubsolver.kub.drawer.KubDrawer;
import com.dimotim.kubsolver.kub.facelet.Povorot;

import java.util.Random;

import lombok.Value;


public class Kub implements Povorotable{
    private final float dUgol=3.3f;
    private final KubController kubController;
    private final KubDrawer kubDrawer;
    private final State state;

    public Kub(int n){
        state=new State(n);
        kubDrawer=new KubDrawer(n);
        kubController=new KubController(n,this);
    }

    public Kub(int[][][] grani){
        state=new State(grani);
        kubDrawer=new KubDrawer(state.getN());
        kubController=new KubController(state.getN(),this);
    }

    public Kub(State start){
        state=new State(start);
        kubDrawer=new KubDrawer(state.getN());
        kubController=new KubController(state.getN(),this);
    }

    public State getState(){
        return new State(state);
    }

    public void shaderProgramChanged(SimpleShaderProgram shaderProgram){
        kubDrawer.setProgram(shaderProgram);
    }

    public void onTouch(float[] matrix, float[] monitorMatrix, TouchEvent event){
        kubController.command(matrix, monitorMatrix, event);
    }

    public void shuffle(){
        Random random=new Random(System.currentTimeMillis());
        int[][][] facelet=new int[6][state.getN()][state.getN()];
        state.getKvColor(facelet);
        for (int i = 0; i < 100; i++) {
            Povorot.rotate(facelet, random.nextInt(3) + 1, random.nextInt(state.getN()) + 1, random.nextInt(2) * 2 - 1);
        }
        state.setKvColor(facelet);
        state.resetPovorots();
    }

    public void povorot(int storona,int srez,int sign){
        if(state.isRotating())return;
        state.addPovorotsInQueue(new PovorotInf[]{new PovorotInf(srez,storona,sign)});
    }

    public void setPoslPovorots(PovorotInf[] povorots){
        if(state.isRotating())return;
        else state.addPovorotsInQueue(povorots);
    }

    public void draw() {
        state.addUgol(dUgol);
        kubDrawer.draw(state);
    }

    // MotionEvents is buffered in android pool
    @Value
    public static class TouchEvent{
        float x;
        float y;
        int actionMasked;
    }
}
