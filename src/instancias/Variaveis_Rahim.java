package instancias;

import java.util.HashMap;
import java.util.Random;

public class Variaveis_Rahim {

	/**
	 * Nivel de estoque inicial do Cliente j
	 */
	private double[] I_j0;

	/**
	 * Estoque do Cliente j no periodo t
	 */
	private double[][] Ijt ;

	/**
	 * @return the yt_v
	 */
	public int[][] getYt_v() {
		return Yt_v;
	}


	/**
	 * @param yt_v the yt_v to set
	 */
	public void setYt_v(int[][] yt_v) {
		Yt_v = yt_v;
	}

	/**
	 * Quantidade restante no ve�culo v quando ele viaja do ve�culo i para o j no tempo t
	 */
	private double[][][][] Qijt_v ;





	/**
	 * Quantidade entregue no local j no periodo t
	 * retorna 0 caso n�o tenha entrega.
	 */
	private double[][] qjt;

	/**
	 * Variavel Binaria,
	 * 1 se o local j � visitado imediatamente ap�s o local i no periodo t.
	 * 0 caso contr�rio.
	 */
	private int[][][][] Xijt_v ;

	/**
	 * Variavel Binaria,
	 * 1 se o ve�culo v est� sendo usado no periodo t.
	 * 0 caso contr�rio.
	 */
	private int[][] Yt_v;

	/**
	 * Variavel Binaria,
	 * 1 se o ve�culo v est� sendo usado no periodo t.
	 * 0 caso contr�rio.
	 */
	private double custo;
	private double UB;
	private double LB;

	/**
	 * @return the i_j0
	 */
	public double[] getI_j0() {
		return I_j0;
	}


	/**
	 * @return the uB
	 */
	public double getUB() {
		return UB;
	}


	/**
	 * @param uB the uB to set
	 */
	public void setUB(double uB) {
		UB = uB;
	}


	/**
	 * @return the lB
	 */
	public double getLB() {
		return LB;
	}


	/**
	 * @param lB the lB to set
	 */
	public void setLB(double lB) {
		LB = lB;
	}


	/**
	 * @return the ijt
	 */
	public double[][] getIjt() {
		return Ijt;
	}


	/**
	 * @param ijt the ijt to set
	 */
	public void setIjt(double[][] ijt) {
		Ijt = ijt;
	}


	/**
	 * @return the custo
	 */
	public double getCusto() {
		return custo;
	}


	/**
	 * @param custo the custo to set
	 */
	public void setCusto(double custo) {
		this.custo = custo;
	}


	

	/**
	 * @param i_j0 the i_j0 to set
	 */
	public void setI_j0(double[] i_j0) {
		I_j0 = i_j0;
	}

	
	/**
	 * @return the qijt_v
	 */
	public double[][][][] getQijt_v() {
		return Qijt_v;
	}

	/**
	 * @param qijt_v the qijt_v to set
	 */
	public void setQijt_v(double[][][][] qijt_v) {
		Qijt_v = qijt_v;
	}

	/**
	 * @return the qjt
	 */
	public double[][] getQjt() {
		return qjt;
	}

	/**
	 * @param qjt the qjt to set
	 */
	public void setQjt(double[][] qjt) {
		this.qjt = qjt;
	}

	/**
	 * @return the xijt_v
	 */
	public int[][][][] getXijt_v() {
		return Xijt_v;
	}

	/**
	 * @param xijt_v the xijt_v to set
	 */
	public void setXijt_v(int[][][][] xijt_v) {
		Xijt_v = xijt_v;
	}

	
	public Variaveis_Rahim(){
	
	}
	public Variaveis_Rahim(Instancia_Rahim i,double [] I_j0Range){

		if(I_j0Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'I_j0Range' com tamanho incorreto: Deve conter [min,max]");

		Random rand = new Random(1);

		this.I_j0 = new double[i.getS()+1];
		for(int j = 0;j < i.getS()+1; j++){
			//this.I_j0.put(String.valueOf(j)+"0",I_j0Range[0] + (I_j0Range[1] - I_j0Range[0]) * rand.nextdouble());
			this.I_j0[j] =I_j0Range[0] + (I_j0Range[1] - I_j0Range[0]) * rand.nextDouble();
		}

	}
	public Variaveis_Rahim(Instancia_Rahim_new i,double [] I_j0Range){

		if(I_j0Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'I_j0Range' com tamanho incorreto: Deve conter [min,max]");

/*	this.I_j0 = new double[i.getS()];
		for(int j = 0;j < i.getS()+1; j++){
			//this.I_j0.put(String.valueOf(j)+"0",I_j0Range[0] + (I_j0Range[1] - I_j0Range[0]) * rand.nextdouble());
			this.I_j0[j] =I_j0Range[0] + (I_j0Range[1] - I_j0Range[0]) * rand.nextDouble();
		}*/
	}

	public void setX0(Instancia_Rahim inst){
		int [][][][] Xijt_v = new int[inst.getS()+1][inst.getS()+1][inst.getT()][inst.getS()+1];
		int [][] Yt_v = new int[inst.getT()][inst.getS()+1];
		
		int v = 0 ;
		for (int i = 1; i < inst.getS()+1; i++) {//Para cada i
			for (int t = 1; t < inst.getT(); t++) { //Para cada t
				Xijt_v[0][i][t][v] = 1;
				Xijt_v[i][0][t][v] = 1;
				Yt_v[t][v] = 1;
			}
			v ++;
		}

		this.Xijt_v = Xijt_v;
		this.Yt_v = Yt_v;
	}
	/*public void setX0(Instancia_Rahim inst){
		int [][][][] Xijt_v = new int[inst.getS()+1][inst.getS()+1][inst.getT()][inst.getS()+1];

		for (int i = 0; i < inst.getS()+1; i++) {//Para cada i
			for (int j = 0; j < inst.getS()+1; j++) { //Para cada j
				for (int t = 0; t < inst.getT(); t++) { //Para cada t
					for (int v = 0; v < inst.getS()+1; v++) {//Para cada v

						boolean alocado = false;

						for (int i1 = 0; i1 < inst.getS()+1; i1++) {//Para cada v
							for (int j1 = 0; j1 < inst.getS()+1; j1++) {//Para cada v
								if(Xijt_v[i1][j1][t][v] == 1)
								{ 
									alocado = true;
									break;
								}
							}
						}
						for (int v1 = 0; v1 < inst.getS()+1; v1++) {//Para cada v
							if(v1 != v)
								if(Xijt_v[i][j][t][v1] == 1){ alocado = true;break;}
						}

						if(!alocado){
							if(i == 0 && j == 0){
								Xijt_v[i][j][t][v] = 0;
							}
							else if(i == 0 || j == 0){
								Xijt_v[i][j][t][v] = 1; 
							}
							else{
								Xijt_v[i][j][t][v] = 0;
							}
						}
					}
				}
			}
		}

		this.Xijt_v = Xijt_v;
	}*/
	/*
	public void setY0(Instancia_Rahim inst){

		int [][] Yt_v = new int[inst.getT()][inst.getS()+1];


		for (int t = 0; t < inst.getT(); t++) { //Para cada t
			for (int v = 0; v < inst.getS()+1; v++) {//Para cada v
				Yt_v[t][v] = 1; 
			}
		}

		this.Yt_v = Yt_v;
	}*/
}
