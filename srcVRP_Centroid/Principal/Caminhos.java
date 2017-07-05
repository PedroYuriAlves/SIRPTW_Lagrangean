package Principal;
import java.util.LinkedList;

public class Caminhos {
	public static int capacity;
	public static double tempoMax;
	public static int veloc;
	public static LinkedList<LinkedList<Integer>> solucao; //Cada lista ligada � o caminho de um caminhao
	
	public static void inicializarListaSolucao() {
		solucao = new LinkedList<LinkedList<Integer>>();
	}
	public static void zerarSolucaoAnterior() {
 		solucao.clear();
 	}
	public static int distanciaAcumuladaRota(int rota) {
		int distanciaTotal = 0;
		int verticeAntecessor = -1;
		int verticeSucessor = -1;
		int numeroListaAtual = 0;
		
		for(LinkedList<Integer>rotaAtual : solucao) {
			if(numeroListaAtual==rota) {
				for(Integer verticeAtual : rotaAtual) {
					if(verticeAntecessor == -1) verticeAntecessor = verticeAtual;
					else if(verticeSucessor == -1) {
						verticeSucessor = verticeAtual;
						distanciaTotal += Grafo.distanciaEuclidiana(verticeAntecessor, verticeSucessor);
						verticeAntecessor = verticeSucessor;
					}
					else {
						verticeSucessor = verticeAtual;
						distanciaTotal += Grafo.distanciaEuclidiana(verticeAntecessor, verticeSucessor);
						verticeAntecessor = verticeSucessor;
					}
				}
				break;
			}
			numeroListaAtual++;
		}
		return distanciaTotal;
	}
	public static int distanciaTotalRotas() {
 		int distanciaTotal = 0;
 		
 		for(int i=0; i<solucao.size(); i++) {
 			distanciaTotal += distanciaAcumuladaRota(i);
 		}
 		return distanciaTotal;
 	}
	public static int distanciaTotalCluster(LinkedList<Integer> cluster) {
		int distanciaTotal = 0;
		int estadoAntecessor = -1;
		int estadoSucessor = -1;
		
		for(Integer estadoAtual : cluster) {
			if(estadoAntecessor == -1) estadoAntecessor = estadoAtual;
			else if(estadoSucessor == -1) {
				estadoSucessor = estadoAtual;
				distanciaTotal += Grafo.distanciaEuclidiana(estadoAntecessor, estadoSucessor);
				estadoAntecessor = estadoSucessor;
			}
			else {
				estadoSucessor = estadoAtual;
				distanciaTotal += Grafo.distanciaEuclidiana(estadoAntecessor, estadoSucessor);
				estadoAntecessor = estadoSucessor;
			}
		}
		return distanciaTotal;
	}
	public static int distanciaTotalRotasCluster(LinkedList<LinkedList<Integer>> clusters) {
 		int distanciaTotal = 0;
 		
 		for(int i=0; i<clusters.size(); i++) {
 			distanciaTotal += distanciaTotalCluster(clusters.get(i));
 		}
 		return distanciaTotal;
 	}
	public static void copiarDeSolucaoParaLista(LinkedList<LinkedList<Integer>> solucaoCopia) {
		solucaoCopia.clear(); //Removendo dados anteriores
		
		for(LinkedList<Integer> rotaAtual : solucao) {
			LinkedList<Integer> novaRota = new LinkedList<Integer>();
			for(Integer estadoAtual : rotaAtual) {
				novaRota.addLast(estadoAtual);
			}
			solucaoCopia.addLast(novaRota);
		}
	}
	public static void copiarDeListaParaSolucao(LinkedList<LinkedList<Integer>> lista) {
		solucao.clear();
		
		for(LinkedList<Integer> rotaAtual : lista) {
			LinkedList<Integer> novaRota = new LinkedList<Integer>();
			for(Integer estadoAtual : rotaAtual) {
				novaRota.addLast(estadoAtual);
			}
			solucao.addLast(novaRota);
		}
	}
	/*public static void copiar(LinkedList<LinkedList<Integer>> solucaoCopia) {
		solucaoCopia.clear(); //Removendo dados anteriores
		
		for(LinkedList<Integer> rotaAtual : solucao) {
			LinkedList<Integer> novaRota = new LinkedList<Integer>();
			for(Integer estadoAtual : rotaAtual) {
				novaRota.addLast(estadoAtual);
			}
			solucaoCopia.addLast(novaRota);
		}
	}*/
}
