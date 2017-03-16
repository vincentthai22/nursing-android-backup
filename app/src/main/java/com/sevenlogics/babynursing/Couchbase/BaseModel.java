package com.sevenlogics.babynursing.Couchbase;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stevenchan1 on 1/13/17.
 */

@JsonIgnoreProperties({"document"})
public class BaseModel {

    public ArrayList<String> cblChannel;

    public String type;

    private Date mMarkDocDeletedDate;

    @JsonIgnoreProperties
    public Document document;

    public BaseModel()
    {

    }

    public static <T> T modelForId(String documentId, Class<T> aClass)
    {
        Document document = CouchbaseManager.getInstance().documentWithId(documentId);

        if (null != document)
        {
            return BaseModel.modelForDocument(document, aClass);
        }

        return null;
    }

    public static <T> T modelForDocument(Document aDocument, Class<T> aClass)
    {
        T aInstance = CouchbaseManager.getInstance().getObjectMapper().convertValue(aDocument.getProperties(), aClass);

        BaseModel baseModel = (BaseModel)aInstance;

        baseModel.document = aDocument;

        return aInstance;
    }

    public BaseModel modelForNewDocumentInDatabase(Database database)
    {
        BaseModel baseModel = new BaseModel(database);

        baseModel.type = baseModel.docType();

        return baseModel;
    }

    private BaseModel(Database database)
    {
        this.document = database.createDocument();
    }

    public void deleteModel(Boolean hardDelete)
    {
        if (hardDelete)
        {
            try
            {
                this.document.delete();
            }
            catch (Exception e)
            {

            }
        }
        else
        {
            mMarkDocDeletedDate = new Date();


        }
    }

    public void deleteModel()
    {
        deleteModel(false);
    }


    public String docType()
    {
        return "";
    }
}
