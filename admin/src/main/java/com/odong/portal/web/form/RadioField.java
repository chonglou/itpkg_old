package com.odong.portal.web.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-26
 * Time: 下午5:41
 */
public final class RadioField<T> extends Field<T> {

    public RadioField(String id, String label, T value) {
        super(id, label, "radio", value, true, null);
        this.options = new ArrayList<>();
        this.cols = 5;
    }

    public void addOption(String label, T value) {
        addOption(label, value, null);
    }

    public void addOption(String label, T value, String tooltip) {
        Option option = new Option();
        option.setValue(value);
        option.setLabel(label);
        option.setTooltip(tooltip);
        this.options.add(option);
    }

    private static final long serialVersionUID = 8218621869957653686L;

    public final class Option implements Serializable {

        private static final long serialVersionUID = -2068326659499622098L;
        private T value;
        private String label;
        private String tooltip;

        public String getTooltip() {
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

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

    private List<Option> options;
    private int cols;

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
