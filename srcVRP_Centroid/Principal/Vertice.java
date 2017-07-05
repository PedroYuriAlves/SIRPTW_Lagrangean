package Principal;
public class Vertice {
	private int numero;
	private int id;
	private double X;
	private double Y;
	private double demanda;
	
	public Vertice(int numero, int X, int Y, int id) {
		this.numero = numero;
		this.X = X;
		this.Y = Y;
		this.id = id;
	}
	
	public Vertice(int numeroVertice, double x2, double y2, int id) {
		// TODO Auto-generated constructor stub
		this.numero = numeroVertice;
		this.X = x2;
		this.Y = y2;
		this.id = id;
	}

	//Se��o de getters e setters
	public int getNumero() { //Nao necessita de setter por causa do construtor
		return numero;
	}
	
	public int getId() { //Nao necessita de setter por causa do construtor
		return id;
	}
	public double getX() { //Nao necessita de setter por causa do construtor
		return X;
	}
	public double getY() { //Nao necessita de setter por causa do construtor
		return Y;
	}

	public double getDemanda() {
		return demanda;
	}
	public void setDemanda(double demanda2) {
		this.demanda = demanda2;
	}
}
