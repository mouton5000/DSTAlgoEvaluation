package graphTheory.utils;

import java.util.Set;

/**
 * Define an object where different parameters can be added to.
 * 
 * @author Watel Dimitri
 * 
 */
public interface Parametable {

	public abstract void defineParam(String s, Object obj);

	public abstract boolean containsParam(String s);

	public abstract Object getParam(String s);

	public abstract Integer getParamInteger(String s);

	public abstract Double getParamDouble(String s);

	public abstract Long getParamLong(String s);

	public abstract String getParamString(String s);

	public abstract Boolean getParamBoolean(String s);

	public abstract void clearParams();

	public abstract void copyParams(Parametable a);

	public abstract Set<String> getParamsNames();
}
