package Simullated;
/* Classe com métodos estáticos para uso
 * no Algoritmo de Simullated Annealing
 *
 */

import java.util.*;

public class GeracaoDeVizinhos {

	// método para testes locais
	public static void main(String[] args) {
		Util u = new Util();
	//	u.inicializa();
		//Rota #1: 0 1 2 3 4 5 6 7 9 11 0 custo: 203.0757085865738 demanda atendida: 160 CLIENTES 9
		//
		LinkedList<ArrayList<Client>> grafo = new LinkedList<ArrayList<Client>>();		
		ArrayList<Client> lista = new ArrayList<Client>();
		lista.add(u.clients[1]);
		lista.add(u.clients[2]);
		lista.add(u.clients[3]);
		lista.add(u.clients[4]);
		lista.add(u.clients[5]);
		lista.add(u.clients[6]);
		lista.add(u.clients[7]);
		lista.add(u.clients[8]);
		lista.add(u.clients[9]);
		lista.add(u.clients[11]);
		grafo.add(lista);		
		lista = new ArrayList<Client>();
		lista.add(u.clients[8]);
		lista.add(u.clients[10]);
		lista.add(u.clients[12]);
		lista.add(u.clients[13]);
		lista.add(u.clients[14]);
		lista.add(u.clients[15]);
		lista.add(u.clients[16]);
		lista.add(u.clients[17]);
		lista.add(u.clients[18]);
		lista.add(u.clients[19]);
		grafo.add(lista);		
		//Rota #2: 0 8 10 12 13 14 15 16 17 18 19 0 custo: 283.34619417964365 demanda atendida: 150 CLIENTES 19
		LinkedList<Client> teste = new LinkedList<Client>();
		teste = transformaEmVetor(grafo);
		for (int i = 0; i < teste.size(); i++) {
			System.out.print(teste.get(i).id+" ");
		}
		System.out.println();
		GeracaoDeVizinhos.oneToOneExchange(teste);
		grafo = new LinkedList<ArrayList<Client>>();
		grafo = transformaEmGrafo(teste);
		u.imprime_rotas(grafo);
	}
	
	public static void oneToOneExchange(LinkedList<Client> rotas) {

		Random r = new Random();
		int posicao1 = r.nextInt(rotas.size());
		int posicao2 = r.nextInt(rotas.size());
		Client aux = rotas.get(posicao1);
		rotas.set(posicao1, rotas.get(posicao2));
		rotas.set(posicao2, aux);
	}
	
	
	

	public static void deleteInsert(LinkedList<Client> rotas) {
		Random r = new Random();
		int posicao = r.nextInt(rotas.size());
		int posicaoDestino = r.nextInt(rotas.size());
		rotas.add(posicaoDestino, rotas.remove(posicao));
	}

	// adicionar condicional de balanceamento do grafo
	public static void partialReversal(LinkedList<Client> rotas) {
		Random r = new Random();
		int posicao1 = r.nextInt(rotas.size() + 1);
		int posicao2 = r.nextInt(rotas.size() + 1);
		int menor = posicao1 < posicao2 ? posicao1 : posicao2;
		int maior = posicao1 > posicao2 ? posicao1 : posicao2;
		Collections.reverse(rotas.subList(menor, maior));
	}

	/* métodos auxiliares de transformação grafo-vetor */

	public static LinkedList<Client> transformaEmVetor (LinkedList<ArrayList<Client>> grafo) {
		LinkedList<Client> resposta = new LinkedList<Client>();
		for (int i = 0; i < grafo.size(); i++){			
			for( int j = 0; j < grafo.get(i).size(); j++)
				resposta.add(grafo.get(i).get(j));
			
			resposta.add(Util.clients[0]);
			}
		
			resposta.remove(resposta.size()-1);
		return resposta;
	}

	public static LinkedList<ArrayList<Client>> transformaEmGrafo(LinkedList<Client> lista) {
		LinkedList<ArrayList<Client>> grafo = new LinkedList<ArrayList<Client>>();
		ArrayList<Client> listaAuxiliar;
		
		for (int i = 0; i <lista.size(); i++) {
			listaAuxiliar = new ArrayList<Client>();
			grafo.add(listaAuxiliar);
			while (i<lista.size()&&!lista.get(i).id.equals("0")) {
				listaAuxiliar.add(lista.get(i));
				i++;
			}
		}
		

		return grafo;
	}
	
}
