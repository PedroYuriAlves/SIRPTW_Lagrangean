package instancias;

import java.util.HashMap;
import java.util.Random;
import util.*;

import javax.sql.rowset.spi.TransactionalWriter;

/**
 * @author Krespo
 *
 */
public class Instancia_Rahim_new {

	Funcoes f = new Funcoes();

	/**
	 * Numero de Clientes.
	 */
	private int S;
	/**
	 * Tempo de Horizonte.
	 */
	private int T;
	
	/**
	 * Unidades de tempo no periodo t
	 */
	private int τt;
	
	/**
	 * Capacidade do Veículo.
	 */
	private int kv;

	
	/**
	 * Custo Fixo de Operação do veiculo v
	 */
	private double[][][][] ψv;
	
	/**
	 * Custo fixo do veículo por viagem.
	 */
	private double[][][][] δv;
		
	
	/**
	 *Custo de entrega
	 * no cliente j no periodo t.
	 */
	private double[][][][] ϕj_t;
	
	/**
	 *Custo de armazenagem por unidade do produto
	 * no cliente j no periodo t.
	 */
	private double[][][][] ηj_t;

	
	/**
	 * Capacidade de estoque do cliente i
	 */
	private double[][][][] V_i;

	/**
	 * Demanda do cliente j
	 * no periodo t.
	 */
	private double[][][][] d_jt;

	/**
	 *Tamanho da frota de veículo (numero de veículos disponiveis)
	 */
	private int V;	
	
	/**
	 *Velocidade Média do veículo v
	 */
	private int ν_v;	

	/**
	 * Custo de Viagem por unidade de produto do cliente i para o j
	 */
	private double[][][][] c_ij;
	
	/**
	 * Tempo de viagem de i para o j
	 */
	private double[][][][]  θ_ij;

	/**
	 * Custo de Viagem vazia do cliente i até a base
	 */
	private double[][][][] b_c_i0;

	/**
	 * Transferencia planejada para o armazem no período t
	 */
	private double[][][][] Rrt;

