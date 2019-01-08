package fr.mrcraftcod.reconnaissancesdeformes;

import com.sun.tools.javac.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-12-21.
 *
 * @author Thomas Couchoud
 * @since 2018-12-21
 */
public class Main{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception{
		new File("./temp").mkdirs();
		// bestList();
		cut(0.1, 100, 0.1, 10, 10, 0.001);
	}
	
	public static void bestList(){
		com.sun.tools.javac.util.List<Double> cs = com.sun.tools.javac.util.List.of(1D, 2D, 5D, 10D, 100D);
		com.sun.tools.javac.util.List<Double> gs = List.of(0.1D, 0.5D, 1D, 2D, 10D);
		
		cs.parallelStream().flatMap(c -> gs.stream().map(g -> new HashMap.SimpleEntry<>(c, g))).map(couple -> {
			try{
				String fileModel = String.format("./temp/iris.app.model.%f.%f", couple.getKey(), couple.getValue());
				String fileOut = String.format("%s.out", fileModel);
				svm_train.main(String.format("-t 2 -c {0} -g {1} ../Iris/iris.app %s", fileModel).split(" "));
				return new HashMap.SimpleEntry<>(svm_predict.main(String.format("../Iris/iris.test %s %s", fileModel, fileOut).split(" ")), couple);
			}
			catch(IOException e){
				LOGGER.error("", e);
			}
			return null;
		}).filter(Objects::nonNull).max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getKey)).ifPresent(c -> LOGGER.info("Max couple is c={}, g={} with accuracy={}", c.getValue().getKey(), c.getValue().getValue(), c.getKey()));
	}
	
	public static TableValues cut(double minC, double maxC, double minG, double maxG, int step, double precision) throws Exception{
		LOGGER.info("Testing minC={}, maxC={}, minG={}, maxG={}", minC, maxC, minG, maxG);
		if(maxC - minC <= precision && maxG - minG <= precision){
			return new TableValues(minC, maxC, minG, maxG);
		}
		double stepAmountC = (maxC - minC) / step;
		double stepAmountG = (maxG - minG) / step;
		Optional<AbstractMap.SimpleEntry<Double, AbstractMap.SimpleEntry<Double, Double>>> best = IntStream.range(0, step).mapToDouble(i -> minC + i * stepAmountC).boxed().parallel().flatMap(c -> IntStream.range(0, step).mapToDouble(i -> minG + i * stepAmountG).mapToObj(g -> new HashMap.SimpleEntry<>(c, g))).map(couple -> {
			try{
				String fileModel = String.format("./temp/iris.app.model.%f.%f", couple.getKey(), couple.getValue());
				String fileOut = String.format("%s.out", fileModel);
				svm_train.main(String.format("-t 2 -c %f -g %f ../Iris/iris.app %s", couple.getKey(), couple.getValue(), fileModel).split(" "));
				return new HashMap.SimpleEntry<>(svm_predict.main(String.format("../Iris/iris.test %s %s", fileModel, fileOut).split(" ")), couple);
			}
			catch(IOException e){
				LOGGER.error("", e);
			}
			return null;
		}).filter(Objects::nonNull).max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getKey));
		if(best.isPresent()){
			AbstractMap.SimpleEntry<Double, Double> bb = best.get().getValue();
			LOGGER.info("Best is c={}, g={}, precision={}", bb.getKey(), bb.getValue(), best.get().getKey());
			double mminC;
			double mmaxC;
			double mminG;
			double mmaxG;
			if(minC == bb.getKey()){
				mminC = minC;
			}
			else{
				mminC = bb.getKey() - stepAmountC;
			}
			if(minG == bb.getValue()){
				mminG = minG;
			}
			else{
				mminG = bb.getValue() - stepAmountG;
			}
			if(maxC == bb.getKey()){
				mmaxC = maxC;
			}
			else{
				mmaxC = bb.getKey() + stepAmountC;
			}
			if(maxG == bb.getValue()){
				mmaxG = maxG;
			}
			else{
				mmaxG = bb.getValue() + stepAmountG;
			}
			return cut(mminC, mmaxC, mminG, mmaxG, step, precision);
		}
		throw new Exception("BIG PROBLEM");
	}
}
