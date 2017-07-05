package Principal;
public class Random {
	public static double gerarAleatorio() { //Assumindo funcao normal de desvio padrao=1 e media=0
															  //Funcao CDF
		double[] array = new double[20];
		for(int i=0; i<array.length; i++) {
			array[i] = Math.random();
		}
		double position = Math.random() * array.length;
		double resposta = array[(int)position];
		return resposta;
	}
}
