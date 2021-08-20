package com.vijay.jsonwizard.customviews;

import com.unnamed.b.atv.model.TreeNode;
import com.vijay.jsonwizard.BaseTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

public class TreeViewDialogTest extends BaseTest {

    private TreeViewDialog treeViewDialog;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        treeViewDialog = Mockito.spy(new TreeViewDialog(
                RuntimeEnvironment.application,
                new JSONArray(),
                new ArrayList<String>(), new ArrayList<String>()));
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsTrueAndValueInDefaultValuesShouldNotExecuteOnClick() throws JSONException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("test");
        WhiteboxImpl.setInternalState(treeViewDialog, "defaultValue", strings);
        treeViewDialog.setShouldExpandAllNodes(true);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        treeViewDialog.onClick(treeNode, "test");
        Mockito.verify(treeViewDialog, Mockito.never()).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsTrueAndValueNotInDefaultValuesShouldExecuteOnClick() throws JSONException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("testing");
        WhiteboxImpl.setInternalState(treeViewDialog, "defaultValue", strings);
        treeViewDialog.setShouldExpandAllNodes(true);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        treeViewDialog.onClick(treeNode, "test");
        Mockito.verify(treeViewDialog, Mockito.times(1)).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsFalseAndChildrenNodesEmptyShouldExecuteOnClick() throws JSONException {
        treeViewDialog.setShouldExpandAllNodes(false);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        List<TreeNode> treeNodes = new ArrayList<>();
        Mockito.when(treeNode.getChildren()).thenReturn(treeNodes);
        treeViewDialog.onClick(treeNode, "test");
        Mockito.verify(treeViewDialog, Mockito.times(1)).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testDisableOnClickListenerShouldDisableClickActionOnTreeView() {
        treeViewDialog.setShouldDisableOnClickListener(true);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        List<TreeNode> treeNodes = new ArrayList<>();
        Mockito.when(treeNode.getChildren()).thenReturn(treeNodes);
        treeViewDialog.onClick(treeNode, "test");
        Mockito.verify(treeViewDialog, Mockito.never()).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testGetCanvasIsNotNull() {
        Assert.assertNotNull(treeViewDialog.getCanvas());
    }

    @Test
    public void testGetTreeViewIsNotNull() {
        Assert.assertNotNull(treeViewDialog.getTreeView());
    }

    @Test
    public void testGetTreeNodeHashMapIsNotNull() {
        Assert.assertNotNull(treeViewDialog.getTreeNodeHashMap());
    }
}