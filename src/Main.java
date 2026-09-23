import java.util.*;
import java.io.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

class Node{
    int data;
    int h;
    Node left,right;

    public Node(int d){
        this.data=d;
        h=1;
    }
}

class Tree{
    Node root=null;
    int sz=0;

    void insert(int x){
        root=insertH(root,x);
        sz++;
    }

    int size(){
        return sz;
    }

    void inOrder(){
        inOrderHelper(root);
    }

    private Node insertH(Node root,int x){
        if(root==null)
            return new Node(x);

        if(x<root.data)
            root.left=insertH(root.left,x);
        else
            root.right=insertH(root.right,x);

        updateHeight(root);

        if(getBF(root)==-2){ //Right Heavy
            if(getBF(root.right)==-1){ //RR
                root=leftRotate(root);
            }
            else{ //RL
                root.right=rightRotate(root.right);
                root=leftRotate(root);
            }
        }
        else if(getBF(root)==2){ //Left Heavy
            if(getBF(root.left)==1){ //LL
                root=rightRotate(root);
            }
            else{ //LR
                root.left=leftRotate(root.left);
                root=rightRotate(root);
            }
        }

        return root;
    }

    Node rightRotate(Node root){
        Node temp=root.left;
        root.left=temp.right;
        temp.right=root;

        updateHeight(root);
        updateHeight(temp);

        return temp;
    }

    Node leftRotate(Node root){
        Node temp=root.right;
        root.right=temp.left;
        temp.left=root;

        updateHeight(root);
        updateHeight(temp);

        return temp;
    }

    void updateHeight(Node root){
        root.h=1+Math.max(root.left==null ? 0 : root.left.h, root.right==null ? 0 : root.right.h);
    }

    int getBF(Node root){
        return (root.left==null ? 0 : root.left.h) - (root.right==null ? 0 : root.right.h);
    }

    private void inOrderHelper(Node r){
        if(r==null)
            return;

        inOrderHelper(r.left);
        System.out.print(r.data + " ");
        inOrderHelper(r.right);
    }

    void printTreeLikeATree(){
        TreePrinter<Node> tp=new TreePrinter<Node>(n -> "" + n.data , n -> n.left , n -> n.right);
        tp.printTree(root);
    }
}

class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        Tree t1=new Tree();

        while(sc.hasNextInt()){
            t1.insert(sc.nextInt());
        }

        t1.printTreeLikeATree();
    }
}

class TreePrinter<T> {

    private Function<T, String> getLabel;
    private Function<T, T> getLeft;
    private Function<T, T> getRight;

    private PrintStream outStream = System.out;

    private boolean squareBranches = false;
    private boolean lrAgnostic = false;
    private int hspace = 2;
    private int tspace = 1;

    public TreePrinter(Function<T, String> getLabel, Function<T, T> getLeft, Function<T, T> getRight) {
        this.getLabel = getLabel;
        this.getLeft = getLeft;
        this.getRight = getRight;
    }

    public void setPrintStream(PrintStream outStream) {
        this.outStream = outStream;
    }

    public void setSquareBranches(boolean squareBranches) {
        this.squareBranches = squareBranches;
    }

    public void setLrAgnostic(boolean lrAgnostic) {
        this.lrAgnostic = lrAgnostic;
    }

    public void setHspace(int hspace) {
        this.hspace = hspace;
    }

    public void setTspace(int tspace) {
        this.hspace = tspace;
    }

    /*
        Prints ascii representation of binary tree.
        Parameter hspace is minimum number of spaces between adjacent node labels.
        Parameter squareBranches, when set to true, results in branches being printed with ASCII box
        drawing characters.
    */
    public void printTree(T root) {
        List<TreeLine> treeLines = buildTreeLines(root);
        printTreeLines(treeLines);
    }

