package be.civadis.poc.s3.service;

import be.civadis.poc.s3.dto.DocumentDTO;
import be.civadis.poc.s3.repository.DocumentView;
import be.civadis.poc.s3.utils.ApplicationInfosUtils;
import be.civadis.poc.s3.utils.TenantContext;
import org.springframework.stereotype.Service;

@Service
public class S3IdentifiantAdapterService {

    public S3ObjectLocation getObjectLocation(DocumentDTO documentDTO){
        return getObjectLocation(documentDTO.getCheminDocument(), documentDTO.getNomDocument());
    }

    public S3ObjectLocation getObjectLocation(DocumentView documentView){
        return getObjectLocation(documentView.getCheminDocument(), documentView.getNomDocument());
    }

    public S3ObjectLocation getObjectLocation(String paramPath, String name){
        String path = removeLeadingSlash(paramPath);
        if (isBucketOfUser(path)){
            //ex : testapp_00000 veut accéder à testapp/file.txt, on va donc accéder à file.txt dans le testapp-00000
            return new S3ObjectLocation(getMyBucketName(), getDocumentKey(removeApp(path, getCurrentApp()), name));
        } else {
            //ex : testapp-00000 veut accéder à onyx/facture.pdf, on va donc accéder à facture.pdf dans le bucket onyx-000
            // testapp_00000 devra avoir alors droit d'accès à un autre bucket que le sien
            String app = getApp(path);
            return new S3ObjectLocation(getOtherBucketName(app), getDocumentKey(removeApp(path, app), name));
        }
    }

    public String removeLeadingSlash(String path){
        if (path != null){
            if (path.startsWith("/")){
                if (path.length() > 1){
                    return path.substring(1);
                } else {
                    return "";
                }
            } else {
                return path;
            }
        } else {
            return "";
        }
    }

    public boolean isBucketOfUser(String path){
        if (path == null) return false;
        String app = getCurrentApp();
        return (path.startsWith("/" + app) || path.startsWith(app));
    }

    public String getApp(String paramPath){
        String path = removeLeadingSlash(paramPath);
        if (path.contains("/")){
            return path.substring(0, path.indexOf("/"));
        } else {
            return path;
        }
    }

    public String removeApp(String paramPath, String app){
        String path = removeLeadingSlash(paramPath);
        if (path.startsWith(app)){
            return path.substring(app.length());
        }
        return path;
    }

    public String getMyBucketName(){
        return  getCurrentApp() + "-" + getCurrentTenant();
    }

    public String getOtherBucketName(String app){
        return app + "-" + getCurrentTenant();
    }

    public String getCurrentApp(){
        return ApplicationInfosUtils.getAppUser();
    }

    public String getCurrentTenant(){
        return TenantContext.getCurrentTenant();
    }

    public String getDocumentKey(String path, String name){
        StringBuilder str = new StringBuilder();
        str.append(path);
        if (!path.endsWith("/")){
            str.append("/");
        }
        str.append(name);
        return removeLeadingSlash(str.toString());
    }



}
