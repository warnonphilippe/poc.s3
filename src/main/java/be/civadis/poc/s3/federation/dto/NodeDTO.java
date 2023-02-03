package be.civadis.poc.s3.federation.dto;

import java.util.Map;

public class NodeDTO {

    private boolean isFolder;
    private boolean isFile;
    private String name;
    private String id;
    private String nodeType;
    private String parentId;
    private Map<String, Object> properties;
    private ContentInfosDTO content;
    private PathInfosDTO path;
    private String createdAt;
    private String modifiedAt;

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ContentInfosDTO getContent() {
        return content;
    }

    public void setContent(ContentInfosDTO content) {
        this.content = content;
    }

    public PathInfosDTO getPath() {
        return path;
    }

    public void setPath(PathInfosDTO path) {
        this.path = path;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