    /*
        Prints ascii representations of multiple trees across page.
        Parameter hspace is minimum number of spaces between adjacent node labels in a tree.
        Parameter tspace is horizontal distance between trees, as well as number of blank lines
        between rows of trees.
        Parameter lineWidth is maximum width of output
        Parameter squareBranches, when set to true, results in branches being printed with ASCII box
        drawing characters.
    */
    public void printTrees(List<T> trees, int lineWidth) {
        List<List<TreeLine>> allTreeLines = new ArrayList<>();
        int[] treeWidths = new int[trees.size()];
        int[] minLeftOffsets = new int[trees.size()];
        int[] maxRightOffsets = new int[trees.size()];

        for (int i = 0; i < trees.size(); i++) {
            T treeNode = trees.get(i);
            List<TreeLine> treeLines = buildTreeLines(treeNode);
            allTreeLines.add(treeLines);
            minLeftOffsets[i] = minLeftOffset(treeLines);
            maxRightOffsets[i] = maxRightOffset(treeLines);
            treeWidths[i] = maxRightOffsets[i] - minLeftOffsets[i] + 1;
        }

        int nextTreeIndex = 0;

        while (nextTreeIndex < trees.size()) {
            // print a row of trees starting at nextTreeIndex

            // first figure range of trees we can print for next row
            int sumOfWidths = treeWidths[nextTreeIndex];
            int endTreeIndex = nextTreeIndex + 1;

            while (endTreeIndex < trees.size() && sumOfWidths + tspace + treeWidths[endTreeIndex] < lineWidth) {
                sumOfWidths += (tspace + treeWidths[endTreeIndex]);
                endTreeIndex++;
            }

            endTreeIndex--;

            // find max number of lines for tallest tree
            int maxLines = allTreeLines.stream().mapToInt(list -> list.size()).max().orElse(0);

            // print trees line by line
            for (int i = 0; i < maxLines; i++) {
                for (int j = nextTreeIndex; j <= endTreeIndex; j++) {
                    List<TreeLine> treeLines = allTreeLines.get(j);

                    if (i >= treeLines.size()) {
                        System.out.print(spaces(treeWidths[j]));
                    }
                    else {
                        int leftSpaces = -(minLeftOffsets[j] - treeLines.get(i).leftOffset);
                        int rightSpaces = maxRightOffsets[j] - treeLines.get(i).rightOffset;
                        System.out.print(spaces(leftSpaces) + treeLines.get(i).line + spaces(rightSpaces));
                    }

                    if (j < endTreeIndex)
                        System.out.print(spaces(tspace));
                }

                System.out.println();
            }

            for (int i = 0; i < tspace; i++) {
                System.out.println();
            }

            nextTreeIndex = endTreeIndex + 1;
        }
    }

    private void printTreeLines(List<TreeLine> treeLines) {
        if (treeLines.size() > 0) {
            int minLeftOffset = minLeftOffset(treeLines);
            int maxRightOffset = maxRightOffset(treeLines);

            for (TreeLine treeLine : treeLines) {
                int leftSpaces = -(minLeftOffset - treeLine.leftOffset);
                int rightSpaces = maxRightOffset - treeLine.rightOffset;
                outStream.println(spaces(leftSpaces) + treeLine.line + spaces(rightSpaces));
            }
        }
    }

