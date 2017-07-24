package instancias;

public class Z {

	public Z(int S, int T,int V){
		Ijt = new double [S][T];
		Qijt_v = new double [S][S][T][V];
		hjtv = new double [S][T][V];
		hd_jtv = new double [S][T][V];
		qjt = new double [S][T];
	}
	public double[][] Ijt ;
	public double[][][] hjtv;
	public double[][][] hd_jtv;
	public double[][][][] Qijt_v ;
	public double[][] qjt;
	public double[] I_j0;
}
