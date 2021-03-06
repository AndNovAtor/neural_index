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

import java.util.List;
import java.util.ArrayList;

/**
 * Generic Neuron interface and implementations for hidden and output layer
 * Created by novator on 01.11.2015.
 */

public class Neuron {

    public static final double LearningRate = 0.02; //TODO: What to do with "constant"?!

    /**
     * 		A default Neuron constructor.
     * 		- Description:		Creates a Neuron; general purposes.
     * 		- Purpose:			Creates a Neuron, linked to nothing, with a Linear network function.
     * 		- Prerequisites:	None.
     */

    Neuron() {
        mNetFunc = new Linear();
        mSumOfCharges=0.0;
    }

    /**
     * 		A Neuron constructor based on NetworkFunction.
     * 		- Description:		Creates a Neuron; mostly designed to create an output kind of neurons.
     * 			@param inNetFunc - a network function which is producing neuron's output signal;
     * 		- Purpose:			Creates a Neuron, linked to nothing, with a specific network function.
     * 		- Prerequisites:	The existence of NetworkFunction object.
     */

    Neuron( NetworkFunction inNetFunc ) {
        mNetFunc = inNetFunc;
        mSumOfCharges = 0.0;
    }

    /**
     * 		A Neuron constructor based on layer of Neurons.
     * 		- Description:		Creates a Neuron; mostly designed to create an input and hidden kinds of neurons.
     * 			@param inNeuronsLinkTo - a vector of pointers to Neurons which is representing a layer;
     * 			@param inNetFunc - a network function which is producing neuron's output signal;
     * 		- Purpose:			Creates a Neuron, linked to every Neuron in provided layer.
     * 		- Prerequisites:	The existence of std::vector<Neuron> and NetworkFunction.
     */

    Neuron(List<Neuron> inNeuronsLinkTo, NetworkFunction inNetFunc )
    {
	/*
	 * 		Net Function is an activation function for neuron
	*/


        mNetFunc = inNetFunc;

	/*
	 * 		Initially there is no input data, so sum of charges equal 0
	*/


        mSumOfCharges = 0.0;


        for (Neuron anInNeuronsLinkTo : inNeuronsLinkTo) {

            /*
             *		Creating a link, based on Neuron from vector for every neuron in vector
            */

            NeuralLink pLink = new NeuralLink(anInNeuronsLinkTo, 0.0);

            /*
             *		Newly created neuron will know who is linked to it, by maintaining a vector of links called mLinksToNeurons.
            */

            mLinksToNeurons.add(pLink);

            /*
             * 		A neuron, which is linked to newly created neuron, will know as well what its linked to, by maintaining a vector of input links.
            */

            anInNeuronsLinkTo.SetInputLink(pLink);
    /*		std::cin.get();
            NeuralLink * pInLink = inNeuronsLinkTo[i].GetInputLink().back();
            pInLink.SetWeightCorrectionTerm(10);
            std::cout << pLink.GetWeightCorrectionTerm() << std::endl;
            std::cout << pLink << std::endl;
            std::cout << pInLink<< std::endl;
            std::cin.get();*/
        }

    }

    List<NeuralLink>	GetLinksToNeurons( ) { return mLinksToNeurons; }
    NeuralLink get(int inIndexOfNeuralLink ) { return mLinksToNeurons.get(inIndexOfNeuralLink); }

    void SetLinkToNeuron( NeuralLink inNeuralLink ) { mLinksToNeurons.add( inNeuralLink ); }

    void Input( double inInputData ) { mSumOfCharges += inInputData; }
    double Fire()
    {
        for(NeuralLink pCurrentLink : mLinksToNeurons){
            Neuron pCurrentNeuronLinkedTo = pCurrentLink.GetNeuronLinkedTo();

            final double dWeight = pCurrentLink.GetWeight();
            double	dCharge = mSumOfCharges;
            double 	dXi =  (mNetFunc.Process(dCharge));
            double 	dOutput = dXi*dWeight;

            pCurrentLink.SetLastTranslatedSignal(dXi);
            pCurrentNeuronLinkedTo.Input( dOutput );
            //System.out.println("Link: " + iLink + ", Neuron fired: " + dOutput + " as func of: " + dCharge + " * " + dWeight);
        }
        //mSumOfCharges = 0;
        return mSumOfCharges;
    }
    int GetNumOfLinks() { return mLinksToNeurons.size( ); }
    double GetSumOfCharges() {return mSumOfCharges;}
    void ResetSumOfCharges() {mSumOfCharges = 0.0;}
    double Process() { return mNetFunc.Process( mSumOfCharges ); }
    double Process(double inArg) {return mNetFunc.Process(inArg);}
    double Derivative( ) { return mNetFunc.Derivative(mSumOfCharges); }

