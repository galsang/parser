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

public class parser {
	public static void main(String[] args) {
	    CollapseUnaryTransformer transformer = new CollapseUnaryTransformer();
		String parserModel1 = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser parser = LexicalizedParser.loadModel(parserModel1);
	    TreeBinarizer binarizer = TreeBinarizer.simpleTreeBinarizer(parser.getTLPParams().headFinder(), parser.treebankLanguagePack());
	    // LeftHeadFinder hf = new LeftHeadFinder();
	    // RightHeadFinder hf = new RightHeadFinder();
	    // TreeBinarizer binarizer = new TreeBinarizer(hf, parser.treebankLanguagePack(), false, false, 0, false, false, 0.0, false, true, true);
	    
	    try {
	    	File in1 = new File("/home/galsang/PycharmProjects/own_spinn/.data/sst/plain/train.txt");
	    	File in2 = new File("/home/galsang/PycharmProjects/own_spinn/.data/sst/trees/train.txt");
	    	File out = new File("/home/galsang/PycharmProjects/own_spinn/.data/sst/parsed/train.jsonl");

	    	FileReader filereader1 = new FileReader(in1);
	    	BufferedReader bufReader1 = new BufferedReader(filereader1);
	    	FileReader filereader2 = new FileReader(in2);
	    	BufferedReader bufReader2 = new BufferedReader(filereader2);
	    	BufferedWriter bufWriter = new BufferedWriter(new FileWriter(out));
	    	
	    	String line = "";
	    	while((line = bufReader1.readLine()) != null) {
	    		JSONParser jParser = new JSONParser();
	    		// System.out.println(line);
	    		JSONObject json = (JSONObject) jParser.parse(line);
	    		//JSONObject json = (JSONObject) new JSONObject(); 
	    		
	    		List<HasWord> tokens = sentenceToTokens((String) json.get("sentence"), false);
	    		Tree tree = parser.apply(tokens);
	    	    Tree binarized = binarizer.transformTree(tree);
	    	    Tree collapsedUnary = transformer.transformTree(binarized);

	    	    json.put("tagged_parse", collapsedUnary.toString());
	    	    String original = bufReader2.readLine();
	    	    json.put("original_parse", original);
	    	    
	    		/*
	    		List<HasWord> tokens = sentenceToTokens((String) json.get("sentence1"), true);
	    		Tree tree = parser.apply(tokens);
	    	    Tree binarized = binarizer.transformTree(tree);
	    	    Tree collapsedUnary = transformer.transformTree(binarized);
	    	    json.put("sentence1_parse", collapsedUnary.toString());
	    	    json.put("sentence1_simple_parse", simplify(collapsedUnary).toString());
	    	    json.remove("sentence1_binary_parse");
	    	    
	    	    tokens = sentenceToTokens((String) json.get("sentence2"), true);
	    		tree = parser.apply(tokens);
	    	    binarized = binarizer.transformTree(tree);
	    	    collapsedUnary = transformer.transformTree(binarized);
	    	    json.put("sentence2_parse", collapsedUnary.toString());
	    	    json.put("sentence2_simple_parse", simplify(collapsedUnary).toString());
	    	    json.remove("sentence2_binary_parse");
	    	    */

	    	    //System.out.println(collapsedUnary.pennString());
	    	    //bufWriter.write(collapsedUnary.toString());
	    	    
	    	    System.out.println(json.toJSONString());
	    	    bufWriter.write(json.toJSONString());
	    	    bufWriter.write("\n");
	    	}
	    	bufReader1.close();
	    	bufReader2.close();
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
