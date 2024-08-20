//package com.data.repository;
//
//
//
//import com.common.rx.OnSubscribe;
//import com.common.rx.subscriber.OnNoneSubscriber;
//import com.common.utils.log.LogUtil;
//
//import io.reactivex.schedulers.Schedulers;
//
///**
// * 用户数据仓库
// *
// * @author LiuFeng
// * @data 2021/3/30 17:52
// */
//public class UserRepository {
//    private static final UserRepository mInstance = new UserRepository();
//
//    private UserRepository() {
//    }
//
//    public static UserRepository getInstance() {
//        return mInstance;
//    }
//
//    /**
//     * 构建缓存
//     */
//    public void buildCache() {
//        queryAll().doOnNext(users -> {
//            CacheFactory.getUserCache().save(ObjUtil.convertUser(users));
//            LogUtil.i("构建用户缓存完成");
//        }).subscribeOn(Schedulers.io()).subscribe(new OnNoneSubscriber<>());
//    }
//
//    /**
//     * 保存用户列表
//     *
//     * @param users
//     * @return
//     */
//    public void saveOrUpdate(List<MUserInfoDB> users) {
//        if (UIHandler.isMainThread()) {
//            getObservable(new OnSubscribe<List<MUserInfoDB>>() {
//                @Override
//                protected List<MUserInfoDB> get() {
//                    save(users);
//                    return users;
//                }
//            }).subscribe(new OnNoneSubscriber<>());
//        } else {
//            save(users);
//        }
//    }
//
//    private void save(List<MUserInfoDB> users) {
//        DBFactory.getUserDao().saveOrUpdate(users);
//        CacheFactory.getUserCache().save(ObjUtil.convertUser(users));
//    }
//
//    /**
//     * 保存用户
//     *
//     * @param user
//     * @return
//     */
//    public void saveOrUpdate(MUserInfoDB user) {
//        if (UIHandler.isMainThread()) {
//            getObservable(new OnSubscribe<MUserInfoDB>() {
//                @Override
//                protected MUserInfoDB get() {
//                    save(user);
//                    return user;
//                }
//            }).subscribe(new OnNoneSubscriber<>());
//        } else {
//            save(user);
//        }
//    }
//
//    private void save(MUserInfoDB user) {
//        DBFactory.getUserDao().saveOrUpdate(user);
//        CacheFactory.getUserCache().save(ObjUtil.convert(user));
//    }
//
//    /**
//     * 查询用户信息
//     *
//     * @param userId
//     * @return
//     */
//    public MUserInfo queryUserById(final String userId) {
//        // 1. 查缓存
//        MUserInfo user = CacheFactory.getUserCache().getUser(userId);
//        if (user != null) {
//            return user;
//        }
//
//        // 2. 查数据库
//        MUserInfoDB userDB = DBFactory.getUserDao().queryUserById(userId);
//        if (userDB != null) {
//            user = ObjUtil.convert(userDB);
//
//            // 3. 写缓存
//            CacheFactory.getUserCache().save(user);
//
//            return user;
//        }
//
//        // 4. 无数据
//        return null;
//    }
//
//    /**
//     * 查询所有用户数据
//     *
//     * @return
//     */
//    public Observable<List<MUserInfoDB>> queryAll() {
//        return getObservable(new OnSubscribe<List<MUserInfoDB>>() {
//            @Override
//            protected List<MUserInfoDB> get() {
//                return DBFactory.getUserDao().queryAll();
//            }
//        }).subscribeOn(RxSchedulers.io());
//    }
//
//    /**
//     * 简化创建可观测流
//     *
//     * @param onSubscribe
//     * @param <T>
//     * @return
//     */
//    private <T> Observable<T> getObservable(OnSubscribe<T> onSubscribe) {
//        return Observable.create(onSubscribe).subscribeOn(Schedulers.io());
//    }
//}