    private List<TreeLine> buildTreeLines(T root) {
        if (root == null)
            return Collections.emptyList();
        else {
            String rootLabel = getLabel.apply(root);

            List<TreeLine> leftTreeLines = buildTreeLines(getLeft.apply(root));
            List<TreeLine> rightTreeLines = buildTreeLines(getRight.apply(root));

            int leftCount = leftTreeLines.size();
            int rightCount = rightTreeLines.size();
            int minCount = Math.min(leftCount, rightCount);
            int maxCount = Math.max(leftCount, rightCount);

            int maxRootSpacing = 0;

            for (int i = 0; i < minCount; i++) {
                int spacing = leftTreeLines.get(i).rightOffset - rightTreeLines.get(i).leftOffset;

                if (spacing > maxRootSpacing)
                    maxRootSpacing = spacing;
            }

            int rootSpacing = maxRootSpacing + hspace;

            if (rootSpacing % 2 == 0)
                rootSpacing++;

            List<TreeLine> allTreeLines = new ArrayList<>();

            String renderedRootLabel = rootLabel.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");

            allTreeLines.add(new TreeLine(
                    rootLabel,
                    -(renderedRootLabel.length() - 1) / 2,
                    renderedRootLabel.length() / 2
            ));

            int leftTreeAdjust = 0;
            int rightTreeAdjust = 0;

            if (leftTreeLines.isEmpty()) {
                if (!rightTreeLines.isEmpty()) {
                    // there's a right subtree only
                    if (squareBranches) {
                        if (lrAgnostic) {
                            allTreeLines.add(new TreeLine("│", 0, 0));
                        }
                        else {
                            allTreeLines.add(new TreeLine("└┐", 0, 1));
                            rightTreeAdjust = 1;
                        }
                    }
                    else {
                        allTreeLines.add(new TreeLine("\\", 1, 1));
                        rightTreeAdjust = 2;
                    }
                }
            }
            else if (rightTreeLines.isEmpty()) {
                // there's a left subtree only
                if (squareBranches) {
                    if (lrAgnostic) {
                        allTreeLines.add(new TreeLine("│", 0, 0));
                    }
                    else {
                        allTreeLines.add(new TreeLine("┌┘", -1, 0));
                        leftTreeAdjust = -1;
                    }
                }
                else {
                    allTreeLines.add(new TreeLine("/", -1, -1));
                    leftTreeAdjust = -2;
                }
            }
            else {
                // there's a left and right subtree
                if (squareBranches) {
                    int adjust = (rootSpacing / 2) + 1;
                    String horizontal = String.join("", Collections.nCopies(rootSpacing / 2, "─"));
                    String branch = "┌" + horizontal + "┴" + horizontal + "┐";

                    allTreeLines.add(new TreeLine(branch, -adjust, adjust));

                    rightTreeAdjust = adjust;
                    leftTreeAdjust = -adjust;
                }
                else {
                    if (rootSpacing == 1) {
                        allTreeLines.add(new TreeLine("/ \\", -1, 1));
                        rightTreeAdjust = 2;
                        leftTreeAdjust = -2;
                    }
                    else {
                        for (int i = 1; i < rootSpacing; i += 2) {
                            String branches = "/" + spaces(i) + "\\";
                            allTreeLines.add(new TreeLine(
                                    branches,
                                    -((i + 1) / 2),
                                    (i + 1) / 2
                            ));
                        }

                        rightTreeAdjust = (rootSpacing / 2) + 1;
                        leftTreeAdjust = -((rootSpacing / 2) + 1);
                    }
                }
            }

            // now add joined lines of subtrees, with appropriate number of separating spaces, and adjusting offsets

            for (int i = 0; i < maxCount; i++) {
                TreeLine leftLine, rightLine;

                if (i >= leftTreeLines.size()) {
                    // nothing remaining on left subtree
                    rightLine = rightTreeLines.get(i);

                    rightLine.leftOffset += rightTreeAdjust;
                    rightLine.rightOffset += rightTreeAdjust;

                    allTreeLines.add(rightLine);
                }
                else if (i >= rightTreeLines.size()) {
                    // nothing remaining on right subtree
                    leftLine = leftTreeLines.get(i);

                    leftLine.leftOffset += leftTreeAdjust;
                    leftLine.rightOffset += leftTreeAdjust;

                    allTreeLines.add(leftLine);
                }
                else {
                    leftLine = leftTreeLines.get(i);
                    rightLine = rightTreeLines.get(i);

                    int adjustedRootSpacing = (rootSpacing == 1 ? (squareBranches ? 1 : 3) : rootSpacing);

                    TreeLine combined = new TreeLine(
                            leftLine.line + spaces(adjustedRootSpacing - leftLine.rightOffset + rightLine.leftOffset) + rightLine.line,
                            leftLine.leftOffset + leftTreeAdjust,
                            rightLine.rightOffset + rightTreeAdjust
                    );

                    allTreeLines.add(combined);
                }
            }

            return allTreeLines;
        }
    }

    private static int minLeftOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.leftOffset).min().orElse(0);
    }

    private static int maxRightOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.rightOffset).max().orElse(0);
    }

    private static String spaces(int n) {
        return String.join("", Collections.nCopies(n, " "));
    }

    private static class TreeLine {
        String line;
        int leftOffset;
        int rightOffset;

        TreeLine(String line, int leftOffset, int rightOffset) {
            this.line = line;
            this.leftOffset = leftOffset;
            this.rightOffset = rightOffset;
        }
    }
}