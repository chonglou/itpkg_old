package com.odong.itpkg.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午9:42
 */
@Entity
@Table(name = "siteRpc")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rpc implements Serializable {
    public enum Type {
        FILE, COMMAND, HEART
    }

    public enum State {
        SUBMIT, PROCESS, SUCCESS, FAIL
    }

    private static final long serialVersionUID = -2733778730965933362L;

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;
    @Column(nullable = false, updatable = false)
    private Long host;
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Lob
    @Column(nullable = false, updatable = false)
    private String request;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Lob
    private String response;
    @Column(nullable = false, updatable = false)
    private Date created;
    private Date begin;
    private Date end;
    @Version
    private int version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
