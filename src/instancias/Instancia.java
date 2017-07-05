package instancias;

import java.util.HashMap;
import java.util.Random;
import util.*;

import javax.sql.rowset.spi.TransactionalWriter;

/**
 * @author Krespo
 *
 */
public class Instancia {

	Funcoes f = new Funcoes();

	/**
	 * Numero de Clientes.
	 */
	public int S;
	/**
	 * Tempo de Horizonte.
	 */
	public int H;

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
	public double[] b_c_i0;

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
	 * @param S - Numero de Clientes.
	 * @param H - Tempo de Horizonte.
	 * @param CRange - Intervalo de Capacidade do Ve�culo.
	 * @param f_tRange - Intervalo de Custo fixo do ve�culo por viagem no periodo t.
	 * @param h_itRange - Intervalo de Custo de armazenagem por unidade do produto no cliente i no periodo t.
	 * @param I_j0Range - Intervalo de Nivel de estoque inicial do Cliente i
	 * @param V_iRange - Intervalo de Capacidade de estoque do cliente i.
	 * @param r_itRange - Intervalo de Demanda do cliente i no periodo t.
	 * @param tamXSquare - Tamanho Geografico X
	 * @param tamYSquare - Tamanho Geogr�fico Y
	 * @throws IndexOutOfBoundsException - Intervalo fora do padr�o
	 */
	public Instancia(int S,int H,
			int[] CRange, int[]f_tRange,double[] h_itRange,int[]I_j0Range,int[]V_iRange,int[]r_itRange,
			int tamXSquare,int tamYSquare) throws IndexOutOfBoundsException{

		if(CRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'CRange' com tamanho incorreto: Deve conter [min,max]");
		if(f_tRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'f_tRange' com tamanho incorreto: Deve conter [min,max]");
		if(h_itRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'h_itRange' com tamanho incorreto: Deve conter [min,max]");
		if(I_j0Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'I_i0Range' com tamanho incorreto: Deve conter [min,max]");
		if(V_iRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'V_iRange' com tamanho incorreto: Deve conter [min,max]");
		if(r_itRange.length != 2)throw new IndexOutOfBoundsException("Intervalo 'r_itRange' com tamanho incorreto: Deve conter [min,max]");

		this.S = S;
		this.H = H;

		Random rand = new Random(1);

		this.kv = CRange[0]+rand.nextInt(CRange[1]-CRange[0]);

		//double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble()
		this.δv = new double[V];
		for(int v = 0; v < V; v++){
			this.δv[v] = f_tRange[0]+rand.nextInt(f_tRange[1]-f_tRange[0]);
		}

		this.ηj_t = new double [S][H];
		for(int i = 1;i <= S; i++){
			for(int t = 1; t <= H; t++){
				//				this.ηj_t.put(String.valueOf(i)+","+String.valueOf(t) ,h_itRange[0] + (h_itRange[1] - h_itRange[0]) * rand.nextDouble());
				this.ηj_t[i][t] = h_itRange[0] + (h_itRange[1] - h_itRange[0]) * rand.nextDouble();
			}
		}

		this.I_j0 = new double[S];
		for(int i = 1;i <= S; i++){
			this.I_j0[i] = I_j0Range[0]+rand.nextInt(I_j0Range[1]-I_j0Range[0]);
		}

		this.V_i = new double [S];
		for(int i = 1;i <= S; i++){
			this.V_i[i] =  V_iRange[0]+rand.nextInt(V_iRange[1]-V_iRange[0]);
		}

		this.d_jt = new double[S][H];
		for(int j = 1;j <= S; j++){
			for(int t = 1; t <= H; t++){
				this.d_jt[j][t] = r_itRange[0]+rand.nextInt(r_itRange[1]-r_itRange[0]);
			}
		}

		this.iPosX = new double[S];
		this.iPosY = new double[S];
		for(int i = 0;i < S; i++){
			iPosX[i] = 0 + (tamXSquare - 0) * rand.nextDouble();
			iPosY[i] = 0 + (tamYSquare - 0) * rand.nextDouble();
		}

		for(int i = 0;i < S; i++){
			for(int j = 0;j < S;j++){				
				this.c_ij[i][j] = f.dCalculaDist(this.iPosX[i],this.iPosY[i], this.iPosX[j], this.iPosY[j]);
				this.θ_ij[i][j] = this.c_ij[i][j]/this.ν_v;
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
	}
}
