import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Stack;


public class Homework1 extends JPanel
		implements TreeSelectionListener {
	private JEditorPane htmlPane;
	private JTree jTree;

	//Optionally play with line styles.  Possible values are
	//"Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;
	public static Node root;
	public static String Screen;

	public Homework1() {
		super(new GridLayout(1,0));
		//Create the nodes.
		DefaultMutableTreeNode top =
				new DefaultMutableTreeNode(root);
		createNodes(root,top);

		//Create a tree that allows one selection at a time.
		jTree = new JTree(top);
		jTree.getSelectionModel().setSelectionMode
				(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		jTree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			jTree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		//Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(jTree);

		//Create the HTML viewing pane.
		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		//Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100);
		splitPane.setPreferredSize(new Dimension(500, 300));
		// Icon
		ImageIcon leafIcon = createImageIcon("middle.gif");
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer =
					new DefaultTreeCellRenderer();
			renderer.setClosedIcon(leafIcon);
			renderer.setOpenIcon(leafIcon);
			jTree.setCellRenderer(renderer);
		}

		add(splitPane);
	}

	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Homework1.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find file!!!");
			return null;
		}
	}
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				jTree.getLastSelectedPathComponent();

		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		DisplayNode((Node)nodeInfo);

	}

	public void DisplayNode(Node n)
	{
		SetScreen(n);
		if(isOperator(n.chr))
		{
			Screen=Screen+"="+calculate(n);
		}
		htmlPane.setText(Screen);
	}

	public static void createNodes(Node n, DefaultMutableTreeNode top){

		if(n.right_Node!=null)
		{
			DefaultMutableTreeNode Right=new DefaultMutableTreeNode(n.right_Node);
			top.add(Right);
			createNodes(n.right_Node,Right);
		}
		if(n.left_Node!=null)
		{
			DefaultMutableTreeNode left=new DefaultMutableTreeNode(n.left_Node);
			top.add(left);
			createNodes(n.left_Node, left);
		}

	}

	public static void SetScreen(Node n)
	{
		Screen = "";
		if (isOperator(n.chr))
		{
			SetScreen2(n.left_Node);
			Screen += n.chr;
			SetScreen2(n.right_Node);
		}else{
			Screen += n.chr;
		}
	}

	public static void SetScreen2(Node n)
	{
		if (isOperator(n.chr))
		{
			Screen += "(";
			SetScreen2(n.left_Node);
			Screen += n.chr;
			SetScreen2(n.right_Node);
			Screen += ")";
		}else{
			Screen += n.chr;
		}
	}
	private static void createAndShowGUI() {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}


		JFrame frame = new JFrame("Binary Tree Calculator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Homework1());

		frame.pack();
		frame.setVisible(true);
	}

	static String[] Arr ;

	static public void main(String[] args) {

		root = new Node(args[0]);
		Arr = root.str.split("");

		String infixOperand = inorder(infix(root));
		System.out.print(infixOperand.substring(1, infixOperand.length()-1));
		System.out.print("=" + calculate(infix(root)));
		root = infix(root);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	static public Node infix(Node n){
		Node temp1, temp2, temp3;
		Stack stack = new Stack();
		for(int i=0;i<n.str.length(); i++) {
			char c = n.str.charAt(i);
			if(isOperator(c)) {
				temp1 = new Node(c);
				temp2 = (Node) stack.pop();
				temp3 = (Node) stack.pop();
				temp1.right_Node = temp2;
				temp1.left_Node = temp3;
				stack.push(temp1);
			} else {
				temp1 = new Node(c);
				stack.push(temp1);
			}
		}
		return (Node) stack.pop();
	}

	static public String inorder(Node n){
		if(isOperator(n.chr)){
			return "(" + (inorder(n.left_Node) + n.chr + inorder(n.right_Node)) + ")";
		}else{
			return (n.chr+"");
		}
	}

	static public int calculate(Node n){
		if(isOperator(n.chr)) {
			if (n.chr == '+') {
				return calculate(n.left_Node) + calculate(n.right_Node);
			}else if (n.chr == '-') {
				return calculate(n.left_Node) - calculate(n.right_Node);
			}else if (n.chr == '*') {
				return calculate(n.left_Node) * calculate(n.right_Node);
			}else if (n.chr == '/') {
				return calculate(n.left_Node) / calculate(n.right_Node);
			}
		}
		return  n.chr - '0';
	}

	static public class Node{
		char chr;
		String str;
		Node right_Node,left_Node;

		public Node(char e) {
			this.chr = e;
		}
		public Node(String f) {
			this.str = f;
		}
		public String toString(){
			return  chr+"";
		}
	}

	static public boolean isOperator(char x) {
		if (x == '+') {
			return true;
		} else if (x == '-') {
			return true;
		} else if (x == '*') {
			return true;
		} else if (x == '/') {
			return true;
		} else return false;
	}
	}