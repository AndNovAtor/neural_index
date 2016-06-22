/*
 * Copyright © 2016 Andrey Novikov.
 *
 * Java code is based on Sovietmade (https://github.com/Sovietmade) c++
 * code (https://github.com/Sovietmade/NeuralNetworks)
 *
 * This file is part of neural_index.
 *
 * neural_index is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * neural_index is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with neural_index.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.andnovator.neural.network;

/**
 Created by novator on 01.11.2015.
 */
class NeuralLink {
    public NeuralLink() {
        mWeightToNeuron = 0.0;
        mNeuronLinkedTo = null;
        mWeightCorrectionTerm = 0;
        mErrorInformationTerm =0;
        mLastTranslatedSignal = 0;
    }
    public NeuralLink(Neuron inNeuronLinkedTo, double inWeightToNeuron) {
        mWeightToNeuron = inWeightToNeuron;
        mNeuronLinkedTo = inNeuronLinkedTo;
        mWeightCorrectionTerm = 0;
        mErrorInformationTerm = 0;
        mLastTranslatedSignal = 0;
    }
    public NeuralLink( Neuron inNeuronLinkedTo) {
       this(inNeuronLinkedTo, 0.0);
    }
    
    void SetWeight(double inWeight) {mWeightToNeuron = inWeight;}
    double GetWeight() {return mWeightToNeuron;}

    void SetNeuronLinkedTo(Neuron inNeuronLinkedTo) {mNeuronLinkedTo = inNeuronLinkedTo;}
    Neuron GetNeuronLinkedTo() {return mNeuronLinkedTo;}

    void SetWeightCorrectionTerm( double inWeightCorrectionTerm ) {mWeightCorrectionTerm = inWeightCorrectionTerm;}
    double GetWeightCorrectionTerm() {return mWeightCorrectionTerm;}

    void UpdateWeight() {mWeightToNeuron = mWeightToNeuron + mWeightCorrectionTerm;}

    double GetErrorInFormationTerm() {return mErrorInformationTerm;}
    public void SetErrorInFormationTerm(double inEITerm) {mErrorInformationTerm = inEITerm;}

    void SetLastTranslatedSignal( double inLastTranslatedSignal ) {mLastTranslatedSignal = inLastTranslatedSignal;}
    double GetLastTranslatedSignal() {return mLastTranslatedSignal;}
    
    protected double mWeightToNeuron;
    protected Neuron mNeuronLinkedTo;
    protected double mWeightCorrectionTerm;
    protected double mErrorInformationTerm;
    protected double mLastTranslatedSignal;
}