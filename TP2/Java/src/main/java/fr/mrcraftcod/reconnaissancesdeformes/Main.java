package fr.mrcraftcod.reconnaissancesdeformes;

import com.sun.tools.javac.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
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
	
	public static void main(String[] args) throws IOException{
		List<Double> cs = List.of(1D, 2D, 5D, 10D, 100D);
		List<Double> gs = List.of(0.1D, 0.5D, 1D, 2D, 10D);
		
		new File("./temp").mkdirs();
		
		cs.parallelStream().flatMap(c -> gs.stream().map(g -> new HashMap.SimpleEntry<>(c, g))).map(couple -> {
			try{
				svm_train.main(MessageFormat.format("-t 2 -c {0} -g {1} ../Iris/iris.app ./temp/iris.app.model.{0}.{1}", couple.getKey(), couple.getValue()).split(" "));
				return new HashMap.SimpleEntry<>(svm_predict.main(MessageFormat.format("../Iris/iris.test ./temp/iris.app.model.{0}.{1} ./temp/iris.app.model.{0}.{1}.out", couple.getKey(), couple.getValue()).split(" ")), couple);
			}
			catch(IOException e){
				LOGGER.error("", e);
			}
			return null;
		}).filter(Objects::nonNull).max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getKey)).ifPresent(c -> LOGGER.info("Max couple is c={}, g={} with accuracy={}", c.getValue().getKey(), c.getValue().getValue(), c.getKey()));
	}
	
	public static TableValues cut(double minC, double maxC, double minG, double maxG, int step, double precision) throws Exception{
		if(maxC - minC <= precision && maxG - minG <= precision){
			return new TableValues(minC, maxC, minG, maxG);
		}
		Optional<AbstractMap.SimpleEntry<Double, AbstractMap.SimpleEntry<Double, Double>>> best = IntStream.range(0, step).mapToDouble(i -> minC + i * ((maxC - minC) / step)).boxed().parallel().flatMap(c -> IntStream.range(0, step).mapToDouble(i -> minC + i * ((maxC - minC) / step)).mapToObj(g -> new HashMap.SimpleEntry<>(c, g))).map(couple -> {
			try{
				svm_train.main(MessageFormat.format("-t 2 -c {0} -g {1} ../Iris/iris.app ./temp/iris.app.model.{0}.{1}", couple.getKey(), couple.getValue()).split(" "));
				return new HashMap.SimpleEntry<>(svm_predict.main(MessageFormat.format("../Iris/iris.test ./temp/iris.app.model.{0}.{1} ./temp/iris.app.model.{0}.{1}.out", couple.getKey(), couple.getValue()).split(" ")), couple);
			}
			catch(IOException e){
				LOGGER.error("", e);
			}
			return null;
		}).filter(Objects::nonNull).max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getKey));
		if(best.isPresent()){
			AbstractMap.SimpleEntry<Double, Double> bb = best.get().getValue();
			double mminC = 0D;
			double mmaxC = 0D;
			double mminG = 0D;
			double mmaxG = 0D;
			//TODO
		}
		throw new Exception("BIG PROBLEM");
	}
}
