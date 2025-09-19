package nz.cri.gns.fred.hibernate.util.hibernate6;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

//import net.sf.hibernate.HibernateException;
//import net.sf.hibernate.Query;
//import net.sf.hibernate.Session;
//import net.sf.hibernate.type.IntegerType;
//import net.sf.hibernate.type.StringType;

import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.hibernate.Session;
import nz.cri.gns.dataaccess.StorageAccessException;
import org.hibernate.Transaction;





public class HibernateUtils {

	private HibernateUtils() {
	}

	public static <T> T get(HibernateProvider provider, Class<T> clazz, Serializable id) throws StorageAccessException {
		try {
			@SuppressWarnings("unchecked")
			T t = (T)provider.currentSession().get(clazz, id);
			return t;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public static void delete(HibernateProvider provider, Object o, boolean flush) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			session.delete(o);
			if (flush)
				session.flush();
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public static void delete(HibernateProvider provider, Object o) throws StorageAccessException {
		delete(provider, o, true);
	}
	
	public static void evict(HibernateProvider provider, Object o) throws StorageAccessException {
		evict(provider, o, true);
	}
	public static void evict(HibernateProvider provider, Object o, boolean flush) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			session.evict(o);
			if (flush)
				session.flush();
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}

	public static <T> T save(HibernateProvider provider, T object) throws StorageAccessException {
		return save(provider, object, true);
	}
	
	public static <T> T save(HibernateProvider provider, T object, boolean flush) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			session.save(object);
			if (flush)
				session.flush();
	        return object;
	    } catch (HibernateException e) {
	        throw new StorageAccessException(e);
	    }
	}

	public static <T> T saveOrUpdate(HibernateProvider provider, T object) throws StorageAccessException {
		return saveOrUpdate(provider, object, true);
	}
	
	/*public static <T> T saveOrUpdate(HibernateProvider provider, T object, boolean flush) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			session.saveOrUpdate(object);
			if (flush)
				session.flush();
	        return object;
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}*/

	public static <T> T saveOrUpdate(HibernateProvider provider, T object, boolean flush) throws StorageAccessException {
		Transaction tx = null;
		try {
			Session session = provider.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(object);
			if (flush)
				session.flush();
			tx.commit();
	        return object;
	    } catch (Exception e) {
	        
			e.printStackTrace();
            if(tx!=null){
                tx.rollback();
            }
			
			throw new StorageAccessException(e);
	    }
	}




	/*

	public static <T> T saveOrUpdate( T object) throws StorageAccessException {
		
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Transaction tx = null;
        try (Session session = factory.openSession()) {

            tx = session.beginTransaction();
            session.saveOrUpdate(object);
            session.flush();
            
            tx.commit();
            return object;

        }
        catch(Exception e) {
            
            e.printStackTrace();
            if(tx!=null){
                tx.rollback();
            }

            
            throw new StorageAccessException(e);
        }

	}

	*/


	public static <T> T update(HibernateProvider provider, T object) throws StorageAccessException {
		return update(provider, object, true);
	}
	
	public static <T> T update(HibernateProvider provider, T object, boolean flush) throws StorageAccessException {
		try {
			Session session = provider.currentSession();
			session.update(object);
			if (flush)
				session.flush();
	        return object;
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}

/*
	public static <T> T getFirst(HibernateProvider provider, String query, int id, Class<T> clazz) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
	        @SuppressWarnings("unchecked")
			List<T> list = (List<T>)session.find(query, new Integer(id), new IntegerType());
			if (list.size() == 0)
			    return null;
			return list.get(0);
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}
	*/
	public static <T> T getFirst(HibernateProvider provider, String query, int id, Class<T> clazz) throws StorageAccessException {
		return null; //temp

	}
/*
	public static <T> T getFirst(HibernateProvider provider, String query, String value, Class<T> clazz) throws StorageAccessException {
		try {
	        Session session = provider.currentSession();
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)session.find(query, value, new StringType());
			if (list.size() == 0)
			    return null;
			return list.get(0);
	    } catch (Exception e) {
	        throw new StorageAccessException(e);
	    }
	}*/

	public static <T> T getFirst(HibernateProvider provider, String query, String value, Class<T> clazz) throws StorageAccessException {
		return null; //temp


	
	}

	
	public static <T> List<T> list(HibernateProvider provider, String query, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		
		System.out.println("at list(): " + query);
		return list(provider, query, null, clazz, parameters);
	}
	
	@SuppressWarnings("unchecked")
	/*public static <T> Iterator<T> iterate(HibernateProvider provider, String query) throws StorageAccessException {
		try {
			return (Iterator<T>)provider.currentSession().iterate(query);
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}*/

	public static <T> Iterator<T> iterate(HibernateProvider provider, String query)  {
		return null;

	}
	
	/*
	public static <T> List<T> list(HibernateProvider provider, String query, Integer maxResults, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		System.out.println("at list2(): " + query);
		
		Session session = provider.currentSession();
		try {
			Query hqlQuery = session.createQuery(query);	
			for (int i = 0; i < parameters.length; i++) {
				hqlQuery.setParameter(i, parameters[i]);
			}
			if (maxResults != null)
				hqlQuery.setMaxResults(maxResults);
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)hqlQuery.list();
			return list;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}*/

	public static <T> List<T> list(HibernateProvider provider, String query, Integer maxResults, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		System.out.println("at list2(): " + query);
		
		Session session = provider.currentSession();
		try {
			Query hqlQuery = session.createQuery(query);	
			for (int i = 0; i < parameters.length; i++) {
				hqlQuery.setParameter(i+1, parameters[i]); //note: in H6 the first param must be 1 not 0
			}
			if (maxResults != null)
				hqlQuery.setMaxResults(maxResults);
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)hqlQuery.list();
			return list;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}
	
	public static <T> List<T> listByNamedQuery(HibernateProvider provider, String queryName, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		return list(provider, queryName, null, clazz, parameters);
	}
	
	public static <T> List<T> listByNamedQuery(HibernateProvider provider, String queryName, Integer maxResults, Class<T> clazz, Object ... parameters) throws StorageAccessException {
		Session session = provider.currentSession();
		try {
			Query hqlQuery = session.getNamedQuery(queryName);	
			for (int i = 0; i < parameters.length; i++) {
				hqlQuery.setParameter(i, parameters[i]);
			}
			if (maxResults != null)
				hqlQuery.setMaxResults(maxResults);
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)hqlQuery.list();
			return list;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}

	public static <T> List<T> listByNamedQuery(HibernateProvider provider, String queryName, Class<T>clazz, String[] paramNames, Object[] params) throws StorageAccessException {
		Session session = provider.currentSession();
		try {
			Query hqlQuery = session.getNamedQuery(queryName);	
			for (int i = 0; i < params.length; i++) {
				hqlQuery.setParameter(paramNames[i], params[i]);
			}
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)hqlQuery.list();
			return list;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}

	/*public static Serializable getId(HibernateProvider provider, Object obj) throws StorageAccessException {
		try {
			return provider.currentSession().getIdentifier(obj);
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
	}*/

	public static Serializable getId(HibernateProvider provider, Object obj) throws StorageAccessException {

		return 1;//temp
	}
	
	public static <T> T findOne(HibernateProvider provider, nz.cri.gns.dataaccess.Query<T> query, Object ... params) throws StorageAccessException {
		List<T> list = findMany(provider, query, params);
		try {
			return list.get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	/*public static <T> List<T> findMany(HibernateProvider provider, nz.cri.gns.dataaccess.Query<T> query, Object ... params) throws StorageAccessException {
		Session session = provider.currentSession();
		try {
			Query hqlQuery = query.getQuery(session);
			String[] paramNames = query.getParameterNames();
			for (int i = 0; i < params.length; i++) {
				hqlQuery.setParameter(paramNames[i], params[i]);
			}
			@SuppressWarnings("unchecked")
	        List<T> list = (List<T>)hqlQuery.list();
			return list;
		} catch (HibernateException e) {
			throw new StorageAccessException(e);
		}
		
	}*/

	public static <T> List<T> findMany(HibernateProvider provider, nz.cri.gns.dataaccess.Query<T> query, Object ... params) throws StorageAccessException {

		return null; //temp
	}
}
