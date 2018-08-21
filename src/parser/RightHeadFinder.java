package parser;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;

public class RightHeadFinder implements HeadFinder {
  private static final long serialVersionUID = 8453889846239508208L;

  public Tree determineHead(Tree t) {
    if (t.isLeaf()) {
      return null;
    } else {
    	if (t.children().length > 1) {
    		return t.children()[1];
    	}
    	else {
    		return t.children()[0];
    	}
    }
  }

  public Tree determineHead(Tree t, Tree parent) {
    return determineHead(t);
  }
}