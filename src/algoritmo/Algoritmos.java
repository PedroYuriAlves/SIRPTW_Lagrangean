package algoritmo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.sql.rowset.spi.TransactionalWriter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.python.modules.math;

import gurobi.*;
import instancias.Parametros;
import instancias.Sol_XZ;
import Principal.*;
import Simullated.*;
import SATW.*;

public class Algoritmos {

	Boolean print;

	public Sol_XZ algoritmo1(GRBEnv env, Parametros inst, Sol_XZ XZ_0, Boolean p, Random rand)
			throws GRBException, ScriptException {

		this.print = p;
		// Step 0. (Initialization):
		double LB = 0;
		double UB = Double.MAX_VALUE;
		String checkSolution = "";
		// GRBModel XZ_Best;
		Sol_XZ VarXZ_Best;

		int k = 1;	
		int improveC_LRk = 0;
		int kMax = 1000;
		int MaxInproveC_LRk = 30;
int iteracoes = 0;
//		double ω = rand.nextDouble();
		 double ω = 2;
		double[][] μjt = new double[inst.S][inst.H];
		for (int j = 0; j < inst.S; j++) {
			for (int t = 0; t < inst.H; t++) {
				μjt[j][t] = rand.nextDouble();
				// μjt[j][t] = -0.5;
//				 μjt[j][t] = 0;
				// μjt[j][t] = μ;
			}
		}
		inst.μjt = (μjt);

		// Step 1. (Computing the first UB):
		Problems prob = new Problems();
		VarXZ_Best = new Sol_XZ();
		VarXZ_Best = translateGurobi(prob.CVα(inst, env, XZ_0), inst);
		checkSolution = prob.verifySolution("CVα", inst, VarXZ_Best);
		if (checkSolution.length() > 0) {
			System.out.println("Erro1" + checkSolution);
		}
		UB = VarXZ_Best.custo;

		double lastC_LRk = 0;
		do {
			System.gc();
			// Step 2. (Computing the lower bound):
			Sol_XZ Ck_IAP = new Sol_XZ();
			Ck_IAP = translateGurobi(prob.IA_Pμk(inst, env, null), inst);
			checkSolution = prob.verifySolution("IA_Pμk", inst, Ck_IAP);
			if (checkSolution.length() > 0) {
				System.out.println("Erro2" + checkSolution);
			}
			Sol_XZ Ck_RTP = new Sol_XZ();
			Ck_RTP = translateGurobi(prob.CVRP_Pμ(inst, env, null), inst);
			checkSolution = prob.verifySolution("CVRP_Pμ", inst, Ck_RTP);
			if (checkSolution.length() > 0) {
				System.out.println("Erro3" + checkSolution);
			}
			/*
			 * Sol_XZ Ck_LRu = new Sol_XZ(); Ck_LRu =
			 * translateGurobi(prob.CVLRμ( inst, env, null),inst); checkSolution
			 * = prob.verifySolution("CVLRμ", inst, Ck_LRu);
			 * if(checkSolution.length() > 0){
			 * System.out.println("Erro3"+checkSolution); }
			 */
			Double Ck_LR = Ck_IAP.custo + Ck_RTP.custo;// get(GRB.DoubleAttr.ObjVal);

			if (Ck_LR > lastC_LRk) {
				improveC_LRk = 0;
				lastC_LRk = Ck_LR;
			} else {
				improveC_LRk++;
			}

			if (Ck_LR > LB) {
				LB = Ck_LR;
				VarXZ_Best.LB = (LB);
			} else {
				ω = ω / 2;
			}

			// Step 3. (Computing the upper bound):
			boolean execAlg2 = true;
			for (int j = 0; j < Ck_IAP.z.qjt.length; j++) {
				for (int t = 0; t < Ck_IAP.z.qjt[j].length; t++) {
					if (Ck_IAP.z.qjt[j][t] > inst.kv) {
						execAlg2 = false;
						break;
					}
				}
				if (!execAlg2) {
					break;
				}
			}
			if (execAlg2) {
				Sol_XZ XZ_k = new Sol_XZ();
				switch (inst.alg) {
				case 2:
					XZ_k = algoritmo2_Centroid(Ck_IAP, inst, XZ_0, UB);
					break;
				case 3:
					XZ_k = algoritmo2_Simullated_Annealing(Ck_IAP, inst, XZ_0, UB, rand);
					break;
				case 4:
					XZ_k = algoritmo2_MonteCarlo(Ck_IAP, inst, XZ_0, UB, rand);
					break;
				case 5:
					XZ_k = algoritmo2_Simullated_AnnealingTW(Ck_IAP, inst, XZ_0, UB, rand);
					break;
				default:
					XZ_k = algoritmo2(Ck_IAP, inst, XZ_0, UB);
					break;
				}

				// XZ_k = algoritmo3(UB, XZ_k, inst);
				Double custo_K = XZ_k.custo;
				XZ_k.LB = (LB);
				iteracoes++;
			
				checkSolution = prob.verifySolution("CVLRμ", inst, XZ_k);
				if (checkSolution.length() > 0) {
					System.out.println("Erro4" + checkSolution);
				}
				if (custo_K < UB) {
					UB = custo_K;
					VarXZ_Best = new Sol_XZ();
					VarXZ_Best = XZ_k;
					VarXZ_Best.UB = (UB);
					VarXZ_Best.LB = (LB);
				}
			}
			if (print) {
				System.out.println();
				System.out.println("Step 4. (Updating μ):");
				System.out.println();
			}
			double[][] gjt = new double[inst.S][inst.H];

			for (int j = 1; j < inst.S; j++) {// Para cada j
				for (int t = 1; t < inst.H; t++) { // Para cada t

					double sum1 = 0;
					for (int i = 0; i < inst.S; i++) {// Para cada i
						for (int v = 0; v < inst.V; v++) {// Para cada v
							sum1 += Ck_RTP.z.Qijt_v[i][j][t][v];
						}
					}
					double sum2 = 0;
					for (int ki = 0; ki < inst.S; ki++) {// Para cada ki
						for (int v = 0; v < inst.V; v++) {// Para cada v
							sum2 += Ck_RTP.z.Qijt_v[j][ki][t][v];
						}
					}
					// gjt[j][t] = (inst.d_jt[j][t]*inst.τt) - sum1+ sum2;
					gjt[j][t] = Ck_IAP.z.qjt[j][t] - sum1 + sum2;
				}
			}
			double gk = Norma_euclidiana(gjt);

			double sk = ω * (UB - Ck_LR) / (Math.pow(gk, 2));

			double[][] μjtK = new double[inst.S][inst.H];
			for (int j = 1; j < inst.S; j++) {
				for (int t = 1; t < inst.H; t++) {
					// if(Double.isInfinite(sk))
					// sk = 0;

					μjtK[j][t] = (inst.μjt[j][t] + (sk * gjt[j][t]));
					// μjtK[j][t] = Math.max(0,(
					// inst.μjt[j][t]+(sk*gjt[j][t])));
					// if(μjtK[j][t]>2)μjtK[j][t] = 2;
				}
			}

			inst.μjt = μjtK;

			// Step 5. (Stopping rule):
			if (k < kMax && improveC_LRk < MaxInproveC_LRk) {
				k++;
				continue;
			} else {
				int xx = 0;
				break;
			}
		} while (true);
		VarXZ_Best.msg = ("Terminou por:" + (k >= kMax ? "k" : "C_LRk") + "|k=" + k + ";C_LRk" + improveC_LRk);
		VarXZ_Best.k = k;
		VarXZ_Best.C_LRk = improveC_LRk;
		VarXZ_Best.LB = (LB);
		return VarXZ_Best;
	}

