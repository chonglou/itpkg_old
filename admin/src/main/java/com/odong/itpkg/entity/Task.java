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
@Table(name = "siteTask")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task implements Serializable {

    public enum Type {
        RPC_FILE, RPC_COMMAND, RPC_HEART, SYS_GC, MYSQL_BACKUP
    }

    public enum State {
        SUBMIT, PROCESS, DONE
    }

    private static final long serialVersionUID = -2733778730965933362L;

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;
    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Lob
    @Column(updatable = false)
    private String request;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Lob
    private String response;
    @Column(nullable = false, updatable = false)
    private Date created;
    private Date lastBegin;
    private Date lastEnd;
    @Column(nullable = false, updatable = false)
    private Date startUp;
    @Column(updatable = false)
    private Date shutDown;
    @Column(updatable = false)
    /**
     * null - 立刻执行
     * 0    - 不限次数
     * >0   - 限制次数
     */
    private Integer total;
    @Column(nullable = false)
    private int index;
    @Column(nullable = false, updatable = false)
    private int space;
    @Version
    private int version;


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getLastBegin() {
        return lastBegin;
    }

    public void setLastBegin(Date lastBegin) {
        this.lastBegin = lastBegin;
    }

    public Date getLastEnd() {
        return lastEnd;
    }

    public void setLastEnd(Date lastEnd) {
        this.lastEnd = lastEnd;
    }

    public Date getStartUp() {
        return startUp;
    }

    public void setStartUp(Date startUp) {
        this.startUp = startUp;
    }

    public Date getShutDown() {
        return shutDown;
    }

    public void setShutDown(Date shutDown) {
        this.shutDown = shutDown;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
