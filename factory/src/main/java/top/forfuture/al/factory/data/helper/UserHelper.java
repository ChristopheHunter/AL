package top.forfuture.al.factory.data.helper;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.forfuture.al.common.factory.data.DataSource;
import top.forfuture.al.factory.Factory;
import top.forfuture.al.factory.R;
import top.forfuture.al.factory.model.api.RspModel;
import top.forfuture.al.factory.model.api.user.UserUpdateModel;
import top.forfuture.al.factory.model.card.UserCard;
import top.forfuture.al.factory.model.db.User;
import top.forfuture.al.factory.net.Network;
import top.forfuture.al.factory.net.RemoteService;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserHelper {
    // 更新用户信息的操作，异步的
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        // 调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        // 得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        // 网络请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 数据库的存储操作，需要把UserCard转换为User
                    // 保存用户信息
                    User user = userCard.build();
                    user.save();
                    // 返回成功
                    callback.onDataLoaded(userCard);
                } else {
                    // 错误情况下进行错误分配
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }
}
