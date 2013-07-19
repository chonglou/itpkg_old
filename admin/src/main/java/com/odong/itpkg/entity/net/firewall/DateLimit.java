package com.odong.itpkg.entity.net.firewall;

import com.odong.portal.entity.IdEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午11:29
 */

@Entity
@Table(name = "ffDate")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DateLimit extends IdEntity {
    public String toWeeks() {
        StringBuilder sb = new StringBuilder();
        if (mon) {
            sb.append("Mon");
        }
        if (tues) {
            sb.append(",Tue");
        }
        if (wed) {
            sb.append(",Wed");
        }
        if (thur) {
            sb.append(",Thu");
        }
        if (fri) {
            sb.append(",Fri");
        }
        if (sat) {
            sb.append(",Sat");
        }
        if (sun) {
            sb.append(",Sun");
        }
        return sb.toString();
    }

    private static final long serialVersionUID = -4892201038551133631L;
    @Column(nullable = false)
    private String name;
    @Lob
    private String details;
    @Column(nullable = false, updatable = false)
    private Long company;

    @Column(nullable = false)
    private int beginHour;
    @Column(nullable = false)
    private int endHour;
    @Column(nullable = false)
    private int beginMinute;
    @Column(nullable = false)
    private int endMinute;
    private boolean mon;
    private boolean tues;
    private boolean wed;
    private boolean thur;
    private boolean fri;
    private boolean sat;
    private boolean sun;
    @Column(nullable = false, updatable = false)
    private Date created;


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public int getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(int beginHour) {
        this.beginHour = beginHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getBeginMinute() {
        return beginMinute;
    }

    public void setBeginMinute(int beginMinute) {
        this.beginMinute = beginMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTues() {
        return tues;
    }

    public void setTues(boolean tues) {
        this.tues = tues;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThur() {
        return thur;
    }

    public void setThur(boolean thur) {
        this.thur = thur;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }
}
