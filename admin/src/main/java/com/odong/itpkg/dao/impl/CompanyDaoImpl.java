package com.odong.itpkg.dao.impl;

import com.odong.itpkg.dao.CompanyDao;
import com.odong.itpkg.entity.uc.Company;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午11:43
 */
@Repository("uc.companyDao")
public class CompanyDaoImpl extends BaseJpa2DaoImpl<Company, String> implements CompanyDao {
}
