package com.odong.portal.web.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:52
 */
public final class SelectField<T> extends Field<T> {
    public SelectField(String id, String name) {
        this(id, name, null, null);
    }

    public SelectField(String id, String name, T value) {
        this(id, name, value, null);
    }

    public SelectField(String id, String name, T value, String tooltip) {
        super(id, name, "select", value, true, tooltip);
        this.options = new ArrayList<>();
        this.width = 80;
    }

    public void addOption(String label, T value) {
        Option option = new Option();
        option.setValue(value);
        option.setLabel(label);
        this.options.add(option);
    }

    private static final long serialVersionUID = 1148063126960688562L;
    private List<Option> options;
    private int width;

    public final class Option implements Serializable {
        private static final long serialVersionUID = -4564819177779094836L;
        private T value;
        private String label;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
