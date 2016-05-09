package com.andnovator.neural.network;

/**
 Created by novator on 01.11.2015.
 */
class NeuralLink<T>
{
    public NeuralLink() {
        mWeightToNeuron = 0.0;
        mNeuronLinkedTo = null;
        mWeightCorrectionTerm = 0;
        mErrorInformationTerm =0;
        mLastTranslatedSignal = 0;
    }
    public NeuralLink( Neuron<T> inNeuronLinkedTo, double inWeightToNeuron) {
        mWeightToNeuron = inWeightToNeuron;
        mNeuronLinkedTo = inNeuronLinkedTo;
        mWeightCorrectionTerm = 0;
        mErrorInformationTerm = 0;
        mLastTranslatedSignal = 0;
    }
    public NeuralLink( Neuron<T> inNeuronLinkedTo) {
       this(inNeuronLinkedTo, 0.0);
    }
    
    void SetWeight(double inWeight) {mWeightToNeuron = inWeight;}
    double GetWeight() {return mWeightToNeuron;}

    void SetNeuronLinkedTo(Neuron<T> inNeuronLinkedTo) {mNeuronLinkedTo = inNeuronLinkedTo;}
    Neuron<T> GetNeuronLinkedTo() {return mNeuronLinkedTo;}

    void SetWeightCorrectionTerm( double inWeightCorrectionTerm ) {mWeightCorrectionTerm = inWeightCorrectionTerm;}
    double GetWeightCorrectionTerm() {return mWeightCorrectionTerm;}

    void UpdateWeight() {mWeightToNeuron = mWeightToNeuron + mWeightCorrectionTerm;}

    double GetErrorInFormationTerm() {return mErrorInformationTerm;}
    public void SetErrorInFormationTerm(double inEITerm) {mErrorInformationTerm = inEITerm;}

    void SetLastTranslatedSignal( double inLastTranslatedSignal ) {mLastTranslatedSignal = inLastTranslatedSignal;}
    double GetLastTranslatedSignal() {return mLastTranslatedSignal;}
    
    protected double mWeightToNeuron;
    protected Neuron<T> mNeuronLinkedTo;
    protected double mWeightCorrectionTerm;
    protected double mErrorInformationTerm;
    protected double mLastTranslatedSignal;
}