	public boolean validaTour(Tour c, Parametros inst) {
		if (c.totalcap > inst.kv) {
			return false;
		}
		if (c.travelTime > inst.τt) {
			return false;
		}

		return true;
	}

	public Sol_XZ algoritmo2(Sol_XZ XZ_Best, Parametros inst, Sol_XZ var, Double UB) throws GRBException {
		if (print) {
			System.out.println();
			System.out.println("Algorithm 2: (The Lagrangian heuristic algorithm for MP-DAIRPα)");
			System.out.println();
		}
		List<Route> lRouteT = new ArrayList<>();
		for (int t = 1; t < inst.H; t++) {

			List<Integer> SRt = new ArrayList<Integer>();
			for (int j = 1; j < inst.S; j++) {
				if (XZ_Best.z.qjt[j][t] > 0) {
					SRt.add(j);
				}
			}

			if (SRt.size() > 0) {
				if (print) {
					System.out.println();
					System.out.println("A2 - Step 0. (Initialization):");
					System.out.println();
				}
				Route tempRoute = new Route();
				tempRoute.v = 0;
				for (Integer j : SRt) {
					if (inst.θ_ij[0][j] + inst.θ_ij[j][0] <= inst.τt) {
						Tour tour = new Tour();
						tour.retails.add(j);
						tour.totalcap = XZ_Best.z.qjt[j][t];
						tempRoute.ltour.add(tour);
					} else {
						System.out.println();
						System.out.println("ERROOOOOOO!");
						System.out.println();
						break;
					}
				}

				Route L_star = tempRoute;
				L_star.t = t;
				Double bestSV = 0D;
				Double bestSV_Ant = 0D;
				do {
					List<Tour> singleList = L_star.ltour;
					bestSV_Ant = bestSV;
					bestSV = 0D;
					if (print) {
						System.out.println();
						System.out.println("A2 - Step 1. (Improvement Step):");
						System.out.println();
					}
					for (Tour C : singleList) {
						C.CV = custoCV(XZ_Best, L_star, C, inst, t);

					}

					for (int i = 0; i < singleList.size() - 1; i++) {
						for (int j = i + 1; j < singleList.size(); j++) {
							Tour C_Plus = combine(singleList.get(i), singleList.get(j), XZ_Best, t);
							C_Plus = TSP1(inst, C_Plus);
							C_Plus.CV = custoCV(XZ_Best, L_star, C_Plus, inst, t);
							// OK
							if (!validaTour(C_Plus, inst)) {
								C_Plus = new Tour();
							} else {
								if (C_Plus.CV < singleList.get(i).CV + singleList.get(j).CV) {
									Route L_new = new Route();
									L_new.v = L_star.v;
									L_new.t = t;
									L_new.ltour = new ArrayList<>();
									L_new.ltour.addAll(singleList.subList(0, i));
									L_new.ltour.addAll(singleList.subList(i + 1, j));
									L_new.ltour.addAll(singleList.subList(j + 1, singleList.size()));
									L_new.ltour.add(C_Plus);
									Double SV = singleList.get(i).CV + singleList.get(j).CV - C_Plus.CV;
									if (SV > bestSV) {
										L_star = L_new;
										bestSV = SV;
									}
								}
							}
						}
					}
				} while (bestSV > 0);

				if (print) {
					System.out.println();
					System.out.println("A2 - Step 2. (Stopping rule):");
					System.out.println();
				}

				Double Tmin = 0D;
				for (Tour c : L_star.ltour) {
					int ant = 0;
					c.travelTime = 0;
					c.totalcap = 0;
					c.CV = custoCV(XZ_Best, L_star, c, inst, t);
					for (int i = 0; i < c.retails.size(); i++) {
						Tmin += inst.θ_ij[ant][c.retails.get(i)];
						c.travelTime += inst.θ_ij[ant][c.retails.get(i)];
						c.totalcap += XZ_Best.z.qjt[c.retails.get(i)][t];
						ant = c.retails.get(i);

					}
					Tmin += inst.θ_ij[ant][0];
					c.travelTime += inst.θ_ij[ant][0];
					L_star.cost += c.CV;
				}
				L_star.nVeic = 1;
				if (Tmin > inst.τt) {
					L_star.nVeic = (int) (Math.floor(Tmin / inst.τt) + 1);
				}

				L_star.cost += L_star.nVeic * inst.ψv[L_star.v];

				lRouteT.add(L_star);
			}
		}
		Sol_XZ xzk = translate(lRouteT, XZ_Best, inst);
		/*String check = new Problems().verifySolution("IA_Pμk", inst, xzk);
		if (check.length() > 0) {
			System.out.println(check);
		}
		check = new Problems().verifySolution("CVRP_Pμ", inst, xzk);
		if (check.length() > 0) {
			System.out.println(check);
		}
		check = new Problems().verifySolution("CVLRμ", inst, xzk);
		if (check.length() > 0) {
			System.out.println(check);
		}*/
		xzk.custo = calculacustoIA(xzk, inst);
		xzk.custo += calculacustoRP(xzk, inst);
		xzk.UB = (xzk.custo);
		// xzk.LB = (LB);
		// return translate(lRouteT,XZ_Best,inst);
		return xzk;

	}

