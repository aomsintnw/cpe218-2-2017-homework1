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
	private JTree tree1;
	private JEditorPane htmlPane;
	public static String text;
	public static Node head;
	private static boolean playWithLineStyle = false;
	private static boolean useSystemLookAndFeel = false;
	private static String lineStyle = "Horizontal";

	public Homework1() {
		super(new GridLayout(1,0));
		DefaultMutableTreeNode top =
				new DefaultMutableTreeNode(head);
		createNodes(head,top);


		tree1 = new JTree(top);
		tree1.getSelectionModel().setSelectionMode
				(TreeSelectionModel.SINGLE_TREE_SELECTION);


		tree1.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			tree1.putClientProperty("JTree.lineStyle", lineStyle);
		}


		JScrollPane treeView = new JScrollPane(tree1);


		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);


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
			tree1.setCellRenderer(renderer);
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
				tree1.getLastSelectedPathComponent();

		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		DisplayNode((Node)nodeInfo);

	}

	public void DisplayNode(Node n)
	{
		SetScreen(n);
		if(isOperator(n.chr))
		{
			text=text+"="+calculate(n);
		}
		htmlPane.setText(text);
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
		text = "";
		if (isOperator(n.chr))
		{
			SetScreen2(n.left_Node);
			text += n.chr;
			SetScreen2(n.right_Node);
		}else{
			text += n.chr;
		}
	}

	public static void SetScreen2(Node n)
	{
		if (isOperator(n.chr))
		{
			text += "(";
			SetScreen2(n.left_Node);
			text += n.chr;
			SetScreen2(n.right_Node);
			text += ")";
		}else{
			text += n.chr;
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

		head = new Node(args[0]);
		Arr = head.str.split("");

		String infixOperand = inorder(infix(head));
		System.out.print(infixOperand.substring(1, infixOperand.length()-1));
		System.out.print("=" + calculate(infix(head)));
		head = infix(head);
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