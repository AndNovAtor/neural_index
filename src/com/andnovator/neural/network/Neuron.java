package com.andnovator.neural.network;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by novator on 01.11.2015.
 */

public class Neuron<T> {

    public static final double LearningRate = 0.01; //TODO: What to do with "constant"?!

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

    Neuron(List<Neuron<T>> inNeuronsLinkTo, NetworkFunction inNetFunc )
    {
	/*
	 * 		Net Function is an activation function for neuron
	*/


        mNetFunc = inNetFunc;

	/*
	 * 		Initially there is no input data, so sum of charges equal 0
	*/


        mSumOfCharges = 0.0;


        for (Neuron<T> anInNeuronsLinkTo : inNeuronsLinkTo) {

            /*
             *		Creating a link, based on Neuron from vector for every neuron in vector
            */

            NeuralLink<T> pLink = new NeuralLink<>(anInNeuronsLinkTo, 0.0);

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

    List<NeuralLink<T>>	GetLinksToNeurons( ) { return mLinksToNeurons; }
    NeuralLink<T> get( int inIndexOfNeuralLink ) { return mLinksToNeurons.get(inIndexOfNeuralLink); }

    void SetLinkToNeuron( NeuralLink<T> inNeuralLink ) { mLinksToNeurons.add( inNeuralLink ); }

    void Input( double inInputData ) { mSumOfCharges += inInputData; }
    double Fire()
    {
        for(int iLink = 0; iLink < this.GetNumOfLinks(); iLink++){
            NeuralLink<T> pCurrentLink = mLinksToNeurons.get(iLink);
            Neuron<T> pCurrentNeuronLinkedTo = pCurrentLink.GetNeuronLinkedTo();

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

    void SetInputLink( NeuralLink<T> inLink ) { mInputLinks.add( inLink ); }
    List<NeuralLink<T>> GetInputLink( ) { return mInputLinks; }



    double PerformTrainingProcess(double inTarget) {return 0;}
    void PerformWeightsUpdating() {}

    void ShowNeuronState()
    {
        /*
         * 		Printing out Neuron's link's weights
        */

        for(int iNumOfOutLinks = 0; iNumOfOutLinks < mLinksToNeurons.size(); iNumOfOutLinks++ ){
            NeuralLink<T> pNeuralLink = mLinksToNeurons.get(iNumOfOutLinks);
            System.out.println("    Link index: "+iNumOfOutLinks);
            System.out.println("      Weight: "+pNeuralLink.GetWeight()+"; Weight correction term: "+pNeuralLink.GetWeightCorrectionTerm());
        }
    }
    protected NetworkFunction mNetFunc;
    protected List<NeuralLink<T>> mInputLinks = new ArrayList<>();
    protected List<NeuralLink<T>> mLinksToNeurons = new ArrayList<>();

    protected double mSumOfCharges;
}

class OutputLayerNeuronDecorator<T> extends Neuron<T> {
    public OutputLayerNeuronDecorator(Neuron<T> inNeuron) { mOutputCharge = 0; mNeuron = inNeuron; }

    public List<NeuralLink<T>>	GetLinksToNeurons( ) { return mNeuron.GetLinksToNeurons( );}
    public NeuralLink<T>			get( int inIndexOfNeuralLink ) { return ( mNeuron.get( inIndexOfNeuralLink ) );}
    public void SetLinkToNeuron( NeuralLink<T> inNeuralLink )			{ mNeuron.SetLinkToNeuron( inNeuralLink );}
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

    public void SetInputLink( NeuralLink<T> inLink ) { mNeuron.SetInputLink( inLink ); }
    public List<NeuralLink<T>> GetInputLink( ) 			{ return mNeuron.GetInputLink( ); }

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
        for(int iInputLink = 0; iInputLink < (this.GetInputLink()).size(); iInputLink++){
            NeuralLink<T> pInputLink = (this.GetInputLink()).get(iInputLink);
            double Zj = pInputLink.GetLastTranslatedSignal();
            double dWeightCorrectionTerm = Zj*dErrorInformationTerm;
            //std::cout << "dWeightCorrectionTerm: " << dWeightCorrectionTerm << std::endl;
            pInputLink.SetWeightCorrectionTerm(LearningRate*dWeightCorrectionTerm);


		/*
		 * 		Then output unit has to tell the hidden neurons the value of it ErrorInformationTerm, so we are setting its value
		 * 		in the link object.
		 */

            pInputLink.SetErrorInFormationTerm(dErrorInformationTerm);
        }


        return res;
    }

    public void PerformWeightsUpdating()
    {
        for( int iInputLink = 0; iInputLink < (this.GetInputLink()).size(); iInputLink++){
            NeuralLink<T> pInputLink = (this.GetInputLink()).get(iInputLink);

            pInputLink.UpdateWeight();
            //std::cout<<"";
        }
    }
    public void ShowNeuronState( ) { mNeuron.ShowNeuronState( ); }

    protected double mOutputCharge;
    protected Neuron<T> mNeuron;

}

class HiddenLayerNeuronDecorator<T> extends Neuron<T>
{
    HiddenLayerNeuronDecorator( Neuron<T> inNeuron )		{ mNeuron = inNeuron; }

    List<NeuralLink<T>>	GetLinksToNeurons( ) { return mNeuron.GetLinksToNeurons( ); }
    void SetLinkToNeuron( NeuralLink<T> inNeuralLink )			{ mNeuron.SetLinkToNeuron( inNeuralLink ); }
    double GetSumOfCharges( ) { return mNeuron.GetSumOfCharges( ) ;}

    void ResetSumOfCharges( ) {mNeuron.ResetSumOfCharges( ); }
    void Input( double inInputData ) 	{ mNeuron.Input( inInputData ); }
    double Fire( ) {
        /*
         * 		Hidden unit applies its activation function to compute its output signal
         * 		and sends this signal to all units in the layer above (output units).
        */

        for(int iLink = 0; iLink < this.GetNumOfLinks(); iLink++){

            NeuralLink<T> pCurrentLink = mNeuron.get(iLink);
            Neuron<T> pCurrentNeuronLinkedTo = pCurrentLink.GetNeuronLinkedTo();

            final double dWeight = mNeuron.get(iLink).GetWeight(); //TODO: And so what about constants? Need "final"?
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
    NeuralLink<T> get( int inIndexOfNeuralLink ) { return ( mNeuron.get( inIndexOfNeuralLink) ); }

    double Process( ) 			{ return mNeuron.Process( ); }
    double Process( double inArg ) { return mNeuron.Process( inArg ); }

    double Derivative( ) 			{ return mNeuron.Derivative( ); }

    void SetInputLink( NeuralLink<T> inLink ) { mNeuron.SetInputLink( inLink ); }
    List<NeuralLink<T>> GetInputLink( ) 			{ return mNeuron.GetInputLink( ); }

    double PerformTrainingProcess(double inTarget) {
        /*
         * 		Hidden unit sums its delta inputs from units in the layer above
        */
        double dDeltaInputs = 0;
        for(int iOutputLink = 0; iOutputLink < (this.GetNumOfLinks()); iOutputLink++){
            NeuralLink<T> pOutputLink = (this.GetLinksToNeurons()).get(iOutputLink);
            double dErrorInformationTerm = pOutputLink.GetErrorInFormationTerm();
            double dWeight = pOutputLink.GetWeight();
            dDeltaInputs = dDeltaInputs + (dWeight*dErrorInformationTerm);
        }

    /*	for(int iOutputLink = 0; iOutputLink < (this.GetNumOfLinks()); iOutputLink++){
                NeuralLink * pOutputLink = (this.GetLinksToNeurons()).get(iOutputLink);
                pOutputLink.UpdateWeight();
        }*/

        double dErrorInformationTermj = dDeltaInputs * (this.Derivative());
        //std::cout << "dErrorInformationTermjHidden: " << dErrorInformationTermj << " as: " << dDeltaInputs << " * " << this.Derivative() << " .Derivative of:  " << mNeuron.GetSumOfCharges()<< std::endl;
        //std::cin.get();
        /*
         * 		For every link to that hidden neuron, (inputLinks) calculate its weight correction term
         * 		and update the link with it.
        */
        for(int iInputLink = 0; iInputLink < (this.GetInputLink()).size(); iInputLink++){
            NeuralLink<T> pInputLink = (this.GetInputLink()).get(iInputLink);
            double Xi = pInputLink.GetLastTranslatedSignal();
            double dWeightCorrectionTerm = Xi*dErrorInformationTermj;
            //std::cout << "dWeightCorrectionTerm: " << dWeightCorrectionTerm << std::endl;
            pInputLink.SetWeightCorrectionTerm(LearningRate*dWeightCorrectionTerm);


            /*
             * 		Then hidden unit has to tell the input neurons the value of it ErrorInformationTerm, so we are setting its value
             * 		in the link object.
             */

            pInputLink.SetErrorInFormationTerm(dErrorInformationTermj);
        }
        return 0;
    }
    void PerformWeightsUpdating( )
    {
        for( int iInputLink = 0; iInputLink < (this.GetInputLink()).size(); iInputLink++){
            NeuralLink<T> pInputLink = (this.GetInputLink()).get(iInputLink);

            pInputLink.UpdateWeight();
            //std::cout<<"";
        }
    }

    void ShowNeuronState( ) { mNeuron.ShowNeuronState( ); }
    protected Neuron<T> mNeuron;

}
