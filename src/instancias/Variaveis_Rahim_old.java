package instancias;

import java.util.HashMap;
import java.util.Random;

public class Variaveis_Rahim_old {

	/**
	 * Nivel de estoque inicial do Cliente j
	 */
	private HashMap<String,Double> I_j0 = new HashMap<String,Double>();
	
	/**
	 * Estoque do Cliente j no periodo t
	 */
	private HashMap<String,Double> I_jt = new HashMap<String,Double>();
	
	/**
	 * Quantidade restante no ve�culo v quando ele viaja do ve�culo i para o j no tempo t
	 */
	private HashMap<String,Double> Qv_ijt = new HashMap<String,Double>();
	
	/**
	 * Quantidade entregue no local j no periodo t
	 * retorna 0 caso n�o tenha entrega.
	 */
	private HashMap<String,Double> qjt = new HashMap<String,Double>();

	/**
	 * Variavel Binaria,
	 * 1 se o local j � visitado imediatamente ap�s o local i no periodo t.
	 * 0 caso contr�rio.
	 */
	private HashMap<String,Integer> Xv_ijt = new HashMap<String,Integer>();
	
	/**
	 * Variavel Binaria,
	 * 1 se o ve�culo v est� sendo usado no periodo t.
	 * 0 caso contr�rio.
	 */
	private HashMap<String,Integer> Yv_t = new HashMap<String,Integer>();

	
	
	
	
	public Variaveis_Rahim_old(Instancia_Rahim i,double [] I_j0Range){
		
		if(I_j0Range.length != 2)throw new IndexOutOfBoundsException("Intervalo 'I_j0Range' com tamanho incorreto: Deve conter [min,max]");
		
		Random rand = new Random(1);
		
		this.I_j0 = new HashMap<>();
		for(int j = 1;j <= i.getS(); j++){
			this.I_j0.put(String.valueOf(j)+"0",I_j0Range[0] + (I_j0Range[1] - I_j0Range[0]) * rand.nextDouble());
		}

	}
}
