package it.polito.tdp.extflightdelays.model;

public class Rotta {
	
	Airport p; 
	Airport a; 
	int peso;
	
	
	public Rotta(Airport p, Airport a, int peso) {
		
		this.p = p;
		this.a = a;
		this.peso = peso;
	}
	public Airport getP() {
		return p;
	}
	public void setP(Airport p) {
		this.p = p;
	}
	public Airport getA() {
		return a;
	}
	public void setA(Airport a) {
		this.a = a;
	}
	public int getPeso() {
		return peso;
	}
	public void setPeso(int peso) {
		this.peso = peso;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rotta other = (Rotta) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		return true;
	} 
	
	
	

}
