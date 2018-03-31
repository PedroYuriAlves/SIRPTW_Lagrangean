import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.script.ScriptException;

import algoritmo.*;
import gurobi.*;
import instancias.Instancia_Rahim;
import instancias.Parametros;
import instancias.Sol_XZ;
import instancias.Variaveis_Rahim;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Main {

	public static void main(String[] args) throws GRBException, IOException, ScriptException, BiffException {
		// TODO Auto-generated method stub
		File file = new File("C:\\Users\\kresp\\workspace\\Testes_SIRP\\Instancias_SIRP50_3.csv");
		File fileStandardDistribution = new File("C:\\Users\\kresp\\workspace\\Testes_SIRP\\Tabela_Distrib_Normal_Z.csv");
		File fileReal = new File("C:\\Users\\kresp\\workspace\\Testes_SIRP\\Tabela_Caso_Real_final.xls");
		List<String> lines = Files.readAllLines(file.toPath(), 
				StandardCharsets.UTF_8);
		int c = 0;

		Workbook wReal=Workbook.getWorkbook(fileReal);

		Sheet matrizDist = wReal.getSheet("Matriz Dist");
		Sheet geoRef = wReal.getSheet("Georef");
		Sheet Demanda = wReal.getSheet("Demanda");

		int rowGeoRef = geoRef.getRows();
		int colGeoRef = geoRef.getColumns();


		double[][] mDist = null;
		double[] lat = null;
		double[] lng = null;
		double[] demanda = null;
		int rowDemanda = Demanda.getRows();
		int colDemanda = Demanda.getColumns();

		if(rowGeoRef > 0 && rowDemanda > 0){
 			lat = new double[rowGeoRef-1];
			lng = new double[rowGeoRef-1];
			demanda = new double [rowDemanda-1];
			for(int x = 1; x < rowGeoRef; x++){
				if("".equals(geoRef.getCell(1,x).getContents().toString()))break;
				lat[x-1] = Double.parseDouble(geoRef.getCell(2,x).getContents().toString().replace(',', '.'));
				lng[x-1] = Double.parseDouble(geoRef.getCell(3,x).getContents().toString().replace(',', '.'));
				demanda[x-1] = Double.parseDouble(Demanda.getCell(1,x).getContents().toString().replace(',', '.'));
			}
		}

		int rowMatriz = matrizDist.getRows();
		int colMatriz = matrizDist.getColumns();

		if(rowMatriz > 0){
			mDist = new double[rowMatriz-1][rowGeoRef-1];
			int i = 0;
			int j = 0;
			for(int x = 1; x < rowMatriz; x++){
				if("".equals(matrizDist.getCell(0,x).getContents().toString()) ||"".equals(matrizDist.getCell(1,x).getContents().toString()) )break;
				i = Integer.parseInt(matrizDist.getCell(0,x).getContents().toString());
				j = Integer.parseInt(matrizDist.getCell(1,x).getContents().toString());
				mDist[i][j] = Double.parseDouble(matrizDist.getCell(2,x).getContents().toString().replace(',', '.'));

				/*j++;
				if(j >= rowGeoRef-1){
					j = 0;
					i++;
				}*/
			}
		}




		boolean execOtimo = false;
		boolean execRahim = true;

		String[] Header = null;

		Boolean print = false;

		FileWriter writer = new FileWriter(file.getPath().split(".csv")[0]+"result.csv");


		List<String> Matriz_Z = Files.readAllLines(fileStandardDistribution.toPath(), 
				StandardCharsets.UTF_8);
		//Bests Seeds 15_3 = 1-450,
		long [] randSeeds15_3 = {40,22,3,4,5,7,8,9,11,34,45,21,31,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33,40,60,32,41,63,33};
		//		long [] randSeeds = {40,60,32,41,63,33};
		//		long [] randSeeds = {40,60,32,41,63,33};
		//		long [] randSeeds = {40,60,32,41,63,33};	

		long [] randSeeds = randSeeds15_3;				

		int iSeed = 0;
		for (String line : lines) {
			if(c == 0){
				Header = line.split(";");
				writer.append("Instances; LB(eur); UB(eur); CPU(s); Gap;k");
			}else if(c>0) {	
				System.gc();
				String[] columns = line.split(";");
				if(columns.length == 1)columns = line.split(",");
				long seed = randSeeds[iSeed];
				if(columns[24] != "" && columns[24] != "0" && tryParseLong(columns[24])) {
					seed = Long.parseLong(columns[24]);
				}
				Random rand = new Random();
				if(seed != 0){  
					rand = new Random(seed);
				}				





				if(columns.length > 0){
					if(!columns[0].trim().equals("*")){
						if(columns.length == Header.length){
							long startTime = System.currentTimeMillis();
							int index = 0;
							writer.append("\n");
							String Name = columns[index++];
							int S = Integer.parseInt(columns[index++]);	//consisting of 15 retailers
							int H = Integer.parseInt(columns[index++]);

							S = S+1; // Deposito
							H = H+1; // Tempo 0;

							int TMatriz = Matriz_Z.get(0).split(";").length;

							if(Matriz_Z.size() >= S && TMatriz >= H){
								int τt = Integer.parseInt(columns[index++]);
								double zα = Double.parseDouble(columns[index++].replace(',', '.'));
								int v_v = Integer.parseInt(columns[index++]); //The vehicles can travel up to 50 km per hour
								int tamXSquare = Integer.parseInt(columns[index++]);// over a square of 30 by		
								int tamYSquare = Integer.parseInt(columns[index++]);//30 km

								int[] kvRange = new int[] {Integer.parseInt(columns[index++]),Integer.parseInt(columns[index++])};
								int[] δv_tRange = new int[] {Integer.parseInt(columns[index++]),Integer.parseInt(columns[index++])};
								double[] ψvRange = new double[] { Double.parseDouble(columns[index++].replace(',', '.')), Double.parseDouble(columns[index++].replace(',', '.'))};


								/*The inventory holding costs are generated randomly and uniformly
				between 0.1 and 0.15 (in euros per tons per period).*/
								double[] η_itRange = new double[] {Double.parseDouble(columns[index++].replace(',', '.')),Double.parseDouble(columns[index++].replace(',', '.'))};
								double[] ϕj_tRange = new double[] {Double.parseDouble(columns[index++].replace(',', '.')),Double.parseDouble(columns[index++].replace(',', '.'))};

								int[] V_iRange = new int[] {Integer.parseInt(columns[index++]),Integer.parseInt(columns[index++])};

								/*Average demand rates of retailers are generated
			randomly and uniformly between 1 and 3 tons per hour with a standard deviation of 5% of the average over the planning
			horizon and the standard normal va lue zα is set to 1.64.*/
								double[] d_jtRange = new double[] {Double.parseDouble(columns[index++].replace(',', '.')),Double.parseDouble(columns[index++].replace(',', '.'))};

								double[] RrtRange = new double[] {Double.parseDouble(columns[index++].replace(',', '.')),Double.parseDouble(columns[index++].replace(',', '.'))};


								double [][] zjt = new double[S][H];
								for(int i = 1; i < S; i++){
									String[] zline = Matriz_Z.get(i-1).split(";");									
									for(int t = 0; t < H; t++){
										zjt[i][t] = Double.parseDouble(zline[t]);
									}
								}



								double[] I_j0Range = new double[] {20,50};
								int[] wj_Range = new int	[] {120,210};
								//arquivo padrao<<<<<<
								if(print)System.out.println("Instancia:" + columns[0]);
								Parametros inst = new Parametros(S, H, τt, kvRange, ψvRange, v_v, δv_tRange, η_itRange, ϕj_tRange,  V_iRange, d_jtRange,RrtRange, tamXSquare, tamYSquare,I_j0Range
										,rand,wj_Range,
										mDist,lat,lng, demanda);
//								null,null,null, null);
								//								inst.inicializaPadrao();

								inst.alg = Integer.parseInt(columns[25]);//Novos Parametros
								inst.restrInv = Integer.parseInt(columns[26]);//Novos Parametros
								inst.restrCap = Integer.parseInt(columns[27]);//Novos Parametros

								int V = inst.S-1;
								inst.V = V;

								//and the distribution centre is always put in the centre of the square
								inst.iPosX[0]=(tamXSquare/2);
								inst.iPosY[0] = (tamYSquare/2);
								inst.zAlfa = (zjt[1][1]);
								//inst.setzAlfa(zα);

								Sol_XZ XZ_0 = new Sol_XZ(inst.S,inst.H,V);
								//		Caso zero de Yt_v
								for(int t = 0 ; t < inst.H ; t++){
									for(int v = 0 ; v < V ; v++){
										if(t!= 0) XZ_0.x.Yt_v[t][v] = 1;
									}
								}
								//Caso zero de Xijt_v
								for(int i = 0 ; i < inst.S ; i++){
									for(int j = 0 ; j < inst.S ; j++){
										for(int t = 0 ; t < inst.H ; t++){
											for(int v = 0 ; v < V ; v++){
												XZ_0.x.Xijt_v[i][j][t][v] = 0; // Inicializa com 0
												if(t!= 0) {
													if(i == 0 && j == 0){
														XZ_0.x.Xijt_v[i][j][t][v] = 0;
													}
													else if(i == 0 ){
														if( v+1 == j){ //Veiculo 1 atende o cli 1, assim por diante
															XZ_0.x.Xijt_v[i][j][t][v] = 1;
														}
													}
													else if(j == 0 ){
														if( v+1 == i){
															XZ_0.x.Xijt_v[i][j][t][v] = 1;
														}
													}
													else {
														XZ_0.x.Xijt_v[i][j][t][v] = 0;
													}
												}
											}
										}
									}
								}
								//								varZero.setY0(inst);

								GRBEnv envRahim = new GRBEnv();
								envRahim.set(GRB.IntParam.OutputFlag, 1);
								envRahim.set(GRB.DoubleParam.NodefileStart, 0.5);

								Algoritmos alg = new Algoritmos();
								if(execOtimo){
									/*Calcula Ótimo**/
									startTime = System.currentTimeMillis();
									SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
									String currentDate=formatter.format(Calendar.getInstance().getTime()); 
									System.out.println(currentDate.toString());
									Problems prob = new Problems();
									Sol_XZ XZ_OTIMO = new Sol_XZ();
									XZ_OTIMO = alg.translateGurobi(prob.CVα(inst, envRahim, null),inst);
									long stopTime = System.currentTimeMillis();
									long elapsedTime = stopTime - startTime;						  
									Double timeSeconds =((double)elapsedTime/(double)1000); 
									writer.append(Name+";"+String.valueOf(XZ_OTIMO.custo).replace(".", ",")+";"+String.valueOf(timeSeconds).replace(".", ","));

									String check = prob.verifySolution("CVLRμ", inst, XZ_OTIMO);
									System.out.println("Erro Otimo "+check);
									/*Calcula Ótimo**/
									currentDate=formatter.format(Calendar.getInstance().getTime()); 
									System.out.println(currentDate.toString());
									writer.append("\n");
								}
								if(execRahim){
									startTime = System.currentTimeMillis();
									Sol_XZ Best = alg.algoritmo1(envRahim,inst, XZ_0,print,rand,startTime);
									long stopTime = System.currentTimeMillis();
									long elapsedTime = stopTime - startTime;						  
									Double timeSeconds =((double)elapsedTime/(double)1000); 

									double Gap1 =((Best.UB-Best.LB)/Best.UB)*100;
									//if(Gap1 < 0)Gap1 = 0;
									writer.append(Name+";"+String.valueOf(Best.LB).replace(".", ",")+";"+String.valueOf(Best.UB).replace(".", ",")+";"+String.valueOf(timeSeconds).replace(".", ",")+";"+String.valueOf(Gap1).replace(".", ",")+";"+Best.k);
									//if(print){
									System.out.println();
									System.out.println(Best.msg);
									System.out.println("Instancia ~ " +Name);
									System.out.println("Custo ~ " +Best.custo);
									System.out.println("LB ~" +Best.LB);
									System.out.println("GAP ~" +Gap1);
									System.out.println("Tempo ~ :" + timeSeconds);
									System.out.println("Fim ~ :" + columns[0]);
									System.gc();
									iSeed++;
									//}
								}
							}else{
								throw new IndexOutOfBoundsException("Arquivo fora do padrão na linha: "+(c+1));
							}
						}
					}
				}
			}
			c++;
		}
		writer.flush();
		writer.close();
	}
	public static boolean tryParseLong(String value) {  
		try {  
			Long.parseLong(value);  
			return true;  
		} catch (NumberFormatException e) {  
			return false;  
		}  
	}
}

