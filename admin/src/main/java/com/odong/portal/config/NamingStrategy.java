package com.odong.portal.config;

import com.odong.itpkg.util.StringHelper;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午4:05
 */

public class NamingStrategy extends ImprovedNamingStrategy {
    private static final long serialVersionUID = 3022396147085580172L;

    public NamingStrategy() {
        super();
        this.prefix = "ITPKG_";
        this.stringHelper = new StringHelper();

    }

    @Override
    public String classToTableName(String className) {
        return prefix + encode(stringHelper.camelCase2Underscore(className));
    }

    @Override
    public String tableName(String tableName) {
        return prefix + encode(stringHelper.camelCase2Underscore(tableName));
        //return tableName.toUpperCase();
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return encode(propertyName);
    }

    @Override
    public String columnName(String columnName) {
        return columnName.toUpperCase();
    }

    private String encode(String s) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                c = base.charAt((base.indexOf(c) + 5) % 26);

                sb.append(c);
            } else if (c == '_' || (c >= '0' && c <= '9')) {
                sb.append(c);
            } else {

                throw new IllegalArgumentException("只能由数字、字母、下划线组成[" + s + "]");
            }

        }
        return sb.toString();
    }


    private final static Logger logger = LoggerFactory.getLogger(NamingStrategy.class);

    private StringHelper stringHelper;
    private String prefix;


}
