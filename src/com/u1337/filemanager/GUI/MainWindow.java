package com.u1337.filemanager.GUI;

import com.u1337.filemanager.FileManager.FSObject;
import com.u1337.filemanager.FileManager.FileManager;
import com.u1337.filemanager.Main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class MainWindow extends JFrame{

    private JFrame mFrame;
    private JTree mTree = new JTree();
    private JSplitPane mSplitPane;
    private JTable mTable;
    private JScrollPane mTableScroll;
    private JToolBar mToolbar;

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 500;
    public static final int TREE_WIDTH = 200;
    public static final Color TABLE_BORDER_COLOR = new Color(0xffffff);

    private static final int MIN_WINDOW_WIDTH = 430;
    private static final int MIN_WINDOW_HEIGHT = 200;

    private static final String BUTTON_DELETE_ACTION = "delete";
    private static final String BUTTON_MAKEDIR_ACTION = "makedir";
    private static final String BUTTON_MAKEFILE_ACTION = "makefile";
    private static final String BUTTON_COPY_ACTION = "copy";
    private static final String BUTTON_REMOVE_ACTION = "remove";
    private static final String BUTTON_INSERT_ACTION = "insert";
    private static final String BUTTON_UP_ACTION = "up";

    private final String[] COLUMNS = {"", "Name", "Size", "Last modified"};
    private static final int COLUMNS_NUM = 4;

    private FileManager mFileManager = new FileManager();

    public MainWindow()
    {
    }

    public void init()
    {
        // Window

        mFrame = new JFrame(Main.APP_NAME);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mSplitPane = new JSplitPane();
        mFrame.getContentPane().add(mSplitPane);

        mFrame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        mFrame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));

        // Toolbar

        mToolbar = new JToolBar();
        mToolbar.setPreferredSize(new Dimension(MIN_WINDOW_WIDTH, 52));
        mToolbar.setFloatable(false);

        mFrame.add(mToolbar, BorderLayout.PAGE_START);

        mToolbar.add(makeButton(BUTTON_MAKEFILE_ACTION, "create new file"));
        mToolbar.add(makeButton(BUTTON_MAKEDIR_ACTION, "create new directory"));
        mToolbar.add(makeButton(BUTTON_UP_ACTION, "go to parent directory"));
        mToolbar.add(makeButton(BUTTON_COPY_ACTION, "copy to clipboard"));
        mToolbar.add(makeButton(BUTTON_REMOVE_ACTION, "remove to clipboard"));
        mToolbar.add(makeButton(BUTTON_INSERT_ACTION, "insert from clipboard"));
        mToolbar.add(makeButton(BUTTON_DELETE_ACTION, "delete"));

        // JTree

        mTree = new JTree(mFileManager.mRoots.toArray());
        mTree.setCellRenderer(new FSRenderer());
        mTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                System.out.println(e.toString());

                if (e.getClickCount() == 2) {

                    Point p = e.getPoint();
                    TreePath tp = mTree.getPathForLocation(p.x, p.y);

                    if (tp != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) (tp.getLastPathComponent());

                        FSObject obj = (FSObject) node.getUserObject();

                        if (!obj.getDelayedStatus() && !obj.getOpenedStatus()) {
                            loadDirectory(obj, node);
                        }

                        obj.setOpenedStatus(!obj.getOpenedStatus());
                    }
                }
            }
        });
        mSplitPane.setLeftComponent(new JScrollPane(mTree));
        mSplitPane.getLeftComponent().setMinimumSize(new Dimension(TREE_WIDTH, WINDOW_HEIGHT));

        // JTable

        mTable = new JTable() {

            @Override
            public Class getColumnClass(int column) {
                Object obj = getValueAt(0, column);
                return obj.getClass();
            }
        };

        mTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                System.out.println(e.toString());

                int row = mTable.rowAtPoint(e.getPoint());
                int col = mTable.columnAtPoint(e.getPoint());

                if (row < 0 || col < 0)
                    return;

                if (e.getClickCount() == 2) {

                    FSObject obj = (FSObject) mTable.getModel().getValueAt(row, 1);

                    if (obj.isDirectory() && !obj.getDelayedStatus()) {

                        FSObject objDir = obj;

                        DefaultMutableTreeNode tNode = findTreeNodeByObj(mTree,
                                objDir);

                        loadDirectory(obj, tNode);
                    }
                    else
                    {
                        try {
                            Desktop.getDesktop().open(obj.getFile());
                        } catch (IOException err) {
                            showMessage("Couldn't open file");
                        }
                    }
                }
            }
        });

        repaintTable(mFileManager.mRootsObject);

        mTableScroll = new JScrollPane(mTable);
        mSplitPane.setRightComponent(mTableScroll);

        mFrame.pack();
        mFrame.setVisible(true);
    }

    private JButton makeButton(String action, String toolTip) {

        URL imageURL = ResourcesManager.getPngResource(action);

        JButton button = new JButton();
        button.setActionCommand(action);
        button.setToolTipText(toolTip);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String cmd = actionEvent.getActionCommand();

                switch (cmd)
                {
                    case MainWindow.BUTTON_UP_ACTION: {
                        moveUP();
                        break;
                    }
                    case MainWindow.BUTTON_MAKEFILE_ACTION: {
                        makefile();
                        break;
                    }
                    case MainWindow.BUTTON_MAKEDIR_ACTION: {
                        makedir();
                        break;
                    }
                    case MainWindow.BUTTON_COPY_ACTION: {
                        copy();
                        break;
                    }
                    case MainWindow.BUTTON_REMOVE_ACTION: {
                        remove();
                        break;
                    }
                    case MainWindow.BUTTON_INSERT_ACTION: {
                        insert();
                        break;
                    }
                    case MainWindow.BUTTON_DELETE_ACTION: {
                        delete();
                        break;
                    }

                    default: break;
                }
            }
        });

        if (imageURL != null) {
            button.setIcon(new ImageIcon(imageURL));
        } else {
            showMessage("Package is broken. Exit...");
            System.out.println("Package broken, resource not found");
            System.exit(0);
        }

        return button;
    }

    private void releaseDirectory(FSObject directory, DefaultMutableTreeNode node)
    {
        directory.setDelayedStatus(false);

        repaintTable(directory);

        if (node != null)
            repaintTree(directory, node);
    }

    public void loadDirectory(FSObject directory, DefaultMutableTreeNode node)
    {
        if (directory != null) {

            directory.setDelayedStatus(true);
            mTree.repaint();

            mFileManager.loadAndActivate(directory);

            Timer tTimer = new Timer();
            TimerTask tTask = new TimerTask() {
                @Override
                public void run() {
                    releaseDirectory(directory, node);
                }
            };
            tTimer.schedule(tTask, 2 * 1000);
        }
    }

    public void showMessage(String s)
    {
        JOptionPane.showMessageDialog(mFrame, s);
    }

    private void repaintTable(FSObject obj)
    {
        Object[][] data = MainWindow.prepareData(obj.getChildren());
        DefaultTableModel tModel = new DefaultTableModel(data, this.COLUMNS)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        mTable.setModel(tModel);
        mTable.getColumnModel().getColumn(0).setMaxWidth(ResourcesManager.CLOSED_DIRECTORY_ICON.getIconWidth());

    }

    private void repaintTree(FSObject parent, DefaultMutableTreeNode parentNode)
    {
        boolean isLeaf = parentNode.isLeaf();
        TreePath tmPath = new TreePath(parentNode.getPath());

        if (!mTree.isExpanded(tmPath) && !isLeaf)
            return;

        parentNode.setAllowsChildren(true);

        DefaultTreeModel tm = (DefaultTreeModel) mTree.getModel();

        parentNode.removeAllChildren();
        tm.reload(parentNode);

        for (FSObject o : parent.getChildren())
        {
            if (o.isDirectory()) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(o);
                tm.insertNodeInto(treeNode, parentNode, parentNode.getChildCount());
                tm.reload(treeNode);
            }
        }
        mTree.setModel(tm);

        mTree.expandPath(tmPath);
        mTree.repaint();
    }

    // Buttons

    public void moveUP()
    {
        FSObject obj = mFileManager.getCurrent();

        if (obj == null || obj.isRoot()) {
            showMessage("Couldn't load parent directory: this is root directory");
            return;
        }

        FSObject objF = obj.getParent();
        mFileManager.setCurrent(objF);
        loadDirectory(objF, findTreeNodeByObj(mTree, objF));
    }

    public void makefile()
    {
        FSObject obj = mFileManager.getCurrent();

        if (obj == null) {
            showMessage("Couldn't create file: this is root directory");
            return;
        }

        String name = JOptionPane.showInputDialog(null, "Enter file name");
        name = name.trim();
        if (name.equals(""))
        {
            showMessage("File name can't be empty");
            return;
        }

        if (FileManager.makeFile(obj.getFile(), name))
        {
            mFileManager.loadAndActivate(obj);
            loadDirectory(obj, findTreeNodeByObj(mTree, obj));
        }
        else
        {
            showMessage("Error: couldn't create file");
        }
    }

    public void makedir()
    {
        FSObject obj = mFileManager.getCurrent();

        if (obj == null) {
            showMessage("Couldn't create directory: this is root directory");
            return;
        }

        String name = JOptionPane.showInputDialog(null, "Enter directory name");

        name = name.trim();
        if (name.equals("")) {
            showMessage("Directory name can't be empty");
            return;
        }

        if (FileManager.makeDir(obj.getFile(), name))
        {
            mFileManager.loadAndActivate(obj);
            loadDirectory(obj, findTreeNodeByObj(mTree, obj));
        }
        else
        {
            showMessage("Error: couldn't create directory");
        }
    }

    public void delete()
    {
        List<FSObject> list = getSelection(mTable);
        for (FSObject obj : list)
        {
            if (!mFileManager.delete(obj))
            {
                showMessage("Error: couldn't delete file ".concat(obj.getName()));
            }
        }

        FSObject obj = mFileManager.getCurrent();
        mFileManager.loadAndActivate(obj);
        loadDirectory(obj, findTreeNodeByObj(mTree, obj));
    }

    public void copy()
    {
        List<FSObject> list = getSelection(mTable);
        mFileManager.setClipboard(list, FileManager.CLIPBOARD_COPY);
    }

    public void remove()
    {
        List<FSObject> list = getSelection(mTable);
        mFileManager.setClipboard(list, FileManager.CLIPBOARD_MOVE);
    }

    public void insert()
    {
        List<FSObject> list = mFileManager.getClipboard();
        int type = mFileManager.getClipboardType();

        if (type == FileManager.CLIPBOARD_RESET)
        {
            showMessage("Clipboard is empty");
            return;
        }

        for (FSObject obj : list) {

            if (!mFileManager.insert(mFileManager.getCurrent(), obj, type))
            {
                showMessage("Error: couldn't complete operation - ".concat(obj.getName()));
            }

        }

        mFileManager.clearClipboard();

        FSObject obj = mFileManager.getCurrent();
        mFileManager.loadAndActivate(obj);
        loadDirectory(obj, findTreeNodeByObj(mTree, obj));
    }

    // staff

    public static Object[][] prepareData(List<FSObject> fs)
    {
        Object[][] data = new Object[fs.size()][MainWindow.COLUMNS_NUM];

        int c = 0;
        for (FSObject f : fs)
        {
            data[c][1] = f;
            if (f.isFile())
            {
                data[c][0] = ResourcesManager.FILE_ICON;

                float filesize = f.getFile().length();
                String suffix = "B";

                if (filesize >= 1024)
                {
                    filesize /= 1024;
                    suffix = "KB";

                    if (filesize >= 1024)
                    {
                        filesize /= 1024;
                        suffix = "MB";

                        if (filesize >= 1024)
                        {
                            filesize /= 1024;
                            suffix = "GB";
                        }
                    }
                }

                data[c][2] = String.format("%.1f", filesize).concat(" ").concat(suffix);
                data[c][3] = stamp2date(f.getFile().lastModified());
            }
            else
            {
                if (f.isRoot())
                    data[c][0] = ResourcesManager.HARD_DRIVE_ICON;
                else
                    data[c][0] = ResourcesManager.CLOSED_DIRECTORY_ICON;
                data[c][2] = "";
                data[c][3] = "";
            }
            c++;
        }
        return data;
    }

    public static String stamp2date(long t)
    {
        Date date = new Date(t);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(date).toString();
    }

    public List<FSObject> getSelection(JTable table)
    {
        List<FSObject> res = new ArrayList<>();
        for (int i : table.getSelectedRows())
        {
            res.add((FSObject) table.getValueAt(i, 1));
        }

        return res;
    }

    public DefaultMutableTreeNode findTreeNodeByObj(JTree tree, FSObject needle) {

        return findTreeNodeByObjR((DefaultMutableTreeNode) tree.getModel().getRoot(), needle);
    }

    public DefaultMutableTreeNode findTreeNodeByObjR(DefaultMutableTreeNode node, FSObject needle) {
        Object obj = node.getUserObject();
        if (obj.getClass() == needle.getClass() && needle.equals(obj))
            return node;

        Enumeration<DefaultMutableTreeNode> childs = node.children();

        for (DefaultMutableTreeNode childNode: Collections.list(childs)) {
            DefaultMutableTreeNode res = findTreeNodeByObjR(childNode, needle);
            if (res != null)
                return res;
        }
        return null;
    }

    private static class FSRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            FSObject fsvalue = null;

            if (userObject.getClass() == FSObject.class) {
                fsvalue = (FSObject) userObject;
            }

            if (fsvalue != null && fsvalue.getDelayedStatus()) {
                setIcon(ResourcesManager.LOADING_ICON);
            } else if (row == 0) {
                setIcon(ResourcesManager.HARD_DRIVE_ICON);
            } else {
                if (leaf || !expanded) {
                    setIcon(ResourcesManager.CLOSED_DIRECTORY_ICON);
                } else {
                    setIcon(ResourcesManager.OPENED_DIRECTORY_ICON);
                }
            }

            return this;
        }

    }

}
