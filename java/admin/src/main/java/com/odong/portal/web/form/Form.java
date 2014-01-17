package com.odong.portal.web.form;

import com.odong.portal.web.ResponseItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:25
 */
public final class Form extends ResponseItem {
    public void addField(Field field) {
        this.fields.add(field);
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    public Form(String id, String title, String action) {
        super(Type.form);
        this.id = id;
        this.title = title;
        this.action = action;
        this.method = Method.post;
        this.fields = new ArrayList<>();
        this.buttons = new ArrayList<>();
    }

    public enum Method {
        get, post
    }

    private static final long serialVersionUID = -3941326971007776611L;
    private String id;
    private String action;
    private String title;
    private List<Field> fields;
    private List<Button> buttons;
    private Method method;
    private boolean captcha;


    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isCaptcha() {
        return captcha;
    }

    public void setCaptcha(boolean captcha) {
        this.captcha = captcha;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }


    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }
}
