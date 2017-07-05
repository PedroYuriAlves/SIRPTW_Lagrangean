package algoritmo;

import java.text.DecimalFormat;

import org.python.modules.math;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import instancias.Instancia;
import instancias.Parametros;
import instancias.Sol_XZ;

public class Constraints {
	final static double gapAceito = 0.1;

	public static void constr_Zero(Parametros par,GRBModel model,Sol_XZ xz_zero) throws GRBException{
		for (int t = 0; t < par.H; t++) { //Para cada t			
			for (int v = 0; v < par.V; v++) {//Para cada v	
				GRBLinExpr exprY = new GRBLinExpr();//Cria Expressão
				exprY.addTerm(1,model.getVarByName("y"+"_"+t+"_"+v));
				model.addConstr(exprY, GRB.EQUAL,xz_zero.x.Yt_v[t][v], "cY0_t"+t+"_v"+v);

				for (int i = 0; i < par.S; i++) {//Para cada i
					for (int j = 0; j < par.S; j++) {//Para cada j
						GRBLinExpr exprX = new GRBLinExpr();//Cria Expressão
						exprX.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
						model.addConstr(exprX, GRB.EQUAL,xz_zero.x.Xijt_v[i][j][t][v], "cX0_i"+i+"_j"+j+"_t"+t+"_v"+v);


					}
				}		
			}					
		}
	}	
	public static String checkconstr_Zero(Parametros par,Sol_XZ xz){
		String ret = "";
		for (int t = 0; t < par.H; t++) { //Para cada t			
			for (int v = 0; v < par.V; v++) {//Para cada v	
				//GRBLinExpr exprY = new GRBLinExpr();//Cria Expressão
				double exprY = 0;//Cria Expressão
				exprY += 1*xz.x.Yt_v[t][v];
				//	exprY.addTerm(1,model.getVarByName("y"+"_"+t+"_"+v));
				//model.addConstr(exprY, GRB.EQUAL,xz_zero.x.Yt_v[t][v], "cY0_t"+t+"_v"+v);
				if(exprY == xz.x.Yt_v[t][v]){				
				}else{
					ret = ";"+"cY0_t"+t+"_v"+v;
				}
				for (int i = 0; i < par.S; i++) {//Para cada i
					for (int j = 0; j < par.S; j++) {//Para cada j
						//GRBLinExpr exprX = new GRBLinExpr();//Cria Expressão
						double exprX = 0;//Cria Expressão
						exprX += 1*xz.x.Xijt_v[i][j][t][v];
						//						exprX.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
						//						model.addConstr(exprX, GRB.EQUAL,xz_zero.x.Xijt_v[i][j][t][v], "cX0_i"+i+"_j"+j+"_t"+t+"_v"+v);
						if(exprX == xz.x.Xijt_v[i][j][t][v]){
						}else{
							if(Math.abs(exprX- xz.x.Xijt_v[i][j][t][v])> gapAceito)ret = ";"+"cX0_i"+i+"_j"+j+"_t"+t+"_v"+v+"("+exprX +"=="+xz.x.Xijt_v[i][j][t][v]+")";		
						}

					}
				}		
			}					
		}
		return ret;
	}	


	public static void constr_Ij0(Parametros par,GRBModel model) throws GRBException{
		for (int j = 0; j < par.S; j++) { //Para cada t						
			GRBLinExpr expr = new GRBLinExpr();//Cria Expressão
			expr.addTerm(1,model.getVarByName("I"+"_"+j+"_"+"0"));	
			model.addConstr(expr, GRB.EQUAL,par.I_j0[j], "cI_j0_j"+j);
		}
	}			
	public static String checkconstr_Ij0(Parametros par,Sol_XZ xz){
		String ret = "";
		for (int j = 0; j < par.S; j++) { //Para cada t						
			//			GRBLinExpr expr = new GRBLinExpr();//Cria Expressão
			double expr = 0;//Cria Expressão
			expr += 1*xz.z.Ijt[j][0];
			//			expr.addTerm(1,model.getVarByName("I"+"_"+j+"_"+"0"));	
			//			model.addConstr(expr, GRB.EQUAL,par.I_j0[j], "cI_j0_j"+j);
			if(expr == par.I_j0[j]){
			}else{
				if(Math.abs(expr - par.I_j0[j])> gapAceito)
					ret = ";"+ "cI_j0_j"+j+"("+expr +"=="+par.I_j0[j]+")";		
			}
		}
		return ret;
	}			