	/**
	 * @return the f
	 */
	public Funcoes getF() {
		return f;
	}
	/**
	 * @param f the f to set
	 */
	public void setF(Funcoes f) {
		this.f = f;
	}
	/**
	 * @return the s
	 */
	public int getS() {
		return S;
	}
	/**
	 * @param s the s to set
	 */
	public void setS(int s) {
		S = s;
	}
	/**
	 * @return the t
	 */
	public int getT() {
		return T;
	}
	/**
	 * @param t the t to set
	 */
	public void setT(int t) {
		T = t;
	}
	/**
	 * @return the τt
	 */
	public int getΤt() {
		return τt;
	}
	/**
	 * @param τt the τt to set
	 */
	public void setΤt(int τt) {
		this.τt = τt;
	}
	/**
	 * @return the kv
	 */
	public int getKv() {
		return kv;
	}
	/**
	 * @param kv the kv to set
	 */
	public void setKv(int kv) {
		this.kv = kv;
	}
	/**
	 * @return the ψv
	 */
	public double[][][][] getΨv() {
		return ψv;
	}
	/**
	 * @param ψv the ψv to set
	 */
	public void setΨv(double[][][][] ψv) {
		this.ψv = ψv;
	}
	/**
	 * @return the δv
	 */
	public double[][][][] getΔv() {
		return δv;
	}
	/**
	 * @param δv the δv to set
	 */
	public void setΔv(double[][][][] δv) {
		this.δv = δv;
	}
	/**
	 * @return the ϕj_t
	 */
	public double[][][][] getΦj_t() {
		return ϕj_t;
	}
	/**
	 * @param φj_t the ϕj_t to set
	 */
	public void setΦj_t(double[][][][] φj_t) {
		ϕj_t = φj_t;
	}
	/**
	 * @return the ηj_t
	 */
	public double[][][][] getΗj_t() {
		return ηj_t;
	}
	/**
	 * @param ηj_t the ηj_t to set
	 */
	public void setΗj_t(double[][][][] ηj_t) {
		this.ηj_t = ηj_t;
	}
	/**
	 * @return the v_i
	 */
	public double[][][][] getV_i() {
		return V_i;
	}
	/**
	 * @param v_i the v_i to set
	 */
	public void setV_i(double[][][][] v_i) {
		V_i = v_i;
	}
	/**
	 * @return the d_jt
	 */
	public double[][][][] getD_jt() {
		return d_jt;
	}
	/**
	 * @param d_jt the d_jt to set
	 */
	public void setD_jt(double[][][][] d_jt) {
		this.d_jt = d_jt;
	}
	/**
	 * @return the v
	 */
	public int getV() {
		return V;
	}
	/**
	 * @param v the v to set
	 */
	public void setV(int v) {
		V = v;
	}
	/**
	 * @return the ν_v
	 */
	public int getΝ_v() {
		return ν_v;
	}
	/**
	 * @param ν_v the ν_v to set
	 */
	public void setΝ_v(int ν_v) {
		this.ν_v = ν_v;
	}
	/**
	 * @return the c_ij
	 */
	public double[][][][] getC_ij() {
		return c_ij;
	}
	/**
	 * @param c_ij the c_ij to set
	 */
	public void setC_ij(double[][][][] c_ij) {
		this.c_ij = c_ij;
	}
	/**
	 * @return the θ_ij
	 */
	public double[][][][] getΘ_ij() {
		return θ_ij;
	}
	/**
	 * @param θ_ij the θ_ij to set
	 */
	public void setΘ_ij(double[][][][] θ_ij) {
		this.θ_ij = θ_ij;
	}
	/**
	 * @return the b_c_i0
	 */
	public double[][][][] getB_c_i0() {
		return b_c_i0;
	}
	/**
	 * @param b_c_i0 the b_c_i0 to set
	 */
	public void setB_c_i0(double[][][][] b_c_i0) {
		this.b_c_i0 = b_c_i0;
	}
	/**
	 * @return the rrt
	 */
	public double[][][][] getRrt() {
		return Rrt;
	}
	/**
	 * @param rrt the rrt to set
	 */
	public void setRrt(double[][][][] rrt) {
		Rrt = rrt;
	}
	/**
	 * @return the iPosX
	 */
	public double[] getiPosX() {
		return iPosX;
	}
	/**
	 * @param iPosX the iPosX to set
	 */
	public void setiPosX(double[] iPosX) {
		this.iPosX = iPosX;
	}
	/**
	 * @return the iPosY
	 */
	public double[] getiPosY() {
		return iPosY;
	}
	/**
	 * @param iPosY the iPosY to set
	 */
	public void setiPosY(double[] iPosY) {
		this.iPosY = iPosY;
	}

	/**
	 * coordenadas X do cliente i
	 */
	private double [] iPosX;
	/**
	 *  coordenadas Y do cliente i
	 */
	private double[] iPosY;


	public void setCDPosY(double CDPosY) {
		this.iPosY[0] = CDPosY;
	}
	public void setCDPosX(double CDPosX) {
		this.iPosX[0] = CDPosX;
	}

	
	// vijt  

