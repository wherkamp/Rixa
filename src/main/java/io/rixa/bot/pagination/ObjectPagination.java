package io.rixa.bot.pagination;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ObjectPagination {

    @Getter private List<Object> objects;
    @Getter private int maxPage, pageSize;
    public ObjectPagination(List<Object> objects, int pageSize) {
        this.objects = objects;
        this.pageSize = pageSize;
        this.maxPage = (int) Math.ceil((objects.size()/pageSize)+1);
    }

    public List<Object> getPage(int page) {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(objects == null || objects.size() < fromIndex){
            return Collections.emptyList();
        }

        // toIndex exclusive
        return objects.subList(fromIndex, Math.min(fromIndex + pageSize, objects.size()));
    }

    public void updateList(List<Object> obj) {
        this.objects = obj;
    }
}
