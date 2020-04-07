package com.vijay.jsonwizard.customviews;

import com.unnamed.b.atv.model.TreeNode;
import com.vijay.jsonwizard.BaseTest;

import org.json.JSONArray;
import org.json.JSONException;
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
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsTrueAndValueInDefaultValuesShouldNotExecuteOnClick() throws JSONException {
        treeViewDialog = new TreeViewDialog(
                RuntimeEnvironment.application,
                new JSONArray(),
                new ArrayList<String>(), new ArrayList<String>());
        TreeViewDialog spyTreeViewDialog = Mockito.spy(treeViewDialog);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("test");
        WhiteboxImpl.setInternalState(spyTreeViewDialog, "defaultValue", strings);
        spyTreeViewDialog.setShouldExpandAllNodes(true);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        spyTreeViewDialog.onClick(treeNode, "test");
        Mockito.verify(spyTreeViewDialog, Mockito.never()).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsTrueAndValueNotInDefaultValuesShouldExecuteOnClick() throws JSONException {
        treeViewDialog = new TreeViewDialog(
                RuntimeEnvironment.application,
                new JSONArray(),
                new ArrayList<String>(), new ArrayList<String>());
        TreeViewDialog spyTreeViewDialog = Mockito.spy(treeViewDialog);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("testing");
        WhiteboxImpl.setInternalState(spyTreeViewDialog, "defaultValue", strings);
        spyTreeViewDialog.setShouldExpandAllNodes(true);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        spyTreeViewDialog.onClick(treeNode, "test");
        Mockito.verify(spyTreeViewDialog, Mockito.times(1)).executeOnClick(Mockito.any(TreeNode.class));
    }

    @Test
    public void testOnClickWhenExpandAllNodesIsFalseAndChildrenNodesEmptyShouldExecuteOnClick() throws JSONException {
        treeViewDialog = new TreeViewDialog(
                RuntimeEnvironment.application,
                new JSONArray(),
                new ArrayList<String>(), new ArrayList<String>());
        TreeViewDialog spyTreeViewDialog = Mockito.spy(treeViewDialog);
        spyTreeViewDialog.setShouldExpandAllNodes(false);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        List<TreeNode> treeNodes = new ArrayList<>();
        Mockito.when(treeNode.getChildren()).thenReturn(treeNodes);
        spyTreeViewDialog.onClick(treeNode, "test");
        Mockito.verify(spyTreeViewDialog, Mockito.times(1)).executeOnClick(Mockito.any(TreeNode.class));
    }
}