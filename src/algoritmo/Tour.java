package algoritmo;

import java.util.ArrayList;
import java.util.List;

public class Tour {
	public List<Integer> retails;
	public double CV = 0;
	public double travelTime;
	public double totalcap;
	public double [] ha;
	public double [] hd;
	public List<Simullated.Client> clientes;
	
	public Tour(){
		retails = new ArrayList<>();
		clientes = new ArrayList<>();
	}
}
