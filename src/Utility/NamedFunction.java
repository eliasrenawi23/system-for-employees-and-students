package Utility;

public class NamedFunction implements Comparable{

	private String name;
	private Func func;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Func getFunc() {
		return func;
	}
	public void setFunc(Func func) {
		this.func = func;
	}
	public NamedFunction(String name, Func func) {
		super();
		this.name = name;
		this.func = func;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		NamedFunction other = (NamedFunction) obj;
		if (func == null) {
			if (other.func != null)
				return false;
		} else if (!func.equals(other.func))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public int compareTo(Object other) {
		return name.compareTo(((NamedFunction) other).getName());
	}
	
	
}
