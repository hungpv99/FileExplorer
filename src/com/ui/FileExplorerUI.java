package com.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JToolBar;
import javax.swing.Icon;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileExplorerUI extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private FileSystemView fileSystemView;

	private FileTableModel fileTableModel;

	private String folderSource = "";
	private String folderTarget = "";
	private boolean isCopy = false;

	private boolean cellSizesSet = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileExplorerUI frame = new FileExplorerUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileExplorerUI() {

		// code auto generation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		// code auto generation

		//==========================Tool bar==============
		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);
		
		//handle action click copy
		JButton btnCopy = new JButton("Copy");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!folderSource.equals("")) {
					JOptionPane.showMessageDialog(null, "Copied folder, please choose target folder then click Paste!!!");
					isCopy = true;
				} else {
					JOptionPane.showMessageDialog(null, "You haven't choosen folder yet !!!");
				}
			}
		});
		
		JButton btnOpen = new JButton("Open");
		toolBar.add(btnOpen);
		btnOpen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!folderSource.equals("")) {
					File file = new File(folderSource);
					if(file.isFile()) {
						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.open(file);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else {
						JOptionPane.showMessageDialog(null, "This is not a file !!!");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "You haven't choosen file yet !!!");
				}
			}
		});
		
		JButton btnNewFile = new JButton("New File");
		toolBar.add(btnNewFile);
		btnNewFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!folderSource.equals("")) {
					File file = new File(folderSource);
					if(file.isDirectory()) {
						//TODO create new file
						String nameFile = JOptionPane.showInputDialog(null, "Please enter name's file:");
						File newFile = new File(folderSource, nameFile);
						if(newFile.exists()) {
							JOptionPane.showMessageDialog(null, "The name's file is exists !!!");
						}else {
							try {
								newFile.createNewFile();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}else {
						JOptionPane.showMessageDialog(null, "This is not a folder !!!");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "You haven't choosen parent file !!!");
				}
			}
		});
		
		JButton btnEditName = new JButton("Edit Name");
		toolBar.add(btnEditName);
		btnEditName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!folderSource.equals("")) {
					String newName = JOptionPane.showInputDialog(null, "Please enter a new name:");
					String parentPath = folderSource.substring(0,folderSource.lastIndexOf("\\"));
					
					File file = new File(folderSource);
					if(file.isFile()) {
						String extension = folderSource.substring(folderSource.lastIndexOf("."));
						newName = newName+extension;
						File newFile = new File(parentPath,newName);
						if(newFile.exists()) {
							JOptionPane.showMessageDialog(null, "The name's file is exists !!!");
						}else {
							boolean success = file.renameTo(newFile);
							if(!success) {
								JOptionPane.showMessageDialog(null, "File haven't changed own name yet !!!");
							}else {
								JOptionPane.showMessageDialog(null, "Successfully !!!");
								System.out.println(parentPath);
								File[] files = file.getParentFile().listFiles();
								setTableData(files);
							}
						}
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "You haven't choosen parent file !!!");
				}
			}
		});
		
		JButton btnDelete = new JButton("Delete");
		toolBar.add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!folderSource.equals("")) {
					File file = new File(folderSource);
					int input = JOptionPane.showConfirmDialog(null, "Do you really want to delete file?");
					if(input == 0) {
						File parentFile = file.getParentFile();
						file.delete();
						File[] files = parentFile.listFiles();
						setTableData(files);
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "You haven't choosen parent file !!!");
				}
			}
		});
		
		toolBar.add(btnCopy);
		
		
		//handle action click paste
		JButton btnPaste = new JButton("Paste");
		btnPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isCopy) {
					int index = folderSource.lastIndexOf("\\");
					copyFolder(new File(folderSource), new File(folderTarget + folderSource.substring(index)));
					isCopy = false;
					JOptionPane.showMessageDialog(null, "Copy successfuly !!!");
				}
				else {
					JOptionPane.showMessageDialog(null, "You haven't choosen folder yet !!!");
				}
			}
		});
		toolBar.add(btnPaste);
		//========================End Tool Bar=============================
		

		//===========================Main pane===============================
		//split pane into 2 parts
		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);

		//--------------------------Tree---------------------------
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);
		
		//get root file
		fileSystemView = FileSystemView.getFileSystemView();
		File[] roots = fileSystemView.getRoots();
		
		//add file into tree
		for (File fileSystemRoot : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);

			File[] files = fileSystemView.getFiles(fileSystemRoot, true);
			for (File file : files) {
				if (file.isDirectory()) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
		}

		tree = new JTree(treeModel);
		// handle action listener when click tree node
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) arg0.getPath().getLastPathComponent();
				showChildren(node);
			}
		});
		tree.setRootVisible(false);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.expandRow(0);
		splitPane.setLeftComponent(tree);
		//----------------------End Tree----------------
		

		//---------------Table-------------------
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = table.rowAtPoint(arg0.getPoint());
				// int col = table.columnAtPoint(arg0.getPoint());
				if (row >= 0) {
					if (!isCopy) {
						folderSource = (String) table.getModel().getValueAt(row, 2);
					} else {
						folderTarget = (String) table.getModel().getValueAt(row, 2);
					}
				}
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.setShowVerticalLines(false);
		splitPane.setRightComponent(table);
		//------------------------End Table--------------
		
		//================================End Main Pane===============
	}

	
	//show children of tree
	private void showChildren(final DefaultMutableTreeNode node) {
		//accept handling of response from tree
		tree.setEnabled(false);

		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); 
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
								//after publish folder will be handled by function process
							}
						}
					}
					//show data into table
					setTableData(files);
				}
				return null;
			}

			@Override
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					//add node into tree
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			protected void done() {
				//mark is done
				tree.setEnabled(true);
			}
		};
		//execute thread
		worker.execute();
	}

	private void setTableData(final File[] files) {
		// create thread to set data for table
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				fileTableModel.setFiles(files);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// Increase height of row to better for icons
					table.setRowHeight(icon.getIconHeight() + 10);

					setColumnWidth(0, -1);
//					setColumnWidth(3, 60);
//					table.getColumnModel().getColumn(3).setMaxWidth(120);
					setColumnWidth(3, -1);

					// mark has changed the width of the column to avoid to repeat
					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		// get column based on index
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			// get label of column
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			// get size of label
			Dimension preferred = label.getPreferredSize();
			// Increase width of column
			width = (int) preferred.getWidth() + 14;
		}
		// set width for column
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	public static void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} catch (Exception e) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
