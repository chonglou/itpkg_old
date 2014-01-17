package com.odong.itpkg.entity.uc;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:02
 */

@Entity
@Table(name = "ucGroupUser")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GroupUser extends IdEntity {
    private static final long serialVersionUID = 9103845888833258240L;
    @Column(nullable = false, updatable = false)
    private Long user;
    @Column(nullable = false, updatable = false)
    private Long group;
    @Column(nullable = false, updatable = false)
    private Date created;

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
