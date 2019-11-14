package com.vijay.jsonwizard.reader;

import com.vijay.jsonwizard.processor.MultiSelectCsvFileProcessor;

public abstract class MultiSelectFileReader {

    protected String fileName;
    private FileReader fileReader;
    private MultiSelectCsvFileProcessor processor;

    public MultiSelectFileReader(FileReader fileReader, MultiSelectCsvFileProcessor processor) {
        this.fileReader = fileReader;
        this.processor = processor;
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
        return processor.process(content);
    }

    protected abstract void save(Object processedContent);

    protected abstract boolean isAlreadySaved(String fileName);
}