	public static void constr2(Parametros par,GRBModel model) throws GRBException{
		// Add constraint: Sum_v(Sum_i(Xijt_v)) <= 1

		GRBLinExpr expr = new GRBLinExpr();		
		for (int j = 1; j < par.S; j++) {//Para cada j
			for (int t = 0; t < par.H; t++) { //Para cada t
				expr = new GRBLinExpr();//Cria Expressão
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v)); 
					}
				}
				model.addConstr(expr, GRB.LESS_EQUAL, 1, "c2_j"+j+"_t"+t);				
			}
		}	
	}
	public static String checkconstr2(Parametros par,Sol_XZ xz) {
		// Add constraint: Sum_v(Sum_i(Xijt_v)) <= 1
		String ret  = "";
		//		GRBLinExpr expr = new GRBLinExpr();
		double expr = 0;
		for (int j = 1; j < par.S; j++) {//Para cada j
			for (int t = 0; t < par.H; t++) { //Para cada t
				expr = 0;
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						//						expr.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v)); 
						expr+= 1.0* xz.x.Xijt_v[i][j][t][v];//("x"+"_"+i+"_"+j+"_"+t+"_"+v)); 
					}
				}

				//				model.addConstr(expr, GRB.LESS_EQUAL, 1, "c2_j"+j+"_t"+t);		
				if(expr <= 1){
				}else{
					if(Math.abs(expr - 1) > gapAceito)ret = ";"+  "c2_j"+j+"_t"+t+"("+expr +"<="+1+")";		
				}		
			}
		}	
		return ret;
	}
	public static void constr3(Parametros par,GRBModel model) throws GRBException{
		GRBLinExpr expr = new GRBLinExpr();
		for (int j = 0; j < par.S; j++) {//Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t
				expr = new GRBLinExpr();//Cria Expressão
				for (int v = 0; v < par.V; v++) {// Para cada v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v)); 
					}
				}
				for (int v = 0; v < par.V; v++) {// Para cada v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr.addTerm(-1.0, model.getVarByName("x"+"_"+j+"_"+k+"_"+t+"_"+v)); 
					}	
				}
				model.addConstr(expr, GRB.EQUAL, 0, "c3_j"+j+"_t"+t);
			}							
		}
	}
	public static String checkconstr3(Parametros par,Sol_XZ xz){
		//		GRBLinExpr expr = new GRBLinExpr();
		String ret = "";
		double expr = 0;
		for (int j = 0; j < par.S; j++) {//Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t
				expr = 0;
				for (int v = 0; v < par.V; v++) {// Para cada v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr += 1*xz.x.Xijt_v[i][j][t][v];
						//						expr.addTerm(1.0, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v)); 
					}
				}

				for (int v = 0; v < par.V; v++) {// Para cada v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr += (-1)*xz.x.Xijt_v[j][k][t][v];
						//						expr.addTerm(-1.0, model.getVarByName("x"+"_"+j+"_"+k+"_"+t+"_"+v)); 
					}	
				}
				//					model.addConstr(expr, GRB.EQUAL, 0, "c3_j"+j+"_t"+t+"_v"+v);
				if(expr == 0){
				}
				else{
					if(Math.abs(expr - 0) > gapAceito)ret += ";"+ "c3_j"+j+"_t"+t+"("+expr +"=="+0+")";						
				}
			}	
		}	 
		return ret;
	}
	public static void constr4(Parametros par,GRBModel model) throws GRBException{

		GRBLinExpr expr = new GRBLinExpr();
		for (int t = 1; t < par.H; t++) { //Para cada t
			for (int v = 0; v < par.V; v++) {// Para cada v	
				expr = new GRBLinExpr();//Cria Expressão
				for (int i = 0; i < par.S; i++) {// Somatória de i
					for (int j = 0; j < par.S; j++) {// Somatória de j
						expr.addTerm(par.θ_ij[i][j],model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));						
					}					

				}	
				model.addConstr(expr, GRB.LESS_EQUAL, par.τt, "c4_t"+t+"_v"+v);
			}
		}	
	}
	public static String checkconstr4(Parametros par,Sol_XZ xz){
		//		GRBLinExpr expr = new GRBLinExpr();
		String ret = "";
		double expr = 0;
		for (int t = 1; t < par.H; t++) { //Para cada t
			for (int v = 0; v < par.V; v++) {// Para cada v	
				expr = 0;
				for (int i = 0; i < par.S; i++) {// Somatória de i
					for (int j = 0; j < par.S; j++) {// Somatória de j
						expr += par.θ_ij[i][j] * xz.x.Xijt_v[i][j][t][v];
						//						expr.addTerm(par.θ_ij[i][j],model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));						
					}					

				}	
				//				model.addConstr(expr, GRB.LESS_EQUAL, par.τt, "c4_t"+t+"_v"+v);
				if(expr <=  par.τt){
				}
				else{
					if(Math.abs(expr -  par.τt) > gapAceito)ret += ";"+ "c4_t"+t+"_v"+v+"("+expr +"<="+par.τt+")";						
				}
			}
		}	
		return ret;
	}
	public static void constr5(Parametros par,GRBModel model) throws GRBException{

		GRBLinExpr expr = new GRBLinExpr();
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t

				expr = new GRBLinExpr();//Cria Expressão
				for (int v = 0; v < par.V; v++) {// Somatória de 		v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));						
					}	
				}

				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr.addTerm(-1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));						
					}	
				}
				model.addConstr(expr, GRB.EQUAL, model.getVarByName("q"+"_"+j+"_"+t), "c5_j"+j+"_t"+t);
			}
		}	
	}
	public static String checkconstr5(Parametros par,Sol_XZ xz){
		String ret = "";
		//		GRBLinExpr expr = new GRBLinExpr();
		double expr = 0;
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t

				expr = 0;
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						//						expr.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));
						expr+= 1*xz.z.Qijt_v[i][j][t][v];
					}	
				}

				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr += (-1)*xz.z.Qijt_v[j][k][t][v];
						//						expr.addTerm(-1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));						
					}	
				}
				//				model.addConstr(expr, GRB.EQUAL, model.getVarByName("q"+"_"+j+"_"+t), "c5_j"+j+"_t"+t);
				if(RoundTo2Decimals(expr) ==  xz.z.qjt[j][t]){
				}
				else{
					if(Math.abs(RoundTo2Decimals(expr) - xz.z.qjt[j][t]) > gapAceito)
						ret += ";"+ "c5_j"+j+"_t"+t+"("+RoundTo2Decimals(expr) +"<="+xz.z.qjt[j][t]+")";						
				}
			}
		}	
		return ret;
	}
	public static void constr6(Parametros par,GRBModel model) throws GRBException{	
		for (int i = 0; i < par.S; i++) {//Para cada i
			for (int j = 0; j < par.S; j++) { //Para cada j
				for (int t = 1; t < par.H; t++) { //Para cada t
					for (int v = 0; v < par.V; v++) {//Para cada v
						GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
						GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
						expr1.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));	

						expr2.addTerm(par.kv,model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));		

						model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c6_i"+i+"_j"+j+"_t"+t+"_v"+v);
					}
				}
			}
		}	
	}
	public static String checkconstr6(Parametros par,Sol_XZ xz){	
		String ret = "";
		for (int i = 0; i < par.S; i++) {//Para cada i
			for (int j = 0; j < par.S; j++) { //Para cada j
				for (int t = 1; t < par.H; t++) { //Para cada t
					for (int v = 0; v < par.V; v++) {//Para cada v
						//						GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
						double expr1 = 0;
						//						GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
						double expr2 = 0;
						//						expr1.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));	
						expr1 += 1* xz.z.Qijt_v[i][j][t][v];
						//						expr2.addTerm(par.kv,model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));		
						expr2 += par.kv* xz.x.Xijt_v[i][j][t][v];
						//						model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c6_i"+i+"_j"+j+"_t"+t+"_v"+v);
						if(RoundTo2Decimals(expr1) <= RoundTo2Decimals(expr2)){
						}
						else{
							if(Math.abs(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2)) > gapAceito)
								ret += ";"+ "c6_i"+i+"_j"+j+"_t"+t+"_v"+v+"("+RoundTo2Decimals(expr1) +"<="+RoundTo2Decimals(expr2)+")";						
						}
					}
				}
			}
		}	
		return ret;
	}

	public static void constr6IA(Parametros par,GRBModel model) throws GRBException{	
		//for (int i = 0; i < par.S; i++) {//Para cada i
		for (int j = 0; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t
				//			for (int v = 0; v < par.V; v++) {//Para cada v
				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
				//						GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				expr1.addTerm(1,model.getVarByName("q"+"_"+j+"_"+t));	

				model.addConstr(expr1, GRB.LESS_EQUAL, par.kv, "c6IA_j"+j+"_t"+t);
			}
		}
	}
	public static String checkconstr6IA(Parametros par,Sol_XZ xz) {	
		//for (int i = 0; i < par.S; i++) {//Para cada i
		String ret = "";
		for (int j = 0; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t
				//			for (int v = 0; v < par.V; v++) {//Para cada v
				//				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
				double expr1 = 0;
				//						GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				//				expr1.addTerm(1,model.getVarByName("q"+"_"+j+"_"+t));	
				expr1 += 1* xz.z.qjt[j][t];

				//				model.addConstr(expr1, GRB.LESS_EQUAL, par.kv, "c6IA_j"+j+"_t"+t);
				if(expr1 <=  par.kv){
				}
				else{
					if(Math.abs(expr1 -  par.kv) > gapAceito)
						ret += ";"+ "c6IA_j"+j+"_t"+t+"("+expr1 +"<="+par.kv+")";	
				}
			}
		}
		return ret;
	}

	public static void constr7(Parametros par,GRBModel model) throws GRBException{	
		//		for (int j = 1; j < par.S; j++) { //Para cada j
		for (int t = 1; t < par.H; t++) { //Para cada t
			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
			expr1.addTerm(1,model.getVarByName("I_0"+"_"+(t-1)));
			expr1.addConstant(par.Rrt[t]);
			expr1.addTerm(-1,model.getVarByName("I_0"+"_"+(t)));

			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			for (int j1 = 1; j1 < par.S; j1++) { //Somatória de j
				expr2.addTerm(1,model.getVarByName("q"+"_"+j1+"_"+t));	
			}				
			model.addConstr(expr1, GRB.EQUAL, expr2, "c7_t"+t);
		}
	}
	public static String checkconstr7(Parametros par,Sol_XZ xz) {	
		String ret = "";
		for (int t = 1; t < par.H; t++) { //Para cada t
			//			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
			double expr1 = 0;
			//			expr1.addTerm(1,model.getVarByName("I_0"+"_"+(t-1)));
			expr1 += 1*xz.z.Ijt[0][t-1];
			expr1 += par.Rrt[t];
			//			expr1.addConstant(par.Rrt[t]);
			expr1 += -1*xz.z.Ijt[0][t];
			//			expr1.addTerm(-1,model.getVarByName("I_0"+"_"+(t)));

			//			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			double expr2 = 0;
			for (int j1 = 1; j1 < par.S; j1++) { //Somatória de j
				//				expr2.addTerm(1,model.getVarByName("q"+"_"+j1+"_"+t));	
				expr2 += 1*xz.z.qjt[j1][t];
			}				
			//			model.addConstr(expr1, GRB.EQUAL, expr2, "c7_t"+t);
			if(RoundTo2Decimals(expr1) == RoundTo2Decimals(expr2)){
			}
			else{
				if(Math.abs(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2))> gapAceito)
					ret += ";"+ "c7_t"+t+"("+RoundTo2Decimals(expr1) +"=="+RoundTo2Decimals(expr2)+")";						
			}
		}
		return ret;
	}
	public static void constr8(Parametros par,GRBModel model) throws GRBException{	
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t

				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
				expr1.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(t-1)));
				expr1.addTerm(1,model.getVarByName("q"+"_"+j+"_"+t));
				expr1.addTerm(-1,model.getVarByName("I"+"_"+j+"_"+(t)));

				GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				expr2.addConstant(par.τt*par.d_jt[j][t]);		

				model.addConstr(expr1, GRB.EQUAL, expr2, "c8_j"+j+"_t"+t);
			}
		}	
	}
	public static String checkconstr8(Parametros par,Sol_XZ xz) {	
		String ret = "";
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t

				//				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
				double expr1 = 0;				
				//				expr1.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(t-1)));
				//				expr1.addTerm(1,model.getVarByName("q"+"_"+j+"_"+t));
				//				expr1.addTerm(-1,model.getVarByName("I"+"_"+j+"_"+(t)));
				expr1 += xz.z.Ijt[j][t-1]+xz.z.qjt[j][t]-xz.z.Ijt[j][t];
				//				GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				double expr2 = 0;
				//				expr2.addConstant(par.τt*par.d_jt[j][t]);	
				expr2+=par.τt*par.d_jt[j][t];

				//				model.addConstr(expr1, GRB.EQUAL, expr2, "c8_j"+j+"_t"+t);
				if(RoundTo2Decimals(expr1) == RoundTo2Decimals(expr2)){
				}
				else{
					if(Math.abs(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2))> gapAceito)
						ret += ";"+ "c8_j"+j+"_t"+t+"("+RoundTo2Decimals(expr1) +"=="+RoundTo2Decimals(expr2)+")";		;						
				}
			}
		}	
		return ret;
	}
	public static void constr9(Parametros par,GRBModel model) throws GRBException{	
		for (int j = 1; j < par.S; j++) { //Para cada j

			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
			expr1.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(0)));
			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			expr2.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(par.H-1)));	

			model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c9_j"+j);
		}	
	}
	public static String checkconstr9(Parametros par,Sol_XZ xz) {	
		String ret = "";
		for (int j = 1; j < par.S; j++) { //Para cada j

			//			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
			double expr1 = 0;
			//			expr1.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(0)));
			expr1 += 1*xz.z.Ijt[j][0];
			//			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			double expr2 = 0;
			//			expr2.addTerm(1,model.getVarByName("I"+"_"+j+"_"+(par.H-1)));	
			expr2 += 1*xz.z.Ijt[j][par.H-1];
			//			model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c9_j"+j);
			if(RoundTo2Decimals(expr1) <= RoundTo2Decimals(expr2)){
			}
			else{
				if(Math.abs(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2))> gapAceito)
					ret += ";"+ "c9_j"+j+"("+RoundTo2Decimals(expr1) +"<="+RoundTo2Decimals(expr2)+")";						
			}
		}	
		return ret;
	}

	public static void constr10(Parametros par,GRBModel model) throws GRBException{	
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 0; t < par.H; t++) { //Para cada t
				for (int v = 0; v < par.V; v++) { //Para cada v

					GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
					expr1.addTerm(1,model.getVarByName("x_0"+"_"+j+"_"+t+"_"+v));

					GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
					expr2.addTerm(1,model.getVarByName("y"+"_"+t+"_"+v));	

					model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c10_j"+j+"_t"+t+"_v"+v);
				}
			}	
		}
	}

	public static String checkconstr10(Parametros par,Sol_XZ xz) {	
		String ret = "";
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 0; t < par.H; t++) { //Para cada t
				for (int v = 0; v < par.V; v++) { //Para cada v

					//					GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão
					double expr1 = 0;
					//					expr1.addTerm(1,model.getVarByName("x_0"+"_"+j+"_"+t+"_"+v));
					expr1 += 1*xz.x.Xijt_v[0][j][t][v];
					//					GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
					double expr2 = 0;
					//					expr2.addTerm(1,model.getVarByName("y"+"_"+t+"_"+v));	
					expr2 += 1*xz.x.Yt_v[t][v];
					//					model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c10_j"+j+"_t"+t+"_v"+v);
					if(expr1 <= expr2){
					}
					else{
						if(Math.abs(expr1 - expr2)> gapAceito)
							ret += ";"+ "c10_j"+j+"_t"+t+"_v"+v+"("+RoundTo2Decimals(expr1) +"<="+RoundTo2Decimals(expr2)+")";					
					}
				}
			}	
		}
		return ret;
	}
	public static void constr11(Parametros par,GRBModel model) throws GRBException{	
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t		

				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão				
				expr1.addTerm(1, model.getVarByName("I_"+j+"_"+(t-1)));

				for (int s = t; s < par.H; s++) { //Para cada s	
					expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+s));
				}

				GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				for (int s = t; s < par.H; s++) { //Para cada s	
					expr2.addConstant(par.Dj[j]*par.τt);
				}
				expr2.addConstant(par.zAlfa*(Math.sqrt((par.H-1)-t+1))*par.sigma_j[j]);

				model.addConstr(expr1, GRB.GREATER_EQUAL, expr2, "c11_j"+j+"_t"+t);
			}
		}
	}
	public static String checkconstr11(Parametros par,Sol_XZ xz) {	
		String ret = "";
		for (int j = 1; j < par.S; j++) { //Para cada j
			for (int t = 1; t < par.H; t++) { //Para cada t		

				//				GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão		
				double expr1 = 0;
				//				expr1.addTerm(1, model.getVarByName("I_"+j+"_"+(t-1)));
				expr1 += xz.z.Ijt[j][t-1];
				for (int s = t; s < par.H; s++) { //Para cada s	
					//					expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+s));
					expr1+= xz.z.qjt[j][s];
				}

				//				GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
				double expr2 = 0;
				for (int s = t; s < par.H; s++) { //Para cada s	
					//					expr2.addConstant(par.Dj[j]*par.τt);
					expr2 += par.Dj[j]*par.τt;
				}
				//				expr2.addConstant(par.zAlfa*(Math.sqrt((par.H-1)-t+1))*par.sigma_j[j]);
				expr2 += par.zAlfa*(Math.sqrt((par.H-1)-t+1))*par.sigma_j[j];
				//				model.addConstr(expr1, GRB.GREATER_EQUAL, expr2, "c11_j"+j+"_t"+t);
				if(RoundTo2Decimals(expr1) >= RoundTo2Decimals(expr2)){
				}
				else{
					if(Math.abs(RoundTo2Decimals(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2)))> gapAceito)
						ret += ";"+ "c11_j"+j+"_t"+t+"("+RoundTo2Decimals(expr1) +">="+RoundTo2Decimals(expr2)+")";						
				}
			}
		}
		return ret;
	}
	public static void constr16(Parametros par,GRBModel model) throws GRBException{	
		for (int j = 1; j < par.S; j++) { //Para cada j
			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão	

			expr1.addConstant(par.Dj[j]*par.τt);
			expr1.addConstant(par.zAlfa*par.sigma_j[j]);				
			expr1.addTerm(-1, model.getVarByName("I_"+j+"_"+(0)));


			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			for (int v = 0; v < par.V; v++) {//Para cada v
				for (int i= 0; i < par.S; i++) { //Para cada s	
					expr2.addTerm(1, model.getVarByName("Q"+"_"+i+"_"+j+"_"+1+"_"+v));
				}
			}

			model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c16_j"+j);
		}
	}

	public static String checkconstr16(Parametros par,Sol_XZ xz){	
		String ret = "";
		for (int j = 1; j < par.S; j++) { //Para cada j
			//			GRBLinExpr expr1 = new GRBLinExpr();//Cria Expressão	
			double expr1 = 0;
			//			expr1.addConstant(par.Dj[j]*par.τt);
			//			expr1.addConstant(par.zAlfa*par.sigma_j[j]);	
			expr1 += par.Dj[j]*par.τt + par.zAlfa*par.sigma_j[j] - xz.z.Ijt[j][0];
			//			expr1.addTerm(-1, model.getVarByName("I_"+j+"_"+(0)));


			//			GRBLinExpr expr2 = new GRBLinExpr();//Cria Expressão
			double expr2 = 0;
			for (int v = 0; v < par.V; v++) {//Para cada v
				for (int i= 0; i < par.S; i++) { //Para cada s	
					//					expr2.addTerm(1, model.getVarByName("Q"+"_"+i+"_"+j+"_"+1+"_"+v));
					expr2 += xz.z.Qijt_v[i][j][1][v];
				}
			}

			//			model.addConstr(expr1, GRB.LESS_EQUAL, expr2, "c16_j"+j);
			if(RoundTo2Decimals(expr1) <= RoundTo2Decimals(expr2)){
			}
			else{
				if(Math.abs(RoundTo2Decimals(expr1) - RoundTo2Decimals(expr2))> gapAceito)
					ret += ";"+ "c16_j"+j+"("+RoundTo2Decimals(expr1) +"<="+RoundTo2Decimals(expr2)+")";;						
			}
		}
		return ret;
	}
	public static double RoundTo2Decimals(double val) {
		try{
			DecimalFormat df2 = new DecimalFormat("###.##");
			return Double.valueOf(df2.format(val).replaceAll(",", "."));
		}
		catch(Exception e){
			return 0;
		}

	}
}
