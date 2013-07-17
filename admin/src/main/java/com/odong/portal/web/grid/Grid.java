package com.odong.portal.web.grid;

import com.odong.portal.web.ResponseItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:26
 */
public class Grid extends ResponseItem {
    public Grid(String id, String name, Column... cols) {
        super(Type.grid);
        this.id = id;
        this.name = name;
        this.cols = cols;
        this.items = new ArrayList<>();
    }

    public void addRow(String... texts) {
        if((action== null && texts.length == cols.length) ||
                (action!=null && texts.length== cols.length+1)){
            Collections.addAll(items, texts);
        }
        else{
        throw new IllegalArgumentException("参数个数不对");
        }
    }

    public void able(boolean view, boolean add, boolean edit, boolean delete){
        this.view = view;
        this.add = add;
        this.edit = edit;
        this.delete = delete;
    }

    private static final long serialVersionUID = -4735460726311781464L;
    private String id;
    private String name;
    private Integer pageSize;
    private Column[] cols;
    private List<String> items;
    private String action;
    private boolean add;
    private boolean edit;
    private boolean view;
    private boolean delete;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column[] getCols() {
        return cols;
    }

    public void setCols(Column[] cols) {
        this.cols = cols;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
