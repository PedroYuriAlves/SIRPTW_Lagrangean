package Principal;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import instancias.Parametros;
import instancias.Sol_XZ;

public class Grafo {
	public static int indexDepot;
	public static Vertice[] vertices;

	public static void inicializarGrafo(String arquivo) {
		try {
			InputStream is = new FileInputStream(arquivo);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			br.readLine(); //"NAME"
			br.readLine(); //"COMMENT"
			br.readLine(); //"TYPE"

			String dimension = br.readLine(); //String que procura a quantidade de v�rtices
			dimension = dimension.replace("DIMENSION : ", "");
			int dimensao = Integer.parseInt(dimension); //Transformando String "dimension" em inteiro

			vertices = new Vertice[dimensao]; //Modificando atributo static

			br.readLine(); //"EDGE_WEIGHT_TYPE"
			String capacidade = br.readLine();
			capacidade = capacidade.replace("CAPACITY : ","");

			Caminhos.capacity = Integer.parseInt(capacidade); //Modificando atributo static

			br.readLine(); //"NODE_COORD_SECTION"

			int i;
			int numeroVertice;
			int X;
			int Y;
			String leitor;
			for(i=0; i<vertices.length; i++) {
				leitor = br.readLine();

				Scanner sc = new Scanner(leitor);
				numeroVertice = sc.nextInt();
				X = sc.nextInt();
				Y = sc.nextInt();
				sc.close();

				Vertice novo = new Vertice(i, X, Y,numeroVertice);
				vertices[i] = novo;
			}
			br.readLine(); //"DEMAND_SECTION"

			int demanda;
			for(i=0;i<vertices.length;i++) {
				leitor = br.readLine();

				Scanner sc = new Scanner(leitor);
				sc.nextInt(); //Numero do vertice que ja foi definido anteriormente
				demanda = sc.nextInt();
				vertices[i].setDemanda(demanda);

				sc.close();
			}
			br.readLine(); //"DEPOT_SECTION"

			leitor = br.readLine();
			Scanner sc = new Scanner(leitor);
			int depot = sc.nextInt();

			for(i=0; i<vertices.length; i++) {
				if(vertices[i].getNumero()==depot) {
					indexDepot = i;
					break;
				}
			}

			sc.close();
			br.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("Arquivo nao encontrado");
		}
		catch(NoSuchElementException e) {
			System.out.println("Arquivo em formato inapropriado");
		}
		catch(NumberFormatException e) {
			System.out.println("Arquivo em formato inapropriado");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void inicializarGrafo(List<Integer> SRt,Sol_XZ XZ_Best,Parametros par,int t) {
		try {
			//			InputStream is = new FileInputStream(arquivo);
			//			InputStreamReader isr = new InputStreamReader(is);
			//			BufferedReader br = new BufferedReader(isr);

			//			br.readLine(); //"NAME"
			//			br.readLine(); //"COMMENT"
			//			br.readLine(); //"TYPE"

			String dimension = String.valueOf(SRt.size()+1); //String que procura a quantidade de v�rtices
			dimension = dimension.replace("DIMENSION : ", "");
			int dimensao = Integer.parseInt(dimension); //Transformando String "dimension" em inteiro

			vertices = new Vertice[dimensao]; //Modificando atributo static

			//			br.readLine(); //"EDGE_WEIGHT_TYPE"
			String capacidade = String.valueOf(par.kv);
			capacidade = capacidade.replace("CAPACITY : ","");

			Caminhos.capacity = Integer.parseInt(capacidade); //Modificando atributo static
			Caminhos.tempoMax = par.τt; //Modificando atributo static
			Caminhos.veloc = par.ν_v; //Modificando atributo static

			//			br.readLine(); //"NODE_COORD_SECTION"

			int i;
			int id;
			double X;
			double Y;
			String leitor;

			id = 0;
			X = par.iPosX[0];//sc.nextInt();
			Y = par.iPosY[0];//sc.nextInt();
			Vertice novo = new Vertice(id, X, Y, 0);
			vertices[0] = novo;
			for(i=0; i<SRt.size(); i++) {
		
				id = SRt.get(i);
				X = par.iPosX[SRt.get(i)];//sc.nextInt();
				Y = par.iPosY[SRt.get(i)];//sc.nextInt();
				//	sc.close();

				novo = new Vertice(i+1, X, Y,id);
				vertices[i+1] = novo;
			}
			//			br.readLine(); //"DEMAND_SECTION"

			double demanda;
			for(i=0;i<vertices.length;i++) {
				//				leitor = br.readLine();

				//				Scanner sc = new Scanner(leitor);
				//				sc.nextInt(); //Numero do vertice que ja foi definido anteriormente
				demanda = XZ_Best.z.qjt[vertices[i].getId()][t];
				vertices[i].setDemanda(demanda);

				//	sc.close();
			}
			//	br.readLine(); //"DEPOT_SECTION"

			//	leitor = br.readLine();
			//	Scanner sc = new Scanner(leitor);
			int depot = 0;

			for(i=0; i<vertices.length; i++) {
				if(vertices[i].getNumero()==depot) {
					indexDepot = i;
					break;
				}
			}

			//	sc.close();
			//	br.close();
		}
		//catch(FileNotFoundException e) {
		//	System.out.println("Arquivo nao encontrado");
		//}
		catch(NoSuchElementException e) {
			System.out.println("Arquivo em formato inapropriado");
		}
		catch(NumberFormatException e) {
			System.out.println("Arquivo em formato inapropriado");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static double tempoEntre(int estadoI, int estadoJ, double veloc) {
		//"i" e "j" sao posicoes do vetor e nao o numero dos vertices descritos no arquivo de entrada
		double resp = 0;
		
		double dist = distanciaEuclidiana(estadoI,estadoJ);
		resp = dist/veloc;
		
		return resp;
	}
	
	public static double distanciaEuclidiana(int estadoI, int estadoJ) {
		//"i" e "j" sao posicoes do vetor e nao o numero dos vertices descritos no arquivo de entrada
		if(estadoI==estadoJ) return 0;

		double diferencaX = vertices[estadoI].getX() - vertices[estadoJ].getX(); 
		double diferencaY = vertices[estadoI].getY() - vertices[estadoJ].getY();

		//Abaixo calculamos a distancia euclidiana utilizando apenas 1 variavel
		double distancia = (diferencaX*diferencaX)+(diferencaY*diferencaY);
		distancia = Math.sqrt(distancia);
		distancia = Math.round(distancia);

		int resp = (int) distancia;
		return resp;
	}
	public static double distanciaEuclidianaXY(double x, double y, int estado) {
		//"i" e "j" sao posicoes do vetor e nao o numero dos vertices descritos no arquivo de entrada

		double diferencaX = x - vertices[estado].getX(); 
		double diferencaY = y - vertices[estado].getY();

		//Abaixo calculamos a distancia euclidiana utilizando apenas 1 variavel
		double distancia = (diferencaX*diferencaX)+(diferencaY*diferencaY);
		distancia = Math.sqrt(distancia);
		distancia = Math.round(distancia);

		int resp = (int) distancia;
		return resp;
	}
}
