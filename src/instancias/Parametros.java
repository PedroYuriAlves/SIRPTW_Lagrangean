package instancias;

import java.util.HashMap;
import java.util.Random;
import util.*;

import javax.sql.rowset.spi.TransactionalWriter;

/**
 * @author Krespo
 *
 */
public class Parametros {

	Funcoes f = new Funcoes();
	public int alg;
	public int restrInv;
	public int restrCap;
	/**
	 * Numero de Clientes.
	 */
	public int S;
	/**
	 * Tempo de Horizonte.
	 */
	public int H;

	/**
	 * Delivery Rate por Tonelada/ Min
	 * Ex 5 toneladas/minuto
	 */
	public int DR;
	/**
	 * Unidades de tempo no periodo t
	 */
	public int τt;

	/**
	 * Capacidade do Veículo.
	 */
	public int kv;


	/**
	 * Custo Fixo de Operação do veiculo v
	 */
	public double[] ψv;

	/**
	 * Custo fixo do veículo por viagem.
	 */
	public double[] δv;


	/**
	 *Custo de entrega
	 * no cliente j no periodo t.
	 */
	public double[][] ϕj_t;

	/**
	 *Custo de armazenagem por unidade do produto
	 * no cliente j no periodo t.
	 */
	public double[][] ηj_t;


	/**
	 * Capacidade de estoque do cliente i
	 */
	public double[] V_i;

	/**
	 * Demanda do cliente j
	 * no periodo t.
	 */
	public double[][] d_jt;

	/**
	 * Média/ Esperança do cliente j
	 */
	public double[] Dj;

	/**
	 * Inicio da janela de atendimento
	 */
	public int[][] ws_jt;

	/**
	 * Fim da janela de atendimento
	 */
	public int[][] we_jt;


	/**
	 * Time Delivery
	 */
	public int[][] td_jt;

	public int timeInit;


	/**
	 * Desvio Padrão do cliente j
	 */
	public double[] sigma_j;

	/**
	 *Tamanho da frota de veículo (numero de veículos disponiveis)
	 */
	public int V;	

	/**
	 *Velocidade Média do veículo v
	 */
	public int ν_v;	

	/**
	 * Custo de Viagem por unidade de produto do cliente i para o j
	 */
	public double[][] c_ij;

	/**
	 * Tempo de viagem de i para o j
	 */
	public double[][] θ_ij;

	/**
	 * Custo de Viagem vazia do cliente i até a base
	 */
	//	public double[] b_c_i0;

	/**
	 * Transferencia planejada para o armazem no período t
	 */
	public double[] Rrt;

	/**
	 * coordenadas X do cliente i
	 */
	public double [] iPosX;
	/**
	 *  coordenadas Y do cliente i
	 */
	public double [] iPosY;

	/**
	 *  Multiplicador LagrangianoS
	 */
	public double [][] μjt;

	public double zAlfa;

	public double [] I_j0;

