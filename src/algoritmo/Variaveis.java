package algoritmo;

import gurobi.*;
import instancias.Parametros;


public class Variaveis {

	public GRBVar[][][][] addVarXijt_v(Parametros par,GRBModel model) throws GRBException{
		GRBVar[][][][] Xijt_v = new GRBVar[par.S][par.S][par.H][par.V];

		for (int i = 0; i < par.S; i++) {
			for (int j = 0; j < par.S; j++) {
				for (int t = 0; t < par.H; t++) {
					for (int v = 0; v < par.V; v++) {
						Xijt_v[i][j][t][v] = model.addVar( 0, 1,1, GRB.BINARY,
								"x_"+i+"_"+j+"_"+t+"_"+v);
					}
				}
			}
		}
		return Xijt_v;
	}
	public GRBVar[][] addVarYt_v(Parametros par,GRBModel model) throws GRBException{
		GRBVar[][] Yt_v = new GRBVar[par.H][par.V];

		for (int t = 0; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				Yt_v[t][v] = model.addVar( 0, 1,1, GRB.BINARY,
						"y"+"_"+t+"_"+v);
			}
		}

		return Yt_v;
	}


	public GRBVar[][][][] addVarQijt_v(Parametros par,GRBModel model) throws GRBException{
		GRBVar[][][][] Qijt_v = new GRBVar[par.S][par.S][par.H][par.V];

		for (int i = 0; i < par.S; i++) {
			for (int j = 0; j < par.S; j++) {
				for (int t = 0; t < par.H; t++) {
					for (int v = 0; v < par.V; v++) {
						Qijt_v[i][j][t][v] = model.addVar(0, par.kv, 1 , GRB.CONTINUOUS,
								"Q"+"_"+i+"_"+j+"_"+t+"_"+v);
					}
				}
			}
		}
		
		return Qijt_v;
	}
	public GRBVar[][][][] addVarQijt_v(Parametros par,GRBModel model,int nV) throws GRBException{
		GRBVar[][][][] Qijt_v = new GRBVar[par.S][par.S][par.H][nV];

		for (int i = 0; i < par.S; i++) {
			for (int j = 0; j < par.S; j++) {
				for (int t = 0; t < par.H; t++) {
					for (int v = 0; v < nV; v++) {
						Qijt_v[i][j][t][v] = model.addVar(0, par.kv, 1 , GRB.CONTINUOUS,
								"Q"+"_"+i+"_"+j+"_"+t+"_"+v);
					}
				}
			}
		}
		
		return Qijt_v;
	}
	
	public GRBVar[][] addVar_qjt(Parametros par,GRBModel model) throws GRBException{
		GRBVar[][] qjt = new GRBVar[par.S][par.H];
		for (int j = 0; j < par.S; j++) {
			for (int t = 0; t < par.H; t++) {					
				qjt[j][t] = model.addVar(0, 1000, 1 , GRB.CONTINUOUS,
						"q"+"_"+j+"_"+t);
			}
		}
		return qjt;
	}
	public GRBVar[][] addVarIjt(Parametros par,GRBModel model) throws GRBException{
		GRBVar[][] Ijt = new GRBVar[par.S][par.H];
		for (int j = 0; j < par.S; j++) {
			for (int t = 0; t < par.H; t++) {					
				Ijt[j][t] = model.addVar(0, 1000,1, GRB.CONTINUOUS,
						"I"+"_"+j+"_"+t);
				model.update();		
			}
		}
		return Ijt;
	}

}