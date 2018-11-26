package parser;

import edu.stanford.nlp.sentiment.CollapseUnaryTransformer;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreeBinarizer;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.Trees;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.LeftHeadFinder;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import parser.RightHeadFinder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class parser_MR {
	public static void main(String[] args) {
	    CollapseUnaryTransformer transformer = new CollapseUnaryTransformer();
		String parserModel1 = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser parser = LexicalizedParser.loadModel(parserModel1);
	    TreeBinarizer binarizer = TreeBinarizer.simpleTreeBinarizer(parser.getTLPParams().headFinder(), parser.treebankLanguagePack());
	    
	    try {
	    	File in = new File("/home/galsang/PycharmProjects/TC-Tree-LSTM/.data/TREC/trec_test.jsonl");
	    	File out = new File("/home/galsang/PycharmProjects/TC-Tree-LSTM/.data/TREC/parsed/trec_test.jsonl");

	    	FileReader filereader = new FileReader(in);
	    	BufferedReader bufReader = new BufferedReader(filereader);
	    	BufferedWriter bufWriter = new BufferedWriter(new FileWriter(out));
	    	
	    	String line = "";
	    	while((line = bufReader.readLine()) != null) {
	    		JSONParser jParser = new JSONParser();
	    		JSONObject json = (JSONObject) jParser.parse(line); 
	    		
	    		List<HasWord> tokens = sentenceToTokens((String) json.get("sentence"), true);
	    		Tree tree = parser.apply(tokens);
	    	    Tree binarized = binarizer.transformTree(tree);
	    	    Tree collapsedUnary = transformer.transformTree(binarized);
	    	    json.put("parse", collapsedUnary.toString());
	    	    	    	    
	    	    System.out.println(json.toJSONString());
	    	    bufWriter.write(json.toJSONString());
	    	    bufWriter.write("\n");
	    	}
	    	bufReader.close();
	    	bufWriter.close();
	    } catch(Exception e) {
	    	System.out.println(e);
	    }
	} 
	
	public static List<HasWord> sentenceToTokens(String line, boolean tokenize) {
		List<HasWord> tokens = new ArrayList<>();
		if (tokenize) {
			PTBTokenizer<Word> tokenizer = new PTBTokenizer(new StringReader(line), new WordTokenFactory(), "");
			for (Word label; tokenizer.hasNext(); ) {
				tokens.add(tokenizer.next());
			}
		} else {
			for (String word : line.split(" ")) {
				tokens.add(new Word(word));
			}
		}
		return tokens;
	}
}
