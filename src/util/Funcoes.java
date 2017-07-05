package util;

public class Funcoes {

	/**
	 * @param origem_x
	 * @param origem_y
	 * @param dest_x
	 * @param dest_y
	 * @return Retorna distancia entre origem e destino
	 */
	public double dCalculaDist(double origem_x,double origem_y, double dest_x, double dest_y){
		double ret = 0;
		
		double d = (Math.pow((origem_x - dest_x),2)+(Math.pow((origem_y - dest_y),2)));
		ret = Math.sqrt(d);
		//Resposta em KM
		return ret;
	}
	
}
