package algoritmo;

import java.util.Stack;

import instancias.Instancia;
import instancias.Instancia_Rahim;

public class TSP {
	/*public Tour NearestNeighbour(Tour tour, Instancia_Rahim inst){
		Tour ret = new  Tour();
		ret.TravelTime = 0;
		Stack<Integer> stack = new Stack<Integer>();
		if(tour.lClientes.size() > 0){
			stack.push(0);
			int [] visitados = new int[tour.lClientes.size()];

			while (!stack.isEmpty()){
				int atual = stack.peek();
				int  dst = -1,idDest = -1;
				boolean minFlag = false;
				double min = Double.MAX_VALUE;

				for(int i = 0;i< tour.lClientes.size();i++){
					Cliente c =  tour.lClientes.get(i);
					if(visitados[i] == 0){
						if (min > inst.getΘ_ij()[atual][c.id])
						{
							min = inst.getΘ_ij()[atual][c.id];
							dst = c.id;
							minFlag = true;
							idDest = i;
						}
					}
				}
				if (minFlag)
				{
					visitados[idDest] = 1;
					stack.push(dst);

					ret.lClientes.add(tour.lClientes.get(idDest));
					ret.TravelTime += min;

					System.out.print(dst + "\t");
					minFlag = false;
					continue;
				}
				stack.pop();

			}

			ret.TravelTime +=inst.getΘ_ij()[ret.lClientes.getLast().id][0];
		}

		return ret;
	}*/
}
