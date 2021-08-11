package com.vijay.jsonwizard.customviews;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.VisibleForTesting;
import android.view.Window;
import android.widget.LinearLayout;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.vijay.jsonwizard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TreeViewDialog extends Dialog implements TreeNode.TreeNodeClickListener {
    private static final String KEY_NODES = "nodes";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_NAME = "name";
    private static final String KEY_KEY = "key";
    private final Context context;
    private boolean shouldExpandAllNodes = false;
    private ArrayList<String> value;
    private ArrayList<String> name;
    private HashMap<TreeNode, String> treeNodeHashMap;
    private ArrayList<String> defaultValue;

    public TreeViewDialog(Context context, JSONArray structure, ArrayList<String> defaultValue, ArrayList<String> value)
            throws
            JSONException {
        super(context);
        this.context = context;
        init(structure, defaultValue, value);
    }

    public TreeViewDialog(Context context, int theme, JSONArray structure, ArrayList<String> defaultValue,
                          ArrayList<String> value) throws JSONException {
        super(context, theme);
        this.context = context;
        init(structure, defaultValue, value);
    }

    protected TreeViewDialog(Context context, boolean cancelable, OnCancelListener
            cancelListener, JSONArray structure, ArrayList<String> defaultValue, ArrayList<String> value)
            throws JSONException {
        super(context, cancelable, cancelListener);
        this.context = context;
        init(structure, defaultValue, value);
    }

    private static void retrieveValue(HashMap<TreeNode, String> treeNodeHashMap, TreeNode node,
                                      ArrayList<String> value) {
        if (node.getParent() != null) {
            value.add(getTreeNodeKey(treeNodeHashMap, node));
            retrieveValue(treeNodeHashMap, node.getParent(), value);
        }
    }

    private static String getTreeNodeKey(HashMap<TreeNode, String> treeNodeHashMap, TreeNode node) {
        if (treeNodeHashMap.containsKey(node)) {
            return treeNodeHashMap.get(node);
        }
        return null;
    }

    public boolean shouldExpandAllNodes() {
        return shouldExpandAllNodes;
    }

    public void setShouldExpandAllNodes(boolean shouldExpandAllNodes) {
        this.shouldExpandAllNodes = shouldExpandAllNodes;
    }

    private void setSelectedValue(TreeNode treeNode, int level, ArrayList<String> defaultValue,
                                  HashMap<TreeNode, String> treeNodeHashMap) {
        if (treeNode != null) {
            if (defaultValue != null && level >= 0 && level < defaultValue.size()) {
                String levelValue = defaultValue.get(level);
                String nodeValue = getTreeNodeKey(treeNodeHashMap, treeNode);
                if (nodeValue != null && nodeValue.equals(levelValue)) {
                    treeNode.setExpanded(true);
                    List<TreeNode> children = treeNode.getChildren();
                    for (TreeNode curChild : children) {
                        setSelectedValue(curChild, level + 1, defaultValue, treeNodeHashMap);
                    }
                    return;
                }
            }

            treeNode.setExpanded(shouldExpandAllNodes());
        }
    }

    public void init(JSONArray nodes, ArrayList<String> defaultValue,
                     final ArrayList<String> value) throws JSONException {
        this.defaultValue = defaultValue;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.native_form_dialog_tree_view);
        LinearLayout canvas = this.findViewById(R.id.canvas);


        this.value = new ArrayList<>();
        this.name = new ArrayList<>();
        this.treeNodeHashMap = new HashMap<>();

        JSONObject rootObject = new JSONObject();
        rootObject.put(KEY_KEY, "");
        rootObject.put(KEY_NAME, "");
        rootObject.put(KEY_LEVEL, "");
        rootObject.put(KEY_NODES, nodes);
        TreeNode rootNode = constructTreeView(rootObject, null, value == null || value.size() == 0 ? defaultValue : value);

        AndroidTreeView androidTreeView = new AndroidTreeView(context, rootNode);
        androidTreeView.setDefaultContainerStyle(R.style.TreeNodeStyle);

        canvas.addView(androidTreeView.getView());

        setValue(value);
    }

    private TreeNode constructTreeView(JSONObject structure, TreeNode parent, ArrayList<String> defaultValue) throws
            JSONException {
        String name = structure.optString(KEY_NAME, "");
        String key = structure.optString(KEY_KEY, "");
        TreeNode curNode = new TreeNode(name);
        treeNodeHashMap.put(curNode, key);
        curNode.setClickListener(this);
        curNode.setViewHolder(new SelectableItemHolder(context, structure.optString(KEY_LEVEL, "")));
        if (parent == null) {
            curNode.setSelectable(false);
        }
        if (structure.has(KEY_NODES)) {
            JSONArray options = structure.getJSONArray(KEY_NODES);
            for (int i = 0; i < options.length(); i++) {
                constructTreeView(options.getJSONObject(i), curNode, defaultValue);
            }
        }

        if (parent != null) {
            if (parent.getLevel() == 0) {
                setSelectedValue(curNode, 0, defaultValue, treeNodeHashMap);
            }
            parent.addChild(curNode);
        }

        return curNode;
    }

    private void extractName() {
        if (value != null && value.size() > 0) {
            HashMap<String, TreeNode> reverseHashMap = new HashMap<>();
            for (TreeNode curNode : treeNodeHashMap.keySet()) {
                reverseHashMap.put(treeNodeHashMap.get(curNode), curNode);
            }

            for (String curLevel : value) {
                name.add((String) reverseHashMap.get(curLevel).getValue());
            }
        }
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        this.value = new ArrayList<>();
        this.name = new ArrayList<>();
        if (shouldExpandAllNodes() && !getDefaultValue().contains(String.valueOf(value))) {
            executeOnClick(node);
        } else if (!shouldExpandAllNodes() && (node.getChildren().size() == 0)) {
            executeOnClick(node);
        }
    }

    @VisibleForTesting
    void executeOnClick(TreeNode node) {
        ArrayList<String> reversedValue = new ArrayList<>();
        retrieveValue(treeNodeHashMap, node, reversedValue);
        Collections.reverse(reversedValue);
        this.value = reversedValue;
        extractName();
        dismiss();
    }

    public ArrayList<String> getValue() {
        return this.value;
    }

    private void setValue(final ArrayList<String> value) {
        this.value = value;
        extractName();
    }

    public ArrayList<String> getName() {
        return this.name;
    }

    public ArrayList<String> getDefaultValue() {
        return defaultValue;
    }

    public HashMap<TreeNode, String> getTreeNodeHashMap() {
        return treeNodeHashMap;
    }

    public void setTreeNodeHashMap(HashMap<TreeNode, String> treeNodeHashMap) {
        this.treeNodeHashMap = treeNodeHashMap;
    }

}