    void SetInputLink( NeuralLink inLink ) { mInputLinks.add( inLink ); }
    List<NeuralLink> GetInputLink( ) { return mInputLinks; }



    double PerformTrainingProcess(double inTarget) {return 0;}
    void applyWeightCorrection(double dErrorInformationTermj) {
        for(NeuralLink pInputLink : this.GetInputLink()){
            double Xi = pInputLink.GetLastTranslatedSignal();
            double dWeightCorrectionTerm = Xi*dErrorInformationTermj;
            //std::cout << "dWeightCorrectionTerm: " << dWeightCorrectionTerm << std::endl;
            pInputLink.SetWeightCorrectionTerm(LearningRate*dWeightCorrectionTerm);


            /*
             * 		Then hidden/output unit has to tell the input/hidden neurons the value of it ErrorInformationTerm, so we are setting its value
             * 		in the link object.
             */

            pInputLink.SetErrorInFormationTerm(dErrorInformationTermj);
        }
    }
    void PerformWeightsUpdating() {
        this.GetInputLink().forEach(NeuralLink::UpdateWeight);
    }


    void ShowNeuronState()
    {
        /*
         * 		Printing out Neuron's link's weights
        */

        for(int iNumOfOutLinks = 0; iNumOfOutLinks < mLinksToNeurons.size(); iNumOfOutLinks++ ){
            NeuralLink pNeuralLink = mLinksToNeurons.get(iNumOfOutLinks);
            System.out.println("    Link index: "+iNumOfOutLinks);
            System.out.println("      Weight: "+pNeuralLink.GetWeight()+"; Weight correction term: "+pNeuralLink.GetWeightCorrectionTerm());
        }
    }
    protected NetworkFunction mNetFunc;
    protected List<NeuralLink> mInputLinks = new ArrayList<>();
    protected List<NeuralLink> mLinksToNeurons = new ArrayList<>();

    protected double mSumOfCharges;
}

class OutputLayerNeuronDecorator extends Neuron {
    public OutputLayerNeuronDecorator(Neuron inNeuron) { mOutputCharge = 0; mNeuron = inNeuron; }

    public List<NeuralLink>	GetLinksToNeurons( ) { return mNeuron.GetLinksToNeurons( );}
    public NeuralLink get(int inIndexOfNeuralLink ) { return ( mNeuron.get( inIndexOfNeuralLink ) );}
    public void SetLinkToNeuron( NeuralLink inNeuralLink )			{ mNeuron.SetLinkToNeuron( inNeuralLink );}
    public double GetSumOfCharges( ) { return mNeuron.GetSumOfCharges( ); }

    public void ResetSumOfCharges( ) { mNeuron.ResetSumOfCharges( ); }
    public void Input( double inInputData ) 	{ mNeuron.Input( inInputData ); }
    public double Fire( ) {

        //double temp = mNeuron.GetSumOfCharges();
        double output = this.Process();
        mOutputCharge = output;
        //std::cout << "Output Neuron fired: " << output << " as func of: " << temp << std::endl;
        return output;

    }

    public int GetNumOfLinks( ) { return mNeuron.GetNumOfLinks( ); }


    public double Process( ) 			{ return mNeuron.Process( ); }
    public double Process( double inArg ) { return mNeuron.Process( inArg ); }

    public double Derivative( ) 			{ return mNeuron.Derivative( ); }

    public void SetInputLink( NeuralLink inLink ) { mNeuron.SetInputLink( inLink ); }
    public List<NeuralLink> GetInputLink( ) 			{ return mNeuron.GetInputLink( ); }

