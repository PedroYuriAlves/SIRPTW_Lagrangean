package Principal;
import java.util.LinkedList;

public class Main {
	private static void imprimirSolucao() {
		int rota = 1;
		int custoAtual;
		int demandaAtual;
		LinkedList<LinkedList<Integer>> clusters = Caminhos.solucao;
		
		for(LinkedList<Integer> clusterAtual : clusters) {
			if(clusterAtual.size()<=2) continue;
			
			System.out.print("Rota#"+rota+": ");
			demandaAtual = 0;
			
			for(Integer estado : clusterAtual) {
				System.out.print(estado+" ");
				demandaAtual += Grafo.vertices[estado].getDemanda();
			}
			custoAtual = Caminhos.distanciaTotalCluster(clusterAtual);
			System.out.print("custo: "+custoAtual+" demanda atendida: "+demandaAtual);
			System.out.println();
			rota++;
		}
		System.out.println("Custo "+Caminhos.distanciaTotalRotas());
	}
	public static void main(String[] args) {
		try {
			Grafo.inicializarGrafo("C:\\MeusProgramas\\IA\\A-n32-k5.vrp");
			Caminhos.inicializarListaSolucao();
			long tempoAnterior = System.currentTimeMillis();
			
			
			int maxIteracoes = 20;
			Clusters.centroidVRP(maxIteracoes);
			imprimirSolucao();
			
			
			long tempoAtual = System.currentTimeMillis();
			long delta = tempoAtual - tempoAnterior;
			System.out.println("Tempo "+delta);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
