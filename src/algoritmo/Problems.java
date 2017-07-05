package algoritmo;
import gurobi.*;
import instancias.Parametros;
import instancias.Sol_XZ;

public class Problems {
	static Constraints constraints = new Constraints();
	static Variaveis decisionVariables = new Variaveis();

	final static String sucesso = "T";
	final static String invalido = "F";

	public static GRBEnv getEnv() throws GRBException{
		GRBEnv env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
		env.set(GRB.DoubleParam.NodefileStart, 0.5);
		return env;
	}
	static String path = "C:\\Users\\Krespo\\Desktop\\Testes_SIRP";
	public  GRBModel CV(Parametros par, GRBEnv env, Sol_XZ xz) throws GRBException{
		double custo = Double.MAX_VALUE;
		// Read model from file
		env = getEnv();
		
		GRBModel model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense,1);//Minimize
		Variaveis var = new Variaveis();
		var.addVarXijt_v(par, model);
		var.addVarYt_v(par, model);
		var.addVarQijt_v(par, model);
		var.addVarIjt(par, model);
		var.addVar_qjt(par, model);
		model.update();

		if(xz != null){
			constraints.constr_Zero(par,model,xz);
		}
		if(par.I_j0 != null && par.I_j0.length > 0 && par.restrInv == 1){
			constraints.constr_Ij0(par,model);
		}


		constraints.constr2(par, model);
		constraints.constr3(par, model);
		constraints.constr4(par, model);
		constraints.constr5(par, model);
		constraints.constr6(par, model);
		constraints.constr8(par, model);
		constraints.constr7(par, model);
		constraints.constr9(par, model);
		constraints.constr10(par, model);


		GRBLinExpr exprObj = new GRBLinExpr();//Cria Expressão

		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				exprObj.addTerm(par.ψv[v], model.getVarByName("y"+"_"+t+"_"+v));

				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v]*par.ν_v*par.θ_ij[i][j])  + par.ϕj_t[j][t];						
						exprObj.addTerm(coeff, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
					}
				}
			}
		}

		for (int t = 1; t < par.H; t++) {
			for (int j = 0; j < par.S; j++) {
				exprObj.addTerm(par.ηj_t[j][t],model.getVarByName("I"+"_"+j+"_"+t));
			}
		}

		model.setObjective(exprObj);

		// Solve model and capture solution information
		model.optimize();
		System.out.println("");
		System.out.println("Optimization complete");
		if (model.get(GRB.IntAttr.SolCount) == 0) {
			System.out.println("No solution found, optimization status = "
					+ model.get(GRB.IntAttr.Status));
		} else {
			System.out.println("Solution found, objective = "
					+ model.get(GRB.DoubleAttr.ObjVal));
			custo = model.get(GRB.DoubleAttr.ObjVal);
		}
		if(xz != null){
			model.write(path+"CVzero.lp");
		}else{
			model.write(path+"CV.lp");
		}
		return model;
	}	
	public static GRBModel CVα(Parametros par, GRBEnv env, Sol_XZ xz) throws GRBException{
		double custo = Double.MAX_VALUE;
		// Read model from file
		env = getEnv();
		GRBModel model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense,1);//Minimize
		Variaveis var = new Variaveis();
		var.addVarXijt_v(par, model);
		var.addVarYt_v(par, model);
		var.addVarQijt_v(par, model);
		var.addVarIjt(par, model);
		var.addVar_qjt(par, model);
		model.update();

		if(xz != null){
			constraints.constr_Zero(par,model,xz);
		}
		if(par.I_j0 != null && par.I_j0.length > 0 && par.restrInv == 1){
			constraints.constr_Ij0(par,model);
		}


		constraints.constr2(par, model);
		constraints.constr3(par, model);
		constraints.constr4(par, model);
		constraints.constr5(par, model);
		constraints.constr6(par, model);
		constraints.constr7(par, model);
		constraints.constr9(par, model);
		constraints.constr10(par, model);
		constraints.constr11(par, model);


		GRBLinExpr exprObj = new GRBLinExpr();//Cria Expressão

		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				exprObj.addTerm(par.ψv[v], model.getVarByName("y"+"_"+t+"_"+v));

				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v]*par.ν_v*par.θ_ij[i][j])  + par.ϕj_t[j][t];						
						exprObj.addTerm(coeff, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
					}
				}
			}
		}

		for (int t = 1; t < par.H; t++) {
			for (int j = 0; j < par.S; j++) {
				exprObj.addTerm(par.ηj_t[j][t],model.getVarByName("I"+"_"+j+"_"+t));
			}
		}

		model.setObjective(exprObj);

		// Solve model and capture solution information
		model.optimize();
		System.out.println("");
		System.out.println("Optimization complete");
		if (model.get(GRB.IntAttr.SolCount) == 0) {
			System.out.println("No solution found, optimization status = "
					+ model.get(GRB.IntAttr.Status));
		} else {
			System.out.println("Solution found, objective = "
					+ model.get(GRB.DoubleAttr.ObjVal));
			custo = model.get(GRB.DoubleAttr.ObjVal);
		}
		if(xz != null){
			model.write(path+"CVzero.lp");
		}else{
			model.write(path+"CV.lp");
		}
		return model;
	}	


	public static GRBModel IA_Pμk (Parametros par, GRBEnv env, Sol_XZ xz) throws GRBException{
		double custo = Double.MAX_VALUE;
		// Read model from file
		env = getEnv();
		GRBModel model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense,1);//Minimize
		Variaveis var = new Variaveis();
		var.addVarIjt(par, model);
		var.addVar_qjt(par, model);
		//		var.addVarQijt_v(inst, model);
		model.update();

		if(xz != null){
			constraints.constr_Zero(par,model,xz);
		}
		if(par.I_j0 != null && par.I_j0.length > 0 && par.restrInv == 1){
			constraints.constr_Ij0(par,model);
		}

		constraints.constr7(par, model);
		constraints.constr9(par, model);
		constraints.constr11(par, model);
		if(par.restrCap == 1) constraints.constr6IA(par, model);

		GRBLinExpr exprObj = new GRBLinExpr();
		for (int j = 0; j < par.S; j++) {
			for (int t = 1; t < par.H; t++) {
				exprObj.addTerm(par.ηj_t[j][t], model.getVarByName("I_"+j+"_"+t));
			}
		}

		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {
				exprObj.addTerm(par.μjt[j][t], model.getVarByName("q"+"_"+j+"_"+t));
			}
		}
		model.setObjective(exprObj);

		model.optimize();
		// Solve model and capture solution information
		if(xz != null){
			model.write(path+"IAPzero.lp");
		}else{
			model.write(path+"IAP.lp");
		}

		return model;
	}

	public static GRBModel CVRP_Pμ (Parametros par, GRBEnv env, Sol_XZ xz) throws GRBException{
		double custo = Double.MAX_VALUE;
		// Read model from file
		env = getEnv();

		GRBModel model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense,1);//Minimize
		Variaveis var = new Variaveis();

		var.addVarXijt_v(par, model);
		var.addVarYt_v(par, model);
		var.addVarQijt_v(par, model);
		var.addVarIjt(par, model);