    public double PerformTrainingProcess(double inTarget) {
        double res;
        double dErrorInformationTerm = (inTarget - mOutputCharge) * mNeuron.Derivative();
        res = (inTarget - mOutputCharge)*(inTarget - mOutputCharge);
        //std::cout << "dErrorInformationTermOutput: " << dErrorInformationTerm << " as: " << "(" << inTarget << " - " << mOutputCharge << ")" << " * " << mNeuron.Derivative() << " .Derivative of:  " << mNeuron.GetSumOfCharges()<< std::endl;
        //std::cin.get();

    /*
     * 		For every link to that output, (inputLinks) calculate its weight correction term
     * 		and update the link with it.
    */
        applyWeightCorrection(dErrorInformationTerm);


        return res;
    }

    public void ShowNeuronState( ) { mNeuron.ShowNeuronState( ); }

    protected double mOutputCharge;
    protected Neuron mNeuron;

}

class HiddenLayerNeuronDecorator<T> extends Neuron {
    HiddenLayerNeuronDecorator( Neuron inNeuron )		{ mNeuron = inNeuron; }

    List<NeuralLink>	GetLinksToNeurons( ) { return mNeuron.GetLinksToNeurons( ); }
    void SetLinkToNeuron( NeuralLink inNeuralLink )			{ mNeuron.SetLinkToNeuron( inNeuralLink ); }
    double GetSumOfCharges( ) { return mNeuron.GetSumOfCharges( ) ;}

    void ResetSumOfCharges( ) {mNeuron.ResetSumOfCharges( ); }
    void Input( double inInputData ) 	{ mNeuron.Input( inInputData ); }
    double Fire( ) {
        /*
         * 		Hidden unit applies its activation function to compute its output signal
         * 		and sends this signal to all units in the layer above (output units).
        */

        for(NeuralLink pCurrentLink : mNeuron.GetLinksToNeurons()){
            Neuron pCurrentNeuronLinkedTo = pCurrentLink.GetNeuronLinkedTo();

            final double dWeight = pCurrentLink.GetWeight(); //TODO: And so what about constants? Need "final"?
            double	dCharge = mNeuron.GetSumOfCharges();
            double	dZj = (mNeuron.Process(dCharge));
            double 	dOutput = dZj*dWeight;

            pCurrentLink.SetLastTranslatedSignal(dZj);

            pCurrentNeuronLinkedTo.Input( dOutput );

            //std::cout << "Link: " << iLink << ", " << "Hidden Neuron fired: " << dOutput << " as func of: " << dCharge << " * " << dWeight << std::endl;
        }

        return mNeuron.GetSumOfCharges();
    }
    int GetNumOfLinks( ) { return mNeuron.GetNumOfLinks( ); }
    NeuralLink get(int inIndexOfNeuralLink ) { return ( mNeuron.get( inIndexOfNeuralLink) ); }

    double Process( ) 			{ return mNeuron.Process( ); }
    double Process( double inArg ) { return mNeuron.Process( inArg ); }

    double Derivative( ) 			{ return mNeuron.Derivative( ); }

    void SetInputLink( NeuralLink inLink ) { mNeuron.SetInputLink( inLink ); }
    List<NeuralLink> GetInputLink( ) 			{ return mNeuron.GetInputLink( ); }

    double PerformTrainingProcess(double inTarget) {
        /*
         * 		Hidden unit sums its delta inputs from units in the layer above
        */
        double dDeltaInputs = 0;
        for(NeuralLink pOutputLink: this.GetLinksToNeurons()){
            double dErrorInformationTerm = pOutputLink.GetErrorInFormationTerm();
            double dWeight = pOutputLink.GetWeight();
            dDeltaInputs += (dWeight * dErrorInformationTerm);
        }

        /*
        this.GetLinksToNeurons().forEach(NeuralLink::UpdateWeight);
        */

        double dErrorInformationTermj = dDeltaInputs * (this.Derivative());
        //std::cout << "dErrorInformationTermjHidden: " << dErrorInformationTermj << " as: " << dDeltaInputs << " * " << this.Derivative() << " .Derivative of:  " << mNeuron.GetSumOfCharges()<< std::endl;
        //std::cin.get();
        /*
         * 		For every link to that hidden neuron, (inputLinks) calculate its weight correction term
         * 		and update the link with it.
        */
        applyWeightCorrection(dErrorInformationTermj);
        return 0;
    }

    void ShowNeuronState( ) { mNeuron.ShowNeuronState( ); }
    protected Neuron mNeuron;

}