	/**
	 * tempo máximo de tempo em uma rota (min)
	 */
	public int  Rv;
	/**
	 * @param S - Numero de Clientes.
	 * @param H - Tempo de Horizonte.
	 * @param kvRange - Intervalo de Capacidade do Veículo.
	 * @param ψvRange - Intervalo de Custo fixo de operação do Veículo.
	 * @param δv_tRange - Intervalo de Custo fixo do veículo por viagem no periodo t.
	 * @param η_itRange - Intervalo de Custo de armazenagem por unidade do produto no cliente i no periodo t.
	 * @param ϕj_tRange - Intervalo de Custo de Entrega por unidade do produto no cliente j no periodo t.
	 * @param I_i0Range - Intervalo de Nivel de estoque inicial do Cliente i
	 * @param V_iRange - Intervalo de Capacidade de estoque do cliente i.
	 * @param d_jtRange - Intervalo de Demanda do cliente i no periodo t.
	 * @param tamXSquare - Tamanho Geografico X
	 * @param tamYSquare - Tamanho Geográfico Y
	 * @throws IndexOutOfBoundsException - Intervalo fora do padrão
	 */
	public Parametros(int S,int H,int τt,
			int[] kvRange,double[] ψvRange,int v_v, int[]δv_tRange,double[] η_itRange,double[] ϕj_tRange,int[]V_iRange,double[]d_jtRange,
			double[] RrtRange,int tamXSquare,int tamYSquare,double[]Ij0_Range,Random rand) throws IndexOutOfBoundsException{

		if(kvRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'kvRange' com tamanho incorreto: Deve conter [min,max]");
		if(δv_tRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'δv_tRange' com tamanho incorreto: Deve conter [min,max]");
		if(η_itRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'η_itRange' com tamanho incorreto: Deve conter [min,max]");

		if(V_iRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'V_iRange' com tamanho incorreto: Deve conter [min,max]");
		if(d_jtRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'd_jtRange' com tamanho incorreto: Deve conter [min,max]");
		if(RrtRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'RrtRange' com tamanho incorreto: Deve conter [min,max]");
		if(Ij0_Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'Ij0_Range' com tamanho incorreto: Deve conter [min,max]");

		this.S = S;
		this.H = H;// Para contar no array o tempo 0

		this.V =  this.S;
		this.τt = τt;
		this.ν_v = v_v;
		this.Rv = 8*60;
		this.kv = (kvRange[1]-kvRange[0] <= 0)?kvRange[0]:kvRange[0]+rand.nextInt(kvRange[1]-kvRange[0]);

		//double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble()
		this.Rrt = new double[H];
		for(int t = 0; t < H; t++){
			this.Rrt[t] =RrtRange[0] + (RrtRange[1] - RrtRange[0]) * rand.nextDouble();

			//		this.δv.put(String.valueOf(t) ,δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble());
		}

		this.I_j0 = new double[S]; 
		for(int i = 1;i < S; i++){			
			this.I_j0[i]=Ij0_Range[0] + (Ij0_Range[1] - Ij0_Range[0]) * rand.nextDouble();
		}

		/*
		 * the holding cost  per unit per period (in euros per ton per period)
		 * between 0.1 and 0.15
		 */
		this.ηj_t = new double[S][H];
		for(int i = 0;i < S; i++){
			for(int t = 0; t < H; t++){
				this.ηj_t[i][t]=η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble();
				//this.ηj_t.put(String.valueOf(i)+","+String.valueOf(t) ,η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble());
			}
		}

		/*
		 * 		the fixed handling cost (in euros) per delivery in period
		 * which are 25 euros per delivery
		 */
		this.ϕj_t = new double[S][H];
		for(int i = 0;i < S; i++){
			for(int t = 0; t < H; t++){
				this.ϕj_t[i][t] = ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble();
				//this.ϕj_t.put(String.valueOf(i)+","+String.valueOf(t) ,ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble());
			}
		}

		/*
		 * 		Travel Cost(in euros per km);
		 * travel cost of a vehicle is 1 euro per km per hour.
		 */
		this.δv = new double[V];
		for(int v = 0; v < V; v++){
			this.δv[v] =δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble();

			//		this.δv.put(String.valueOf(t) ,δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble());
		}

		/*
		 * 		The fixed operating cost (in euros per vehicle);
		 * 		for each vehicle is 50 euros
		 */
		this.ψv = new double[V]; 	
		for(int v = 0;v < this.V; v++){
			this.ψv[v] = ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble();
			//this.ψv.put(String.valueOf(v),ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble());
		}


		/*this.V_i = new double[S];
		for(int i = 1;i < S; i++){
			this.V_i[i] = V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble();
			//	this.V_i.put(String.valueOf(i),V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble());
		}*/
		this.d_jt =new double[S][H];
		this.Dj =new double[S];
		this.sigma_j = new double[S];
		for(int j = 1;j < S; j++){
			for(int t = 1; t < H; t++){
				this.d_jt[j][t] = d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble();
				//this.d_jt.put(String.valueOf(i)+","+String.valueOf(t) ,d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble());
			}
		}
		for(int j = 1;j < S; j++){
			double media = 0;
			for(int t = 0; t < H; t++){
				media += this.d_jt[j][t];
			}
			this.Dj[j] = media/this.d_jt[j].length;
			// Média - Esperança


			//Variancia

			/* double mean = this.Dj[j];
		        double temp = 0;
		        for(double a :this.d_jt[j])
		            temp += (mean-a)*(mean-a);
		     Double Variancia =  temp/this.d_jt[j].length;*/

			double sumDesvio = 0;
			for(int t = 1;t < H; t++){
				sumDesvio += Math.pow((this.d_jt[j][t] - this.Dj[j]),2);
			}

			//Desvio Padrão
			this.sigma_j[j] = Math.sqrt((double)((double)1/(this.H-1))*sumDesvio);// Desvio Padrão
			//			this.sigma_j[j] = Math.sqrt(Variancia);// Desvio Padrão
		}



		this.iPosX = new double[S];
		this.iPosY = new double[S];
		for(int i = 1;i < S; i++){
			iPosX[i] = 0 + (tamXSquare - 0) * rand.nextDouble();
			iPosY[i] = 0 + (tamYSquare - 0) * rand.nextDouble();
		}
		iPosX[0] = tamXSquare/2;
		iPosY[0] = tamYSquare/2;

		//	this.b_c_i0 = new double[S];
		this.c_ij = new double[S][S];
		this.θ_ij = new double[S][S];
		for(int i = 0;i < S; i++){
			for(int j = 0;j < S;j++){
				if(j == 0 && i > 0){
					//					this.b_c_i0[i] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
					//					this.b_c_i0.put(String.valueOf(i), f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]));
				}
				this.c_ij[i][j] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
				this.θ_ij[i][j] = this.c_ij[i][j]/this.ν_v;
				//				θ_ij em Horas!
			}
		}

		// ?????????????????????????????????????????
		int maxTaur_ij = 1;
		for(int tau = 1;tau < H; tau++){
			int sumT = 0;

			for(int t = 1; t < tau; t++){
				int sumN = 0;
				for(int i = 1;i < S; i++){
					sumN += this.d_jt[i][t];
					//sumN += this.d_jt.get(i+","+t);
				}
				sumT+=sumN/(tau*this.kv);
			}

			if(sumT > maxTaur_ij)maxTaur_ij = sumT;
		}

		//		this.V =  maxTaur_ij;
		// ?????????????????????????????????????????



		this.ν_v = v_v;
	}



	public Parametros(int S,int H,int τt,
			int[] kvRange,double[] ψvRange,int v_v, int[]δv_tRange,double[] η_itRange,double[] ϕj_tRange,int[]V_iRange,double[]d_jtRange,
			double[] RrtRange,int tamXSquare,int tamYSquare,double[]Ij0_Range,Random rand,int[]wab_Range
			,double[][] mDist,double[] lat,double[] lng, double[] demanda) throws IndexOutOfBoundsException{


		if(kvRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'kvRange' com tamanho incorreto: Deve conter [min,max]");
		if(δv_tRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'δv_tRange' com tamanho incorreto: Deve conter [min,max]");
		if(η_itRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'η_itRange' com tamanho incorreto: Deve conter [min,max]");

		if(V_iRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'V_iRange' com tamanho incorreto: Deve conter [min,max]");
		if(d_jtRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'd_jtRange' com tamanho incorreto: Deve conter [min,max]");
		if(RrtRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'RrtRange' com tamanho incorreto: Deve conter [min,max]");
		if(Ij0_Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'Ij0_Range' com tamanho incorreto: Deve conter [min,max]");
		if(wab_Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'wab_Range' com tamanho incorreto: Deve conter [min,max]");
		
		if(demanda != null){
			S = 0;
			for (double d : demanda) {
				if(d != 0){
					S++;
				}
			}
		}
		
		this.S = S;
		this.H = H;// Para contar no array o tempo 0

		this.V =  this.S;
		this.τt = τt;
		this.ν_v = v_v;
		this.Rv = 8*60;
		this.kv = (kvRange[1]-kvRange[0] <= 0)?kvRange[0]:kvRange[0]+rand.nextInt(kvRange[1]-kvRange[0]);

		//double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble()
		this.Rrt = new double[H];
		for(int t = 0; t < H; t++){
			this.Rrt[t] =RrtRange[0] + (RrtRange[1] - RrtRange[0]) * rand.nextDouble();

			//		this.δv.put(String.valueOf(t) ,δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble());
		}

		this.I_j0 = new double[S]; 
		for(int i = 1;i < S; i++){			
			this.I_j0[i]=Ij0_Range[0] + (Ij0_Range[1] - Ij0_Range[0]) * rand.nextDouble();
		}

		/*
		 * the holding cost  per unit per period (in euros per ton per period)
		 * between 0.1 and 0.15
		 */
		this.ηj_t = new double[S][H];
		for(int i = 0;i < S; i++){
			for(int t = 0; t < H; t++){
				this.ηj_t[i][t]=η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble();
				//this.ηj_t.put(String.valueOf(i)+","+String.valueOf(t) ,η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble());
			}
		}

		/*
		 * 		the fixed handling cost (in euros) per delivery in period
		 * which are 25 euros per delivery
		 */
		this.ϕj_t = new double[S][H];
		for(int i = 0;i < S; i++){
			for(int t = 0; t < H; t++){
				this.ϕj_t[i][t] = ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble();
				//this.ϕj_t.put(String.valueOf(i)+","+String.valueOf(t) ,ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble());
			}
		}

		/*
		 * 		Travel Cost(in euros per km);
		 * travel cost of a vehicle is 1 euro per km per hour.
		 */
		this.δv = new double[V];
		for(int v = 0; v < V; v++){
			this.δv[v] =δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble();

			//		this.δv.put(String.valueOf(t) ,δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble());
		}


		this.DR = 5; //5 Toneladas/ min
		this.timeInit = 6*60;
		this.ws_jt = new int[S][H];
		this.we_jt = new int[S][H];
		this.td_jt = new int[S][H];
		double taxW = 1;// Quantos clientes tem janela
//				double taxW = 0.5;// Quantos clientes tem janela
//		double taxW = 0;// Quantos clientes tem janela
		int inicJan = 480;// Inicio do horário de Janela possíveis - 8h
		int fimJan = 1080;// fim do horário de Janela possíveis - 18h
		for(int i = 0;i < S; i++){
			for(int t = 0; t < H; t++){
				if(rand.nextDouble() <= taxW){

					int tamanhoJanela = (int)(wab_Range[0] + (wab_Range[1] - wab_Range[0]) * rand.nextDouble());					
					this.ws_jt[i][t] = (int)(inicJan + ((fimJan-tamanhoJanela) - inicJan) * rand.nextDouble());
					this.we_jt[i][t] = this.ws_jt[i][t] + tamanhoJanela;
				}
				else{				
					this.ws_jt[i][t] = 0;
					this.we_jt[i][t] = 1440;
				}
				this.td_jt[i][t] = (int)(10 + (120 - 10) * rand.nextDouble());

			}
		}

		/*
		 * 		The fixed operating cost (in euros per vehicle);
		 * 		for each vehicle is 50 euros
		 */
		this.ψv = new double[V]; 	
		for(int v = 0;v < this.V; v++){
			this.ψv[v] = ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble();
			//this.ψv.put(String.valueOf(v),ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble());
		}


		/*this.V_i = new double[S];
		for(int i = 1;i < S; i++){
			this.V_i[i] = V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble();
			//	this.V_i.put(String.valueOf(i),V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble());
		}*/
		this.d_jt =new double[S][H];
		this.Dj =new double[S];
		this.sigma_j = new double[S];
		for(int j = 1;j < S; j++){
			for(int t = 1; t < H; t++){
				this.d_jt[j][t] = d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble();
				//this.d_jt.put(String.valueOf(i)+","+String.valueOf(t) ,d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble());
			}
		}
		for(int j = 1;j < S; j++){

			if(demanda != null){
				this.Dj = demanda;
			}
			else{
				double media = 0;
				for(int t = 0; t < H; t++){
					media += this.d_jt[j][t];
				}
				this.Dj[j] = media/this.d_jt[j].length;
				// Média - Esperança
			}

			//Variancia

			/* double mean = this.Dj[j];
		        double temp = 0;
		        for(double a :this.d_jt[j])
		            temp += (mean-a)*(mean-a);
		     Double Variancia =  temp/this.d_jt[j].length;*/

			double sumDesvio = 0;
			for(int t = 1;t < H; t++){
				sumDesvio += Math.pow((this.d_jt[j][t] - this.Dj[j]),2);
			}

			//Desvio Padrão
			this.sigma_j[j] = Math.sqrt((double)((double)1/(this.H-1))*sumDesvio);// Desvio Padrão
			//			this.sigma_j[j] = Math.sqrt(Variancia);// Desvio Padrão
		}



		this.iPosX = new double[S];
		this.iPosY = new double[S];
		if(lat == null && lng == null){
			for(int i = 1;i < S; i++){
				iPosX[i] = 0 + (tamXSquare - 0) * rand.nextDouble();
				iPosY[i] = 0 + (tamYSquare - 0) * rand.nextDouble();
			}
			iPosX[0] = tamXSquare/2;
			iPosY[0] = tamYSquare/2;
		}
		else{
			iPosX = lng;
			iPosY = lat;
		}

		//	this.b_c_i0 = new double[S];
		this.c_ij = new double[S][S];
		this.θ_ij = new double[S][S];
		if(mDist != null){
			c_ij = mDist;
		}
		for(int i = 0;i < S; i++){
			for(int j = 0;j < S;j++){
				if(j == 0 && i > 0){
					//					this.b_c_i0[i] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
					//					this.b_c_i0.put(String.valueOf(i), f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]));
				}
				if(mDist == null){
					this.c_ij[i][j] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
				}
				this.θ_ij[i][j] = this.c_ij[i][j]/this.ν_v;
				//				θ_ij em Horas!
			}
		}

		// ?????????????????????????????????????????
		int maxTaur_ij = 1;
		for(int tau = 1;tau < H; tau++){
			int sumT = 0;

			for(int t = 1; t < tau; t++){
				int sumN = 0;
				for(int i = 1;i < S; i++){
					sumN += this.d_jt[i][t];
					//sumN += this.d_jt.get(i+","+t);
				}
				sumT+=sumN/(tau*this.kv);
			}

			if(sumT > maxTaur_ij)maxTaur_ij = sumT;
		}

		//		this.V =  maxTaur_ij;
		// ?????????????????????????????????????????



		this.ν_v = v_v;
	}









	/**
	 * @return the dj
	 */
	public double[] getDj() {
		return Dj;
	}
	/**
	 * @param dj the dj to set
	 */
	public void setDj(double[] dj) {
		Dj = dj;
	}
	/**
	 * @return the sigma_j
	 */
	public double[] getSigma_j() {
		return sigma_j;
	}
	/**
	 * @param sigma_j the sigma_j to set
	 */
	public void setSigma_j(double[] sigma_j) {
		this.sigma_j = sigma_j;
	}

	public void inicializaPadrao(){
		H = 2;
		S = 3;
		S++;
		H++;
		V = S-1;
		this.Rrt = new double[H];
		this.ηj_t = new double[S][H];
		this.ϕj_t = new double[S][H];
		this.V_i = new double[S];
		this.d_jt =new double[S][H];
		this.Dj =new double[S];
		this.sigma_j = new double[S];
		this.iPosX = new double[S];
		this.iPosY = new double[S];
		//		this.b_c_i0 = new double[S];
		this.c_ij = new double[S][S];
		this.θ_ij = new double[S][S];
		this.δv = new double[V];
		this.ψv = new double[V]; 	
		μjt = new double[S][H];


		zAlfa = 1;
		τt = 8;		
		kv = 30;
		/*I_j0 = new double[S];
		for(int j = 0; j< S;j++)I_j0[j] = 10;
		I_j0[0] = 100;*/

		for(int v = 0; v< V;v++) ψv[v] = 50D;
		for(int v = 0; v< V;v++) δv[v] = 1D;
		for(int j = 0; j< S;j++){
			for(int t = 0; t< H;t++)
				ϕj_t[j][t] = 25D;
		}
		for(int j = 0; j< S;j++){
			for(int t = 0; t< H;t++)
				ηj_t[j][t] = 0.1D;
		}
		for(int i = 0; i< S;i++){
			V_i[i] = Double.MAX_VALUE;
		}
		for(int j = 0; j< S;j++){
			for(int t = 0; t< H;t++){
				d_jt[j][t] = 2.5D;

				//d_jt[j][t] = 1.25D;
				//d_jt[j][t] = 0.625D;
			}
		}
		for(int j = 0; j< S;j++){
			double sum = 0;
			double average;

			for(int i=0; i < d_jt[j].length; i++){
				sum = sum + d_jt[j][i];
			}
			Dj[j] = (double)sum/d_jt[j].length;	

			double sumDesvio = 0;
			for(int t = 0;t < H; t++){
				sumDesvio += Math.pow((this.d_jt[j][t] - this.Dj[j]),2);
			}
			this.sigma_j[j] = Math.sqrt((1/(this.H-1))*sumDesvio);// Desvio Padrão
		}	

		ν_v = 15;	

		iPosX[0] = 5;
		iPosY[0] = 5;

		iPosX[1] = 3;
		iPosY[1] = 3;

		iPosX[2] = 2;
		iPosY[2] = 5;

		iPosX[3] = 9;
		iPosY[3] = 6;


		for(int i = 0;i < S; i++){
			for(int j = 0;j < S;j++){
				if(j == 0 && i > 0){
					//					this.b_c_i0[i] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
					//					this.b_c_i0.put(String.valueOf(i), f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]));
				}
				this.c_ij[i][j] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
				this.θ_ij[i][j] = this.c_ij[i][j]/this.ν_v;
				//				θ_ij em Horas!
			}
		}

		for(int t = 0;t < H; t++){
			Rrt[t] = 30;
		}
		for(int j = 0;j < S;j++){
			for(int t = 0;t < H; t++){
				μjt[j][t] = 0.5;
			}
		}
	}
}
