package com.vijay.jsonwizard.reader;

import com.vijay.jsonwizard.processor.MultiSelectListFileProcessor;

public abstract class MultiSelectListFileReaderAndProcessor {

    protected String fileName;
    private MultiSelectListFileReader fileReader;
    private MultiSelectListFileProcessor fileProcessor;

    public MultiSelectListFileReaderAndProcessor(MultiSelectListFileReader fileReader, MultiSelectListFileProcessor fileProcessor) {
        this.fileReader = fileReader;
        this.fileProcessor = fileProcessor;
    }

    public void initMultiSelectFileReader(String fileName) {
        if (isAlreadySaved(fileName)) {
            return;
        }
        this.fileName = fileName;
        String content = read(fileName);
        Object processedContent = process(content);
        save(processedContent);
    }

    private String read(String fileName) {
        return fileReader.read(fileName);
    }

    public Object process(String content) {
        return fileProcessor.process(content);
    }

    protected abstract void save(Object processedContent);

    protected abstract boolean isAlreadySaved(String fileName);
}
