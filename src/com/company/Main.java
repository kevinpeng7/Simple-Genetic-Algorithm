package com.company;

import java.util.ArrayList;

public class Main {

    int POPULATION_SIZE = 30;
    double CROSSOVER_RATE = 0.7;
    double MUTATION_RATE = 0.001;
    int GENE_LENGTH = 36;

    public byte[] randomGene(){
        byte[] gene = new byte[GENE_LENGTH];
        for (int i = 0; i < gene.length; i++) {
            gene[i] = (byte)(Math.random()*2);
        }
        return gene;
    }

    public byte[] crossover(byte[] gene1, byte[] gene2){
        int pos = (int)(Math.random()*GENE_LENGTH);
        byte[] gene = new byte[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            if(i<pos) gene[i] = gene1[i];
            else gene[i] = gene2[i];
        }
        return gene;
    }

    public ArrayList<Integer> decode(byte[] gene){
        boolean num = true; // if num is true, the decoder is looking for a number next
        ArrayList<Integer> decoded = new ArrayList<Integer>();
        for (int i = 0; i <= GENE_LENGTH-4; i+=4) {
            int val = 8*gene[i] + 4 *gene[i+1] + 2*gene[i+2] + gene[i+3]; //binary evaluation
            if(num && val < 10){
                decoded.add(val);
                num = false;
            }
            else if(!num && val>=10 && val <14){
                decoded.add(val);
                num = true;
            }
        }
        if(num){
            decoded.remove(decoded.size()-1); // remove an operator if no number follows
        }
        return decoded;
    }

    public double evaluate(ArrayList<Integer> gene){
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

    //public double findFitness(doub)

    public static void main(String[] args) {
	// write your code here
    }
}