	int iteracao = 0;

	public Sol_XZ algoritmo2_Centroid(Sol_XZ XZ_Best, Parametros inst, Sol_XZ var, Double UB) throws GRBException {
		if (print) {
			System.out.println();
			System.out.println("Algorithm 2: (The Lagrangian heuristic algorithm for MP-DAIRPα)");
			System.out.println();
		}
		int iteracoes = 0;
		List<Route> lRouteT = new ArrayList<>();
		for (int t = 1; t < inst.H; t++) {

			List<Integer> SRt = new ArrayList<Integer>();
			for (int j = 1; j < inst.S; j++) {
				if (XZ_Best.z.qjt[j][t] > 0) {
					SRt.add(j);
				}
			}

			if (SRt.size() > 0) {
				Route L_star = new Route();
				L_star.t = t;
				try {
					if (iteracao == 10) {
						int x = 0;
					}
					// Preciso converter essa função
					Grafo.inicializarGrafo(SRt, XZ_Best, inst, t);
					Caminhos.inicializarListaSolucao();
					long tempoAnterior = System.currentTimeMillis();

					int maxIteracoes = 20;
					Clusters.centroidVRP(maxIteracoes);
					LinkedList<LinkedList<Integer>> clusters = Caminhos.solucao;

					long tempoAtual = System.currentTimeMillis();
					long delta = tempoAtual - tempoAnterior;
					// System.out.println("Tempo "+delta);

					for (LinkedList<Integer> cl : clusters) {
						Tour c = new Tour();
						c.CV = 0;
						c.retails = new ArrayList<Integer>();
						for (Integer integer : cl) {
							if (integer != 0) {
								c.retails.add(Grafo.vertices[integer].getId());
							}
						}
						L_star.ltour.add(c);
					}

					Double Tmin = 0D;
					for (Tour c : L_star.ltour) {
						int ant = 0;
						c.travelTime = 0;
						c.totalcap = 0;
						c.CV = custoCV(XZ_Best, L_star, c, inst, t);
						for (int i = 0; i < c.retails.size(); i++) {
							Tmin += inst.θ_ij[ant][c.retails.get(i)];
							c.travelTime += inst.θ_ij[ant][c.retails.get(i)];
							c.totalcap += XZ_Best.z.qjt[c.retails.get(i)][t];
							ant = c.retails.get(i);

						}
					
						if(c.totalcap > 60){
							int x =0;
						}
						Tmin += inst.θ_ij[ant][0];
						c.travelTime += inst.θ_ij[ant][0];
						L_star.cost += c.CV;
					}
					L_star.nVeic = 1;
					if (Tmin > inst.τt) {
						L_star.nVeic = (int) (Math.floor(Tmin / inst.τt) + 1);
					}

					L_star.cost += L_star.nVeic * inst.ψv[L_star.v];

					lRouteT.add(L_star);
					iteracoes++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		iteracao++;
		Sol_XZ xzk = translate(lRouteT, XZ_Best, inst);

		xzk.custo = calculacustoIA(xzk, inst);
		xzk.custo += calculacustoRP(xzk, inst);
		xzk.UB = (xzk.custo);
		// xzk.LB = (LB);
		// return translate(lRouteT,XZ_Best,inst);
		return xzk;

	}
	public Sol_XZ algoritmo2_Simullated_Annealing(Sol_XZ XZ_Best, Parametros inst, Sol_XZ var, Double UB, Random rand)
			throws GRBException {
		if (print) {
			System.out.println();
			System.out.println("Algorithm 2: (The Lagrangian heuristic algorithm for MP-DAIRPα)");
			System.out.println();
		}

		List<Route> lRouteT = new ArrayList<>();
		for (int t = 1; t < inst.H; t++) {

			List<Integer> SRt = new ArrayList<Integer>();
			for (int j = 1; j < inst.S; j++) {
				if (XZ_Best.z.qjt[j][t] > 0) {
					SRt.add(j);
				}
			}

			if (SRt.size() > 0) {
				Route L_star = new Route();
				L_star.t = t;
				/////////////////////////////////////////////////////////////////////////////
				try {
					/*
					 * Util z = new Util(); z.inicializa(SRt,XZ_Best,inst,t);
					 * Simullated_Annealing s = new
					 * Simullated_Annealing(z.clients); s.Annealing_CVRP(rand);
					 */
					Util z = new Util();
					if (iteracao == 3) {
						int x = 0;
					}
					z.inicializa(SRt, XZ_Best, inst, t);
					SimullatedAnnealing_2 s = new SimullatedAnnealing_2(z.clients);
					s.Annealing_CVRP(rand);
					// z.imprime_rotas(s.melhor_sol);

					for (ArrayList<Client> cl : s.melhor_sol) {
						Tour c = new Tour();
						c.CV = 0;
						c.retails = new ArrayList<Integer>();
						for (Client integer : cl) {
							if (integer.id != "0") {
								c.retails.add(Integer.parseInt(integer.idSIRP));
							}
						}
						L_star.ltour.add(c);
					}
					/////////////////////////////////////////////////////////////////////////////

					Double Tmin = 0D;
					for (Tour c : L_star.ltour) {
						int ant = 0;
						c.travelTime = 0;
						c.totalcap = 0;
						c.CV = custoCV(XZ_Best, L_star, c, inst, t);
						for (int i = 0; i < c.retails.size(); i++) {
							Tmin += inst.θ_ij[ant][c.retails.get(i)];
							c.travelTime += inst.θ_ij[ant][c.retails.get(i)];
							c.totalcap += XZ_Best.z.qjt[c.retails.get(i)][t];
							ant = c.retails.get(i);

						}
						if(c.totalcap > 60){
						 int x = 0;
						}
						Tmin += inst.θ_ij[ant][0];
						c.travelTime += inst.θ_ij[ant][0];
						L_star.cost += c.CV;
					}
					L_star.nVeic = 1;
					if (Tmin > inst.τt) {
						L_star.nVeic = (int) (Math.floor(Tmin / inst.τt) + 1);
					}

					L_star.cost += L_star.nVeic * inst.ψv[L_star.v];

					lRouteT.add(L_star);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		iteracao++;
		Sol_XZ xzk = translate(lRouteT, XZ_Best, inst);

		xzk.custo = calculacustoIA(xzk, inst);
		xzk.custo += calculacustoRP(xzk, inst);
		xzk.UB = (xzk.custo);
		// xzk.LB = (LB);
		// return translate(lRouteT,XZ_Best,inst);
		return xzk;

	}

	public Sol_XZ algoritmo2_Simullated_AnnealingTW(Sol_XZ XZ_Best, Parametros inst, Sol_XZ var, Double UB, Random rand)
			throws GRBException {
		if (print) {
			System.out.println();
			System.out.println("Algorithm 2: (The Lagrangian heuristic algorithm for MP-DAIRPα)");
			System.out.println();
		}

		List<Route> lRouteT = new ArrayList<>();
		for (int t = 1; t < inst.H; t++) {

			List<Integer> SRt = new ArrayList<Integer>();
			for (int j = 1; j < inst.S; j++) {
				if (XZ_Best.z.qjt[j][t] > 0) {
					SRt.add(j);
				}
			}

			if (SRt.size() > 0) {
				Route L_star = new Route();
				L_star.t = t;
				/////////////////////////////////////////////////////////////////////////////
				try {
					/*
					 * Util z = new Util(); z.inicializa(SRt,XZ_Best,inst,t);
					 * Simullated_Annealing s = new
					 * Simullated_Annealing(z.clients); s.Annealing_CVRP(rand);
					 */
					Util z = new Util();
				
					z.inicializa(SRt, XZ_Best, inst, t);
					SimullatedAnnealing_TW s = new SimullatedAnnealing_TW(z.clients,rand);
					s.SimullatedAnnealing_TW_CVRP(rand,inst);
					// z.imprime_rotas(s.melhor_sol);

					for (ArrayList<Client> cl : s.Sb) {
						Tour c = new Tour();
						c.CV = 0;
						c.retails = new ArrayList<Integer>();
						c.ha = new double[cl.size()];
						c.hd = new double[cl.size()];
						
						int cindex = 0;
						for (Client integer : cl) {
							if (integer.id != "0") {
								c.retails.add(Integer.parseInt(integer.idSIRP));
								c.ha[cindex] = integer.getTa()+integer.getTw();
								c.hd[cindex] = integer.getTd();
								cindex++; 
							}
						}
						L_star.ltour.add(c);
					}
					/////////////////////////////////////////////////////////////////////////////

					Double Tmin = 0D;
					for (Tour c : L_star.ltour) {
						int ant = 0;
						c.travelTime = 0;
						c.totalcap = 0;
						c.CV = custoCV(XZ_Best, L_star, c, inst, t);
						for (int i = 0; i < c.retails.size(); i++) {
							Tmin += inst.θ_ij[ant][c.retails.get(i)];
							c.travelTime += inst.θ_ij[ant][c.retails.get(i)];
							c.totalcap += XZ_Best.z.qjt[c.retails.get(i)][t];
							ant = c.retails.get(i);

						}
						if(c.totalcap > 60){
						 int x = 0;
						}
						Tmin += inst.θ_ij[ant][0];
						c.travelTime += inst.θ_ij[ant][0];
						L_star.cost += c.CV;
					}
					L_star.nVeic = 1;
					if (Tmin > inst.τt) {
						L_star.nVeic = (int) (Math.floor(Tmin / inst.τt) + 1);
					}

					L_star.cost += L_star.nVeic * inst.ψv[L_star.v];

					lRouteT.add(L_star);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		iteracao++;
		Sol_XZ xzk = translate(lRouteT, XZ_Best, inst);

		xzk.custo = calculacustoIA(xzk, inst);
		xzk.custo += calculacustoRP(xzk, inst);
		xzk.UB = (xzk.custo);
		// xzk.LB = (LB);
		// return translate(lRouteT,XZ_Best,inst);
		return xzk;

	}

	public Sol_XZ algoritmo2_MonteCarlo(Sol_XZ XZ_Best, Parametros inst, Sol_XZ var, Double UB, Random rand)
			throws GRBException, ScriptException {
		if (print) {
			System.out.println();
			System.out.println("Algorithm 2: (The Lagrangian heuristic algorithm for MP-DAIRPα)");
			System.out.println();
		}

		List<Route> lRouteT = new ArrayList<>();
		for (int t = 1; t < inst.H; t++) {

			List<Integer> SRt = new ArrayList<Integer>();
			for (int j = 1; j < inst.S; j++) {
				if (XZ_Best.z.qjt[j][t] > 0) {
					SRt.add(j);
				}
			}

			if (SRt.size() > 0) {
				Route L_star = new Route();
				L_star.t = t;
				/////////////////////////////////////////////////////////////////////////////
				// try
				{
					String path = "C:/Users/Krespo/Downloads/monte-carlo-cvrp-master/input/Augerat/";
					String file = createArchiveInstance(path, SRt, XZ_Best, inst, t);

					StringWriter writer = new StringWriter(); // ouput will be
					// stored here
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptContext context = new SimpleScriptContext();
					// FileReader fr = new
					// FileReader("C:/Users/Krespo/Downloads/monte-carlo-cvrp-master/numbers.py");
					// FileReader fr = new
					// FileReader("C:/Users/Krespo/Downloads/monte-carlo-cvrp-master/main.py");
					context.setWriter(writer); // configures output redirection
					ScriptEngine engine = manager.getEngineByName("python");

					String script = "import subprocess\n"
							+ "p = subprocess.Popen(['python', 'C:/Users/Krespo/Downloads/monte-carlo-cvrp-master/run.py'"
							+ ", '" + path + file.split(";")[0] + "', '" + file.split(";")[1]
									+ "'], stdout=subprocess.PIPE, shell=True)\n" + "(output, err) = p.communicate()\n"
									+ "p_status = p.wait()\n" + "print '' + output +''";

					Object obj = engine.eval(script, context);

					// prints: 1
					// System.out.println(writer.toString());

					String result = writer.toString();
					result = result.split(";")[1];
					String[] results = result.split("@");

					int x = 0;
					for (String cl : results) {
						if (cl.contains("Total cost"))
							continue;
						cl = cl.split(":")[0];
						cl = cl.replace("[", "").replace("]", "");

						String[] cliList = cl.split(",");
						Tour c = new Tour();
						c.CV = 0;
						c.retails = new ArrayList<Integer>();
						for (String integer : cliList) {
							if (integer.trim() != "0") {
								c.retails.add(Integer.parseInt(integer.trim()));
							}
						}
						L_star.ltour.add(c);
					}
					/////////////////////////////////////////////////////////////////////////////

					Double Tmin = 0D;
					for (Tour c : L_star.ltour) {
						int ant = 0;
						c.travelTime = 0;
						c.totalcap = 0;
						c.CV = custoCV(XZ_Best, L_star, c, inst, t);
						for (int i = 0; i < c.retails.size(); i++) {
							Tmin += inst.θ_ij[ant][c.retails.get(i)];
							c.travelTime += inst.θ_ij[ant][c.retails.get(i)];
							c.totalcap += XZ_Best.z.qjt[c.retails.get(i)][t];
							ant = c.retails.get(i);

						}
						Tmin += inst.θ_ij[ant][0];
						c.travelTime += inst.θ_ij[ant][0];
						L_star.cost += c.CV;
					}
					L_star.nVeic = 1;
					if (Tmin > inst.τt) {
						L_star.nVeic = (int) (Math.floor(Tmin / inst.τt) + 1);
					}

					L_star.cost += L_star.nVeic * inst.ψv[L_star.v];

					lRouteT.add(L_star);

				}
				// catch(Exception e)
				{
					// e.printStackTrace();
				}
			}
		}
		Sol_XZ xzk = translate(lRouteT, XZ_Best, inst);

		xzk.custo = calculacustoIA(xzk, inst);
		xzk.custo += calculacustoRP(xzk, inst);
		xzk.UB = (xzk.custo);
		// xzk.LB = (LB);
		// return translate(lRouteT,XZ_Best,inst);
		return xzk;

	}

	private static void imprimirSolucao() {
		int rota = 1;
		int custoAtual;
		int demandaAtual;
		LinkedList<LinkedList<Integer>> clusters = Caminhos.solucao;

		for (LinkedList<Integer> clusterAtual : clusters) {
			if (clusterAtual.size() <= 2)
				continue;

			System.out.print("Rota#" + rota + ": ");
			demandaAtual = 0;

			for (Integer estado : clusterAtual) {
				System.out.print(Grafo.vertices[estado].getNumero() + " ");
				demandaAtual += Grafo.vertices[estado].getDemanda();
			}
			custoAtual = Caminhos.distanciaTotalCluster(clusterAtual);
			System.out.print("custo: " + custoAtual + " demanda atendida: " + demandaAtual);
			System.out.println();
			rota++;
		}
		System.out.println("Custo " + Caminhos.distanciaTotalRotas());
	}

	public static Sol_XZ translate(List<Route> lroute, Sol_XZ XZ_IA, Parametros par) {
		Sol_XZ ret = new Sol_XZ(par.S, par.H, par.S);

		int[][][][] Xijt_v = new int[par.S][par.S][par.H][par.V];
		int[][] Yt_v = new int[par.H][par.V];
		double[][] Ijt = new double[par.S][par.H];
		double[][][][] Qijt_v = new double[par.S][par.S][par.H][par.V];
		double[][] qjt = new double[par.S][par.H];
		
		double[][][] hjtv = new double[par.S][par.H][par.V];
		double[][][] hd_jtv = new double[par.S][par.H][par.V];

		qjt = XZ_IA.z.qjt;
		Ijt = XZ_IA.z.Ijt;

		for (Route r : lroute) {
			
			int v = 0;
			double time = 0;
			for (Tour tour : r.ltour) {
				if(tour.retails.size() >0){
					int i = 0;
					if (time > par.τt) {
						time = 0;
						v++;
					}
					Double QtdVeic = 0D;
					for (int rq = 0; rq < tour.retails.size(); rq++) {
						QtdVeic += qjt[tour.retails.get(rq)][r.t];
					}
					if (QtdVeic > 60) {
						int x = 0;
						x++;
					}
					for (int j = 0; j < tour.retails.size(); j++) {
						Xijt_v[i][tour.retails.get(j)][r.t][v] = 1;
						
						hjtv[tour.retails.get(j)][r.t][v] = tour.ha[j];
						hd_jtv[tour.retails.get(j)][r.t][v] = tour.hd[j];
						
						Yt_v[r.t][v] = 1;
						Qijt_v[i][tour.retails.get(j)][r.t][v] = QtdVeic;
						QtdVeic -= qjt[tour.retails.get(j)][r.t];
						i = tour.retails.get(j);
					}
					Xijt_v[tour.retails.get(tour.retails.size() - 1)][0][r.t][v] = 1;
					Yt_v[r.t][v] = 1;
					time += tour.travelTime;

					tour.CV = custoCV(XZ_IA, r, tour, par, r.t);
				}
			}
			ret.custo += r.cost;
		}

		ret.x.Xijt_v = (Xijt_v);
		ret.x.Yt_v = (Yt_v);
		ret.z.Qijt_v = (Qijt_v);
		ret.z.Ijt = (Ijt);
		ret.z.qjt = (qjt);
		
		ret.z.hjtv = (hjtv);
		ret.z.hd_jtv = (hd_jtv);
		return ret;
	}

	public Sol_XZ algoritmo3(Double UB, Sol_XZ XZ_best, Parametros inst, Random rand)
			throws GRBException, ScriptException {
		int k = 0;
		Sol_XZ XZ_k = new Sol_XZ(inst.S, inst.H, inst.V);
		XZ_k.z.qjt = (XZ_best.z.qjt);
		XZ_k.z.Qijt_v = (XZ_best.z.Qijt_v);
		XZ_k.x.Yt_v = (XZ_best.x.Yt_v);
		XZ_k.x.Xijt_v = (XZ_best.x.Xijt_v);
		XZ_k.z.Ijt = (XZ_best.z.Ijt);
		// XZ_k.setI_j0(XZ_best.getI_j0());

		ArrayList<Integer> W = new ArrayList<>();
		for (int i = 1; i < inst.S; i++) {
			int c = 0;
			for (int t = 1; t < inst.H; t++) {
				if (XZ_best.z.qjt[i][t] > 0) {
					c++;
				}
			}
			if (c > 1) {
				W.add(i);
			}
		}

		for (int index = 0; index < W.size(); index++) {
			int j = W.get(index);
			W.remove(index);
			index--;

			for (int m = inst.H - 1; m > 0; m--) {
				if (XZ_best.z.qjt[j][m] > 0) { // for q*jm > 0

					if (XZ_best.z.qjt[j][m] <= inst.kv) {// ≤ κ
						XZ_k.z.qjt[j][m] = XZ_best.z.qjt[j][m];

						for (int n = m + 1; n < inst.H; n++) {
							if (XZ_best.z.qjt[j][n] > 0) {// for q*jn > 0

								if (XZ_k.z.qjt[j][m] + XZ_best.z.qjt[j][n] <= inst.kv) {// ≤
									// κ

									XZ_k.z.qjt[j][m] += XZ_best.z.qjt[j][n];
									XZ_k.z.qjt[j][n] = 0;

									XZ_k.z.Ijt[j][m] += XZ_best.z.qjt[j][n];
									XZ_k.z.Ijt[j][n] -= XZ_best.z.qjt[j][n];

								}
							}
						}
					}

					// XZ_k = algoritmo2(XZ_k, inst, new Sol_XZ(),UB);
					// XZ_k = algoritmo2_Simullated_Annealing(XZ_k, inst, new
					// Sol_XZ(),UB);
					// XZ_k = algoritmo2_Centroid(XZ_k, inst, new Sol_XZ(),UB);
					switch (inst.alg) {
					case 2:
						XZ_k = algoritmo2_Centroid(XZ_k, inst, new Sol_XZ(), UB);
						break;
					case 3:
						XZ_k = algoritmo2_Simullated_Annealing(XZ_k, inst, new Sol_XZ(), UB, rand);
						break;
					case 4:
						XZ_k = algoritmo2_MonteCarlo(XZ_k, inst, new Sol_XZ(), UB, rand);
						break;
					default:
						XZ_k = algoritmo2(XZ_k, inst, new Sol_XZ(), UB);
						break;
					}

					if (UB > XZ_k.custo) {
						UB = XZ_k.custo;
						XZ_best = XZ_k;
					}
				}
			}

			// 1-b
			int count = 0;
			for (int t = 1; t < inst.H; t++) {
				if (XZ_best.z.qjt[j][t] > 0) {
					count++;
				}
			}

			if (count > 1) {

				XZ_k = new Sol_XZ(inst.S, inst.H, inst.V);
				XZ_k.z.qjt = (XZ_best.z.qjt);
				XZ_k.z.Qijt_v = (XZ_best.z.Qijt_v);
				XZ_k.x.Yt_v = (XZ_best.x.Yt_v);
				XZ_k.x.Xijt_v = (XZ_best.x.Xijt_v);
				XZ_k.z.Ijt = (XZ_best.z.Ijt);
				// XZ_k.setI_j0(XZ_best.getI_j0());

				for (int n = 2; n < inst.H; n++) {
					if (XZ_best.z.qjt[j][n] > 0) {
						if (XZ_best.z.qjt[j][n] <= inst.kv) {// ≤ κ
							XZ_k.z.qjt[j][n] = XZ_best.z.qjt[j][n];
							XZ_k.z.Ijt[j][0] = XZ_best.z.Ijt[j][0];

							for (int m = 1; m < n; m++) {
								if (XZ_best.z.qjt[j][m] > 0) {
									if (XZ_k.z.qjt[j][n] + XZ_best.z.qjt[j][m] <= inst.kv) {// ≤
										// κ

										XZ_k.z.qjt[j][n] += XZ_best.z.qjt[j][m];
										XZ_k.z.Ijt[j][0] += XZ_best.z.qjt[j][m];
										XZ_k.z.qjt[j][m] = 0;

										XZ_k.z.Ijt[j][n] += XZ_best.z.qjt[j][m];
										XZ_k.z.Ijt[j][m] -= XZ_best.z.qjt[j][m];

									}

								}
							}
						}

						XZ_k = algoritmo2(XZ_k, inst, new Sol_XZ(), UB);
						// XZ_k = algoritmo2_Centroid(XZ_k, inst, new
						// Sol_XZ(),UB);

						if (UB > XZ_k.custo) {
							UB = XZ_k.custo;
							XZ_best = XZ_k;
						}
					}
				}
			}

			if (W.size() > 0) {
				k = k + 1;
			}
		}

		return XZ_best;
	}

	public double CV_Tour(Tour tour, Parametros inst, int t, int v) {
		double ret = Double.MAX_VALUE;
		if (tour.retails.size() > 0) {
			ret = 0;
			int idAnt = 0;
			for (Integer c : tour.retails) {
				ret += inst.δv[v] * inst.ν_v * inst.θ_ij[idAnt][c] + inst.ϕj_t[idAnt][t];
			}
			// ret+= inst.θ_ij[tour.lClientes.getLast().id][0];

			ret += inst.δv[v] * inst.ν_v * inst.θ_ij[tour.retails.get(tour.retails.size() - 1)][0]
					+ inst.ϕj_t[tour.retails.get(tour.retails.size() - 1)][t];
		}
		return ret;
	}

	public double CV_TourTime(Tour tour, Parametros inst) {
		double ret = Double.MAX_VALUE;
		if (tour.retails.size() > 0) {
			ret = 0;
			int idAnt = 0;
			for (Integer c : tour.retails) {
				ret += inst.θ_ij[idAnt][c];
			}
			ret += inst.θ_ij[tour.retails.get(tour.retails.size() - 1)][0];
		}
		return ret;
	}

	public double CV_custoSolucao(Parametros inst, Sol_XZ var) {

		double ret = 0;
		for (int t = 0; t < inst.H; t++) {
			for (int v = 0; v < inst.V; v++) {
				// exprObj.addTerm(inst.ψv[v], model.getVarByName("Y"+t+"_"+v));
				ret += (inst.ψv[v] * var.x.Yt_v[t][v]);

				for (int i = 0; i < inst.S; i++) {
					for (int j = 0; j < inst.S; j++) {
						Double coeff = (inst.δv[0] * inst.ν_v * inst.θ_ij[i][j]) + inst.ϕj_t[i][t];
						// exprObj.addTerm(coeff,
						// model.getVarByName("X"+i+j+t+"_"+v));
						ret += (coeff * var.x.Xijt_v[i][j][t][v]);
					}
				}
			}
		}

		for (int t = 0; t < inst.H; t++) {
			for (int j = 0; j < inst.S; j++) {
				ret += (inst.ηj_t[j][t] * var.z.Ijt[j][t]);
			}
		}

		return ret;
	}

	public static Sol_XZ translateGurobi(GRBModel model, Parametros par) throws GRBException {
		Sol_XZ ret = new Sol_XZ(par.S, par.H, par.V);

		int[][][][] Xijt_v = new int[par.S][par.S][par.H][par.V];
		int[][] Yt_v = new int[par.H][par.V];
		double[][] Ijt = new double[par.S][par.H];
		double[][][][] Qijt_v = new double[par.S][par.S][par.H][par.V];
		double[][] qjt = new double[par.S][par.H];
		double[][][] hjtv = new double [par.S][par.H][par.V];
		double[][][] hd_jtv = new double [par.S][par.H][par.V];

		for (int i = 0; i < par.S; i++) {
			for (int j = 0; j < par.S; j++) {
				for (int t = 0; t < par.H; t++) {
					if (model.getVarByName("q" + "_" + j + "_" + t) != null)
						qjt[j][t] = RoundTo2Decimals(model.getVarByName("q" + "_" + j + "_" + t).get(GRB.DoubleAttr.X));
					if (model.getVarByName("I" + "_" + j + "_" + t) != null)
						Ijt[j][t] = RoundTo2Decimals(model.getVarByName("I" + "_" + j + "_" + t).get(GRB.DoubleAttr.X));
					for (int v = 0; v < par.V; v++) {
						if (model.getVarByName("y" + "_" + t + "_" + v) != null)
							Yt_v[t][v] = (int) model.getVarByName("y" + "_" + t + "_" + v).get(GRB.DoubleAttr.X);
						if (model.getVarByName("x" + "_" + i + "_" + j + "_" + t + "_" + v) != null)
							Xijt_v[i][j][t][v] = (int) model.getVarByName("x" + "_" + i + "_" + j + "_" + t + "_" + v)
							.get(GRB.DoubleAttr.X);
						if (model.getVarByName("Q" + "_" + i + "_" + j + "_" + t + "_" + v) != null)
							Qijt_v[i][j][t][v] = RoundTo2Decimals(model
									.getVarByName("Q" + "_" + i + "_" + j + "_" + t + "_" + v).get(GRB.DoubleAttr.X));
						
						if (model.getVarByName("h" + "_" + j + "_" + t + "_" + v) != null)
							hjtv[j][t][v] = RoundTo2Decimals(model
									.getVarByName("h" + "_" + j + "_" + t + "_" + v).get(GRB.DoubleAttr.X));
						if (model.getVarByName("hd" + "_" + j + "_" + t + "_" + v) != null)
							hd_jtv[j][t][v] = RoundTo2Decimals(model
									.getVarByName("hd" + "_" + j + "_" + t + "_" + v).get(GRB.DoubleAttr.X));
					}
				}
			}
		}

		ret.x.Xijt_v = (Xijt_v);
		ret.x.Yt_v = (Yt_v);
		ret.z.Qijt_v = (Qijt_v);
		ret.z.Ijt = (Ijt);
		ret.z.qjt = (qjt);
		ret.z.hjtv = (hjtv);
		ret.z.hd_jtv = (hd_jtv);
		ret.custo = (model.get(GRB.DoubleAttr.ObjVal));

		return ret;
	}

	public static double RoundTo2Decimals(double val) {
		try {
			DecimalFormat df2 = new DecimalFormat("###.##");
			return Double.valueOf(df2.format(val).replaceAll(",", "."));
		} catch (Exception e) {
			return 0;
		}

	}

	public double frobenius_Norm(double gjt[][]) {
		double ret = 0;

		for (int j = 1; j < gjt.length; j++) {
			for (int t = 1; t < gjt[j].length; t++) {
				ret += Math.pow(gjt[j][t], 2);
				// ret += Math.pow(gjt[j][t],2);
			}
		}

		ret = Math.sqrt(ret);
		return ret;
	}

	public double Norma_euclidiana(double gjt[][]) {
		double ret = 0;

		for (int j = 1; j < gjt.length; j++) {
			for (int t = 1; t < gjt[j].length; t++) {
				ret += Math.pow(gjt[j][t], 2);
				// ret += Math.pow(gjt[j][t],2);
			}
		}

		ret = Math.sqrt(ret);
		return ret;
	}

	public static Tour TSP1(Parametros par, Tour tour) {
		Tour ret = new Tour();
		ret.travelTime = 0D;
		ret.totalcap = tour.totalcap;
		Stack<Integer> stack = new Stack<Integer>();
		if (tour.retails.size() > 0) {
			stack.push(0);
			int[] visitados = new int[tour.retails.size()];

			while (!stack.isEmpty()) {
				int atual = stack.peek();
				int dst = -1, idDest = -1;
				boolean minFlag = false;
				double min = Double.MAX_VALUE;

				for (int i = 0; i < tour.retails.size(); i++) {
					int c = tour.retails.get(i);
					if (visitados[i] == 0) {
						if (min > par.θ_ij[atual][c]) {
							min = par.θ_ij[atual][c];
							dst = c;
							minFlag = true;
							idDest = i;
						}
					}
				}
				if (minFlag) {
					visitados[idDest] = 1;
					stack.push(dst);

					ret.retails.add(dst);
					ret.travelTime += min;

					System.out.print(dst + "\t");
					minFlag = false;
					continue;
				}
				stack.pop();

			}

			ret.travelTime += par.θ_ij[ret.retails.get(ret.retails.size() - 1)][0];
		}

		return ret;
	}

	public static double custoCV(Sol_XZ XZ_IA, Route route, Tour tour, Parametros par, int t) {
		double ret = Double.MAX_VALUE;
		// ret = par.ψv[route.v];
		ret = 0;
		if (tour.retails.size() > 0) {
			// ret +=
			// (par.δv[route.v]*par.ν_v*par.θ_ij[0][tour.retails.get(0)]);
			int i = 0;
			for (Integer j : tour.retails) {
				ret += (par.δv[route.v] * par.ν_v * par.θ_ij[i][j] + par.ϕj_t[j][t]);
				tour.travelTime += par.θ_ij[i][j];
				i = j;

			}
			// Custo de volta
			ret += (par.δv[route.v] * par.ν_v * par.θ_ij[i][0]);
			tour.travelTime += par.θ_ij[i][0];

			for (Integer j : tour.retails) {
				ret += (par.ηj_t[j][t] + XZ_IA.z.Ijt[j][t]);
			}
		}
		return ret;
	}

	public static Tour combine(Tour ci, Tour cj, Sol_XZ xz, int t) {
		Tour ret = new Tour();
		ret.retails.addAll(ci.retails);
		ret.retails.addAll(cj.retails);

		ret.totalcap = cj.totalcap + ci.totalcap;
		return ret;
	}

	public double calculacustoRP(Sol_XZ rp, Parametros par) {
		GRBLinExpr exprObj = new GRBLinExpr();

		double ret = 0;
		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				ret += par.ψv[v] * rp.x.Yt_v[t][v];

				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v] * par.ν_v * par.θ_ij[i][j]) + par.ϕj_t[i][t];
						// exprObj.addTerm(coeff,
						// model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
						ret += coeff * rp.x.Xijt_v[i][j][t][v];
					}
				}
			}
		}

		double sum3 = 0;
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {
				GRBLinExpr expr1 = new GRBLinExpr();
				double sum1 = 0;
				// expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+t));
				for (int v = 0; v < par.V; v++) {// Somatória de v
					for (int i = 0; i < par.S; i++) {// Somatória de i
						// expr1.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));
						sum1 += rp.z.Qijt_v[i][j][t][v];
					}
				}
				double sum2 = 0;
				for (int v = 0; v < par.V; v++) {// Somatória de v
					for (int k = 0; k < par.S; k++) {// Somatória de k
						// expr1.addTerm(-1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));
						sum2 += rp.z.Qijt_v[j][k][t][v];
					}
				}

				sum3 += par.μjt[j][t] * (sum1 - sum2);
				// multAdd(par.μjt[j][t], expr1);
			}
		}

		ret -= sum3;
		return ret;
	}

	public double calculacustoIA(Sol_XZ ia, Parametros par) {
		GRBLinExpr exprObj = new GRBLinExpr();

		double ret = 0;
		double sum1 = 0;
		for (int t = 0; t < par.H; t++) {
			for (int j = 0; j < par.S; j++) {
				sum1 += par.ηj_t[j][t] * ia.z.Ijt[j][t];
			}
		}

		double sum2 = 0;
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {
				sum1 += par.μjt[j][t] * ia.z.qjt[j][t];
			}
		}
		ret = sum1 + sum2;
		return ret;
	}

	public double calculacustoCVLR(Sol_XZ cv, Parametros par) {
		GRBLinExpr exprObj = new GRBLinExpr();

		double ret = 0;
		for (int t = 1; t < par.H; t++) {
			for (int v = 0; v < par.V; v++) {
				ret += par.ψv[v] * cv.x.Yt_v[t][v];

				for (int i = 0; i < par.S; i++) {
					for (int j = 0; j < par.S; j++) {
						Double coeff = (par.δv[v] * par.ν_v * par.θ_ij[i][j]) + par.ϕj_t[i][t];
						// exprObj.addTerm(coeff,
						// model.getVarByName("x"+"_"+i+"_"+j+"_"+t+"_"+v));
						ret += coeff * cv.x.Xijt_v[i][j][t][v];
					}
				}
			}
		}

		double sum3 = 0;
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {
				GRBLinExpr expr1 = new GRBLinExpr();
				double sum1 = 0;
				// expr1.addTerm(1, model.getVarByName("q"+"_"+j+"_"+t));
				for (int v = 0; v < par.V; v++) {// Somatória de v
					for (int i = 0; i < par.S; i++) {// Somatória de i
						// expr1.addTerm(1,model.getVarByName("Q"+"_"+i+"_"+j+"_"+t+"_"+v));
						sum1 += cv.z.Qijt_v[i][j][t][v];
					}
				}
				double sum2 = 0;
				for (int v = 0; v < par.V; v++) {// Somatória de v
					for (int k = 0; k < par.S; k++) {// Somatória de k
						// expr1.addTerm(-1,model.getVarByName("Q"+"_"+j+"_"+k+"_"+t+"_"+v));
						sum2 += cv.z.Qijt_v[j][k][t][v];
					}
				}

				sum3 += par.μjt[j][t] * (sum1 - sum2);
				// multAdd(par.μjt[j][t], expr1);
			}
		}

		// -----------------------------------------------------

		// double ret = 0;
		double sum1 = 0;
		for (int t = 0; t < par.H; t++) {
			for (int j = 0; j < par.S; j++) {
				sum1 += par.ηj_t[j][t] * cv.z.Ijt[j][t];
			}
		}

		double sum2 = 0;
		for (int t = 1; t < par.H; t++) {
			for (int j = 1; j < par.S; j++) {
				sum1 += par.μjt[j][t] * cv.z.qjt[j][t];
			}
		}
		ret = sum1 + sum2;
		return ret;
	}