	/**
	 * @param S - Numero de Clientes.
	 * @param T - Tempo de Horizonte.
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
	public Instancia_Rahim_new(int S,int T,int τt,
			int[] kvRange,double[] ψvRange,int v_v, int[]δv_tRange,double[] η_itRange,double[] ϕj_tRange,int[]V_iRange,double[]d_jtRange,
			int tamXSquare,int tamYSquare) throws IndexOutOfBoundsException{

		if(kvRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'kvRange' com tamanho incorreto: Deve conter [min,max]");
		if(δv_tRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'δv_tRange' com tamanho incorreto: Deve conter [min,max]");
		if(η_itRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'η_itRange' com tamanho incorreto: Deve conter [min,max]");
		
		if(V_iRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'V_iRange' com tamanho incorreto: Deve conter [min,max]");
		if(d_jtRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'd_jtRange' com tamanho incorreto: Deve conter [min,max]");

		this.S = S;
		this.T = T;
		this.τt = τt;

		Random rand = new Random();

		//this.kv = kvRange[0]+rand.nextInt(kvRange[1]-kvRange[0]);
		this.kv = (int)(kvRange[0] + (kvRange[1] - kvRange[0]) * rand.nextDouble());

		//double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble()
		this.δv = new double[1][1][1][T+1];
		for(int t = 1; t <= T; t++){
			this.δv[0][0][0][t] =δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble();
			
//		this.δv.put(String.valueOf(t) ,δv_tRange[0] + (δv_tRange[1] - δv_tRange[0]) * rand.nextDouble());
		}

		this.ηj_t = new double[1][S+1][1][T+1];
		for(int i = 1;i <= S; i++){
			for(int t = 1; t <= T; t++){
				this.ηj_t[0][i][0][t]=η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble();
				//this.ηj_t.put(String.valueOf(i)+","+String.valueOf(t) ,η_itRange[0] + (η_itRange[1] - η_itRange[0]) * rand.nextDouble());
			}
		}
		
		this.ϕj_t = new double[1][1][S+1][T+1];
		for(int j = 1;j <= S; j++){
			for(int t = 1; t <= T; t++){
				this.ϕj_t[0][0][j][t] = ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble();
				//this.ϕj_t.put(String.valueOf(i)+","+String.valueOf(t) ,ϕj_tRange[0] + (ϕj_tRange[1] - ϕj_tRange[0]) * rand.nextDouble());
			}
		}

		
		this.V_i = new double[1][S+1][1][1];
		for(int i = 1;i <= S; i++){
			this.V_i[0][i][0][0] = V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble();
		//	this.V_i.put(String.valueOf(i),V_iRange[0] + (V_iRange[1] - V_iRange[0]) * rand.nextDouble());
		}
		
		

		
		this.d_jt =new double[1][1][S+1][T+1];
		for(int j = 1;j <= S; j++){
			for(int t = 1; t <= T; t++){
				this.d_jt[0][0][j][t] = d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble();
				//this.d_jt.put(String.valueOf(i)+","+String.valueOf(t) ,d_jtRange[0] + (d_jtRange[1] - d_jtRange[0]) * rand.nextDouble());
			}
		}

		this.iPosX = new double[S+1];
		this.iPosY = new double[S+1];
		for(int i = 0;i < S+1; i++){
			iPosX[i] = 0 + (tamXSquare - 0) * rand.nextDouble();
			iPosY[i] = 0 + (tamYSquare - 0) * rand.nextDouble();
		}
		
		this.b_c_i0 = new double[1][S+2][1][1];
		this.c_ij = new double[1][S+2][S+2][1];
		this.θ_ij = new double[1][S+2][S+2][1];
		for(int i = 0;i < S+1; i++){
			for(int j = 0;j < S+1;j++){
				if(j == 0 && i > 0){
					this.b_c_i0[0][i][0][0] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
//					this.b_c_i0.put(String.valueOf(i), f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]));
				}
				this.c_ij[0][i][j][0] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
				this.θ_ij[0][i][j][0] = this.c_ij[0][i][j][0]/this.ν_v;
			}
		}

		// ?????????????????????????????????????????
		int maxTaur_ij = 0;
		for(int tau = 1;tau <= T; tau++){
			int sumT = 0;
			
			for(int t = 1; t <= tau; t++){
				int sumN = 0;
				for(int j = 1;j <= S; j++){
					sumN += this.d_jt[0][0][j][t];
					//sumN += this.d_jt.get(i+","+t);
				}
				sumT+=sumN/(tau*this.kv);
			}

			if(sumT > maxTaur_ij)maxTaur_ij = sumT;
		}

		this.V = maxTaur_ij;
		// ?????????????????????????????????????????
		
		
		this.ψv = new double[V+1][1][1][1]; 	
		for(int v = 1;v <= this.V; v++){
			this.ψv[v][0][0][0] = ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble();
			//this.ψv.put(String.valueOf(v),ψvRange[0] + (ψvRange[1] - ψvRange[0]) * rand.nextDouble());
		}
		
		this.ν_v = v_v;
	}
}
