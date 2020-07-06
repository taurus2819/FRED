package nz.cri.gns.fred;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nz.cri.gns.auth.domain.User;
import nz.cri.gns.dataaccess.StorageAccessException;
import nz.cri.gns.fred.hibernate.util.FredHibernate;
import nz.cri.gns.fred.hibernate.util.HibernateServletUtil;
import nz.cri.gns.fred.model.FrUser;
import nz.cri.gns.fred.util.UserUtil;
import nz.cri.gns.jsp.IPSysLoginPage;
import org.springframework.security.core.AuthenticationException;

public abstract class FREDIPSysLoginPage extends IPSysLoginPage {

    private static final long serialVersionUID = 20061203L;

    @Override
    protected User createUser(String loginName, String loginPass) throws AuthenticationException {
        //delegate
        User user = super.createUser(loginName, loginPass);

        //now perform work previously done by FREDUser ctor
        //log login
        try {
            UserUtil userUtil = new UserUtil(FredHibernate.get().getDAOFactory());
            FrUser frUser = userUtil.getFrUser(new Integer(user.getId().intValue()));
            if (frUser == null) {
                frUser = userUtil.createNewFrUser();
                frUser.setUserId(new Integer(user.getId().intValue()));
                userUtil.save(frUser);
            }
            frUser.setLastLogin(new Date());
            userUtil.saveOrUpdate(frUser);
        } catch (StorageAccessException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HibernateServletUtil.withHibernateSession(() -> super.service(request, response));
    }

}