//		var.addVar_qjt(par, model);
		model.update();

		if(xz != null){
			constraints.constr_Zero(par,model,xz);
		}
		if(par.I_j0 != null && par.I_j0.length > 0 && par.restrInv == 1){
			constraints.constr_Ij0(par,model);
		}

		constraints.constr2(par, model);
		constraints.constr3(par, model);
		constraints.constr4(par, model);

		constraints.constr6(par, model);
		constraints.constr10(par, model);
//		constraints.constr16(par, model);




		GRBLinExpr exprObj = new GRBLinExpr();

		GRBLinExpr exprObj1 = new GRBLinExpr();
		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				exprObj1.addTerm(par.ψv[v], model.getVarByName("y"+"_"+t+"_"+v));

				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v]*par.ν_v*par.θ_ij[i][j])  + par.ϕj_t[i][t];
						exprObj1.addTerm(coeff, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
					}
				}
			}
		}


		GRBLinExpr exprObj2 = new GRBLinExpr();
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {

				GRBLinExpr expr1 = new GRBLinExpr();
				GRBLinExpr expr2 = new GRBLinExpr();

				//				expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+t));	

				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr2.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));						
					}	
				}
				GRBLinExpr expr3 = new GRBLinExpr();
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr3.addTerm(1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));						
					}	
				}
				expr1.multAdd(-1, expr2);
				expr1.multAdd(+1, expr3);


				exprObj2.multAdd(par.μjt[j][t], expr1);
			}
		}
		exprObj.multAdd(1,exprObj1);
		exprObj.multAdd(+1,exprObj2);

		model.setObjective(exprObj);
		model.optimize();
		if(xz != null){
			model.write(path+"RPPzero.lp");
		}else{
			model.write(path+"RPP.lp");
		}
		// Solve model and capture solution information

		return model;
	}


	public static GRBModel CVLRμ (Parametros par, GRBEnv env, Sol_XZ xz) throws GRBException{
		double custo = Double.MAX_VALUE;
		// Read model from file
		env = getEnv();
		GRBModel model = new GRBModel(env);
		model.set(GRB.IntAttr.ModelSense,1);//Minimize
		Variaveis var = new Variaveis();

		var.addVarXijt_v(par, model);
		var.addVarYt_v(par, model);
		var.addVarQijt_v(par, model);
		var.addVarIjt(par, model);
		var.addVar_qjt(par, model);

		model.update();

		if(xz != null){
			constraints.constr_Zero(par,model,xz);
		}
		if(par.I_j0 != null && par.I_j0.length > 0 && par.restrInv == 1){
			constraints.constr_Ij0(par,model);
		}

		constraints.constr7(par, model);
		constraints.constr9(par, model);
		constraints.constr11(par, model);

		constraints.constr2(par, model);
		constraints.constr3(par, model);
		constraints.constr4(par, model);

		constraints.constr6(par, model);
		constraints.constr10(par, model);
//		constraints.constr16(par, model);




		GRBLinExpr exprObj = new GRBLinExpr();

		GRBLinExpr exprObj1 = new GRBLinExpr();
		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				exprObj1.addTerm(par.ψv[v], model.getVarByName("y"+"_"+t+"_"+v));
				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v]*par.ν_v*par.θ_ij[i][j])  + par.ϕj_t[i][t];
						exprObj1.addTerm(coeff, model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
					}
				}
			}
		}
		exprObj.multAdd(1, exprObj1);

		GRBLinExpr exprObj2 = new GRBLinExpr();
		for (int j = 0; j < par.S; j++) {
			for (int t = 1; t < par.H; t++) {
				exprObj2.addTerm(par.ηj_t[j][t], model.getVarByName("I_"+j+"_"+t));
			}
		}
		exprObj.multAdd(1, exprObj2);

		GRBLinExpr exprObj3 = new GRBLinExpr();
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {

				GRBLinExpr expr1 = new GRBLinExpr();


				GRBLinExpr expr2 = new GRBLinExpr();
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int i = 0; i < par.S; i++) {// Somatória de i
						expr2.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));						
					}	
				}

				GRBLinExpr expr3 = new GRBLinExpr();
				for (int v = 0; v < par.V; v++) {// Somatória de v	
					for (int k = 0; k < par.S; k++) {// Somatória de k
						expr3.addTerm(1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));						
					}	
				}
				expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+t));	
				expr1.multAdd(-1, expr2);
				expr1.multAdd(1, expr3);


				exprObj3.multAdd(par.μjt[j][t], expr1);
			}
		}
		exprObj.multAdd(1,exprObj3);		

		model.setObjective(exprObj);
		model.optimize();
		if(xz != null){
			model.write(path+"RPPzero.lp");
		}else{
			model.write(path+"RPP.lp");
		}
		// Solve model and capture solution information

		return model;
	}

	public String verifySolution(String problem,Parametros par, Sol_XZ xz ){
		String ret = "";
		
		if(problem == "CVα") ret += constraints.checkconstr_Zero(par, xz);
		if(problem != "IA_Pμk") ret += constraints.checkconstr2(par, xz);	
		if(problem != "IA_Pμk") ret += constraints.checkconstr3(par, xz);
		if(problem != "IA_Pμk") ret += constraints.checkconstr4(par, xz);
		if(problem != "CVLRμ" && problem != "CVRP_Pμ" && problem != "IA_Pμk") ret += constraints.checkconstr5(par, xz);
		if(problem != "IA_Pμk") ret += constraints.checkconstr6(par, xz);
		if(problem != "CVRP_Pμ") ret += constraints.checkconstr7(par, xz);
		if(problem != "CVLRμ" && problem != "CVRP_Pμ" && problem != "IA_Pμk" && problem != "CVα") ret += constraints.checkconstr8(par, xz);
		if(problem != "CVRP_Pμ") ret += constraints.checkconstr9(par, xz);
		if(problem != "IA_Pμk") ret += constraints.checkconstr10(par, xz);
		if(problem != "CVRP_Pμ" && problem != "CV") ret += constraints.checkconstr11(par, xz);
//		if(problem != "CV" && problem != "CVα" && problem != "IA_Pμk" ) ret += constraints.checkconstr16(par, xz);
		return ret;
	}
}
