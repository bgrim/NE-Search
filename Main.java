package branchAndBound;

public class Main {
	public static void main(String[] args) {
		int numP=3;
		int numL=3;
		double [] phi = new double[numP];
		double [] lam = new double[numP];
		double [] mu = new double[numL];
		
		while(sample(phi, lam, mu)){
			//print(phi, lam, mu);
			int numNE=0;
			for(int i=0;i<Math.pow(2,numP*numL);i++){
				boolean [][]choice = decode(i, numP, numL);  //player X link
				
				boolean NE=true;
				for(int p=0;p<numP;p++){
					boolean [] best = bestResponse(choice, p, phi, lam, mu);
					for(int l=0;l<numL;l++){
						if(best[l]!=choice[p][l]) NE=false;
					}
				}
				if(NE){
					//print(i,choice);
					numNE++;
				}
			}
			System.out.println("Total Number of NE : "+numNE);
			if(numNE==0){
				print(phi,lam,mu);
				break;
			}
		}
	}

	private static void print(double [] phi, double [] lam, double [] mu){
		System.out.println(phi.length+" Players:");
		for(int p=0;p<phi.length;p++){
			System.out.println("Phi"+p+" = "+phi[p]);
		}
		for(int p=0;p<phi.length;p++){
			System.out.println("Lam"+p+" = "+lam[p]);
		}
		for(int l=0;l<mu.length;l++){
			System.out.println("Mu"+l+" = "+mu[l]);
		}
	}
	private static void print(int i, boolean [][] choice){
		System.out.println("Found NE at "+i);
		for(int p=0;p<choice.length;p++){
			System.out.print("   ");
			for(int l=0;l<choice[0].length;l++){
				System.out.print(" ");
				if(choice[p][l]) System.out.print(1);
				else System.out.print(0);
			}
			System.out.println("");
		}
	}
	private static boolean [][] decode(int i, int numP, int numL){
		boolean [][] ret= new boolean[numP][numL];
		for(int p=0;p<numP;p++){
			for(int l=0;l<numL;l++){
				ret[p][l] = (i%2)==0;
				i=i/2;
			}
		}
		return ret;
	}
	private static boolean [] bestResponse(boolean [][]choice, int p, double []phi, double []lam, double []mu){
		int [] sorted_links = new int[mu.length];
		
		//Sort links based on choice of all other players, there aren't many links, so I will selection sort.
		boolean [] selected = new boolean[mu.length];
		for(int i=0;i<mu.length;i++){
			int max_index=-1;
			double max=Integer.MIN_VALUE;
			for(int j=0;j<mu.length;j++){
				if(selected[j]) continue;
				if(mu[j]/sumPhi(choice,j,p, phi) > max){
					max_index=j;
					max =mu[j]/sumPhi(choice,j,p, phi);
				}
			}
			selected[max_index]=true;
			sorted_links[i]=max_index;
		}
		//Select links in our list until they are not improving
		boolean [] ret = new boolean[mu.length];
		ret[sorted_links[0]]=true;
		double total_cap= phi[p]*mu[sorted_links[0]]/sumPhi(choice,sorted_links[0],p, phi);
		int n=1; //number of links selected
		while((total_cap-lam[p])/n < phi[p]*mu[sorted_links[n]]/sumPhi(choice,sorted_links[n],p, phi)){
			ret[sorted_links[n]]=true;
			total_cap+= phi[p]*mu[sorted_links[n]]/sumPhi(choice,sorted_links[n],p, phi);
			n++;
			if(n>=mu.length)break;
		}
				
		return ret;
	}
	private static double sumPhi(boolean [][]choice, int l, int p, double []phi){
		double ret=0;
		for(int q=0;q<choice.length;q++){
			if(choice[q][l] || q==p) ret+=phi[q];
		}
		return ret;
	}
	
	private static int counter=0;
	private static boolean sample(double [] phi, double [] lam, double [] mu){
		//returns false to end the program
		//otherwise, sets values for phi, lambda, and mu then returns true
		counter++;
		if(counter>1) return false;

		//A test case
		phi[0]=0.1;
		phi[1]=0.48;
		phi[2]=0.42;
		lam[0]=0.2;
		lam[1]=0.5;
		lam[2]=0.28;
		mu[0] =0.6;
		mu[1] =1.27;
		mu[2] =0.30;
		
		/*
		//Generate Random Phi, then Normalize the values
		double total_phi=0;
		for(int p=0; p<phi.length; p++){
			phi[p]=Math.random();
			total_phi+=phi[p];
		}
		for(int p=0; p<phi.length; p++){
			phi[p]=phi[p]/total_phi;
		}
		
		//Generate Random Lambda in [0,1]
		for(int p=0; p<lam.length; p++){
			lam[p]=Math.random();
		}
		
		//Generate Random Mu in [0,3]
		for(int l=0; l<mu.length; l++){
			mu[l]=Math.random()*3;
		}
		*/
		return true;
	}
}
