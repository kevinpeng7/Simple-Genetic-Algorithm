package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static final int POPULATION_SIZE = 30;
    public static final double CROSSOVER_RATE = 0.7;
    public static final double MUTATION_RATE = 0.001;
    public static final int GENE_LENGTH = 44;

    public static byte[] randomGene(){
        byte[] gene = new byte[GENE_LENGTH];
        for (int i = 0; i < gene.length; i++) {
            gene[i] = (byte)(Math.random()*2);
        }
        return gene;
    }

    public static byte[] crossover(byte[] gene1, byte[] gene2){
        if(Math.random()>CROSSOVER_RATE) return gene1;
        int pos = (int)(Math.random()*GENE_LENGTH);
        byte[] gene = new byte[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            if(i<pos) gene[i] = gene1[i];
            else gene[i] = gene2[i];
        }
        return gene;
    }

    public static byte[] mutate(byte[] gene){
        for (int i = 0; i < gene.length; i++) {
            if(Math.random()<MUTATION_RATE){
                if(gene[i]==1)gene[i]=0;
                else gene[i] =1;
            }
        }
        return gene;
    }

    public static ArrayList<Integer> decode(byte[] gene){
        boolean num = true; // if num is true, the decoder is looking for a number next
        ArrayList<Integer> decoded = new ArrayList<Integer>();
        for (int i = 0; i <= GENE_LENGTH-4; i+=4) {
            int val = 1 + 8*gene[i] + 4 *gene[i+1] + 2*gene[i+2] + gene[i+3]; //binary evaluation
            if(num && val < 10){
                decoded.add(val);
                num = false;
            }
            else if(!num && val>=10 && val <14){
                decoded.add(val);
                num = true;
            }
        }
        if(num && decoded.size()>0){
            decoded.remove(decoded.size()-1); // remove an operator if no number follows
        }
        return decoded;
    }

    public static double evaluate(ArrayList<Integer> gene){
        if(gene.size()==0) return 0;
        double result = gene.get(0);
        for (int i = 1; i < gene.size(); i+=2) {
            switch(gene.get(i)){
                case 10:
                    result += gene.get(i+1); break;
                case 11:
                    result -= gene.get(i+1); break;
                case 12:
                    result *= gene.get(i+1); break;
                case 13:
                    result /= gene.get(i+1); break;
            }
        }

        return result;
    }

    public static String evaluateString(ArrayList<Integer> gene){
        if(gene.size()==0) return "0";
        String ret = "";
        for (int i = 0; i < gene.size(); i++) {
            String s;
            switch(gene.get(i)){
                case 0: ret = ret + "0"; break;
                case 1: ret = ret + "1"; break;
                case 2: ret = ret + "2"; break;
                case 3: ret = ret + "3"; break;
                case 4: ret = ret + "4"; break;
                case 5: ret = ret + "5"; break;
                case 6: ret = ret + "6"; break;
                case 7: ret = ret + "7"; break;
                case 8: ret = ret + "8"; break;
                case 9: ret = ret + "9"; break;
                case 10: ret = ret + "+"; break;
                case 11: ret = ret + "-"; break;
                case 12: ret = ret + "*"; break;
                case 13: ret = ret + "/"; break;
            }
        }
        return ret;
    }

    public static double findFitness(double val, double goal){
        if(val == goal) return 0;
        else{
            return Math.abs(1 / (goal - val));
        }
    }

    public static ArrayList<Double> fitnessMapping(ArrayList<byte[]> population, double goal){
        ArrayList<Double> mapping = new ArrayList<Double>();
        for (int i = 0; i < population.size(); i++) {
            mapping.add(findFitness(evaluate(decode(population.get(i))),goal));
        }
        return mapping;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Input a value.");
        double goal = in.nextDouble();
        boolean solved = false;
        ArrayList<byte[]> population = new ArrayList<byte[]>();
        for (int i = 0; i < POPULATION_SIZE; i++) { // generate population
            population.add(randomGene());
        }
        int generation =0;
        while(!solved){
            System.out.println("Generation " + (generation++));
            ArrayList<Double> mapping = fitnessMapping(population,goal);

            //get total for denominator
            double total=0;
            for (int i = 0; i < mapping.size(); i++) {
                total+=mapping.get(i);
            }

            // generated selected
            int[] selected = new int[6];
            for (int i = 0; i < selected.length; i++) {
                selected[i]=-1;
            }

            // find 6 randomly with proportion to their fitness score
            int counter =0;
            while(counter<6){
                double rand = Math.random()*total;
                int index=0;
                for (int i = 0; i < mapping.size(); i++) {
                    rand -= mapping.get(i);
                    index = i;
                    if(rand<0) break;
                }
                boolean skip = false;
                for (int i = 0; i <selected.length ; i++) {
                    if(index == selected[i]) skip = true;
                }
                if(!skip){ // skip if in the list already
                    selected[counter] = index;
                    counter++;
                }
            }

            for (int i = 0; i < selected.length; i+=2) { // add new genes
                byte[] gene1 = population.get(selected[i]);
                byte[] gene2 = population.get(selected[i+1]);
                byte[] new_gene = crossover(gene1, gene2);
                new_gene = mutate(new_gene);
                population.add(new_gene);
            }
            double lowest;
            int index;
            while(population.size()>POPULATION_SIZE && !solved){ //trims population and looks for success
                lowest = 100;
                index = -1;
                for (int i = 0; i < population.size(); i++) {
                    double eval = findFitness(evaluate(decode(population.get(i))),goal);
                    if (eval == 0){
                        solved = true;
                        for (int j = 0; j < population.get(i).length; j++) {
                            System.out.print(population.get(i)[j]);
                        }
                        System.out.println("\n" + evaluateString(decode(population.get(i))));
                        break;
                    }
                    else{
                        if(eval<lowest) {
                            index = i;
                            lowest = eval;
                        }
                    }
                }
                population.remove(index);
            }
        }
    }
}
