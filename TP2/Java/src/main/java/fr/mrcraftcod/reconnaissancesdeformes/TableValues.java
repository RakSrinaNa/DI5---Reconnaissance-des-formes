package fr.mrcraftcod.reconnaissancesdeformes;

import java.util.StringJoiner;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2019-01-07.
 *
 * @author Thomas Couchoud
 * @since 2019-01-07
 */
public class TableValues{
	private final double minC;
	private final double maxC;
	private final double minG;
	private final double maxG;
	
	public TableValues(double minC, double maxC, double minG, double maxG){
		this.minC = minC;
		this.maxC = maxC;
		this.minG = minG;
		this.maxG = maxG;
	}
	
	@Override
	public String toString(){
		return new StringJoiner(", ", TableValues.class.getSimpleName() + "[", "]").add("minC=" + minC).add("maxC=" + maxC).add("minG=" + minG).add("maxG=" + maxG).toString();
	}
}
