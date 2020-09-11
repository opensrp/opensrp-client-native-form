package com.vijay.jsonwizard.factory;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.filesource.AssetsFileSource;
import com.vijay.jsonwizard.filesource.DiskFileSource;

import org.junit.Assert;
import org.junit.Test;

public class FileSourceFactoryHelperTest {

    @Test
    public void testGetFileSourceLoadsCorrectFileSource() {
        // default source is asset file source
        Assert.assertTrue(FileSourceFactoryHelper.getFileSource("") instanceof AssetsFileSource);

        Assert.assertTrue(FileSourceFactoryHelper.getFileSource(JsonFormConstants.FileSource.DISK) instanceof DiskFileSource);
    }
}