	public String createArchiveInstance(String path, List<Integer> SRt, Sol_XZ XZ_Best, Parametros par, int t) {
		String ret = "";

		// ?????????????????????????????????????????
		int maxTaur_ij = 1;
		int sumT = 0;

		int sumN = 0;
		for (int i = 0; i < SRt.size(); i++) {
			sumN += XZ_Best.z.qjt[SRt.get(i)][t];
			// sumN += this.d_jt.get(i+","+t);
		}
		sumT += math.ceil((double) sumN / (1 * par.kv));

		if (sumT > maxTaur_ij)
			maxTaur_ij = sumT;

		int V = maxTaur_ij;
		// ?????????????????????????????????????????

		String filename = "Rahim_CVRP.vrp";
		try {
			PrintWriter writer = new PrintWriter(path + filename, "UTF-8");
			writer.println("NAME : " + filename + "");
			writer.println("COMMENT : (.)");
			writer.println("TYPE : CVRP");
			writer.println("DIMENSION : " + (SRt.size() + 1) + "");
			writer.println("EDGE_WEIGHT_TYPE : EUC_2D ");
			writer.println("CAPACITY : " + par.kv + "");
			writer.println("NODE_COORD_SECTION");
			writer.println("0" + " " + (int) par.iPosX[0] + " " + (int) par.iPosY[0]);
			for (int i = 0; i < SRt.size(); i++) {
				writer.println(SRt.get(i) + " " + (int) par.iPosX[SRt.get(i)] + " " + (int) par.iPosY[SRt.get(i)]);
			}
			writer.println("DEMAND_SECTION");
			writer.println("0" + " " + "0");
			for (int i = 0; i < SRt.size(); i++) {
				writer.println(SRt.get(i) + " " + (int) XZ_Best.z.qjt[SRt.get(i)][t]);
			}
			writer.println("DEPOT_SECTION");
			writer.println("0");
			writer.println("-1");
			writer.println("EOF");
			writer.close();
		} catch (IOException e) {
			// do something
		}
		ret = filename + ";" + V;
		return ret;

	}

}
