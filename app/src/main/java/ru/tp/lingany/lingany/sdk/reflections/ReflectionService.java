package ru.tp.lingany.lingany.sdk.reflections;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.List;
import java.util.UUID;

public class ReflectionService {

    private final String url;

    public ReflectionService(String url) {
        this.url = url;
    }

    public void getAll(ParsedRequestListener<List<Reflection>> listener) {
        AndroidNetworking.get(url)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObjectList(Reflection.class, listener);
    }

    public void getById(UUID uid, ParsedRequestListener<Reflection> listener) {
        AndroidNetworking.get(url + "{uid}")
                .addPathParameter("uid", uid.toString())
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(Reflection.class, listener);
    }

    public void getByLanguages(UUID nativeLangId, UUID foreignLangId,
                               ParsedRequestListener<Reflection> listener) {
        AndroidNetworking.get(url + "languages/{n_id}/{f_id}")
                .addPathParameter("n_id", nativeLangId.toString())
                .addPathParameter("f_id", foreignLangId.toString())
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(Reflection.class, listener);
    }
}
