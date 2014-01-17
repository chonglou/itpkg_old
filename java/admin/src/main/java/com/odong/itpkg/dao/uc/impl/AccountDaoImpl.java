package com.odong.itpkg.dao.uc.impl;

import com.odong.itpkg.dao.uc.AccountDao;
import com.odong.itpkg.entity.uc.Account;
import com.odong.portal.dao.impl.BaseJpa2DaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 上午10:59
 */
@Repository("uc.accountDao")
public class AccountDaoImpl extends BaseJpa2DaoImpl<Account, Long> implements AccountDao {
}
