package ru.conditer.service;

import org.springframework.core.io.FileSystemResource;
import ru.conditer.entity.AppDocument;
import ru.conditer.entity.AppPhoto;
import ru.conditer.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
