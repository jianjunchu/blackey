package org.blackey.ui.choose.paymentMode;

import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.UserPaymentMode;
import org.blackey.service.UserPaymentModeApiService;
import org.blackey.ui.mine.myaccount.form.MyAccountFormFragment;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;

public class ChoosePaymentModeListItemViewModel extends ItemViewModel<ChoosePaymentModeListViewModel> {

    public ObservableField<UserPaymentMode> entity = new ObservableField<>();
    public ObservableField<String> paymentModeNameText = new ObservableField<>();
    public ObservableField<Integer> imageViewSrc = new ObservableField<>();

    public ChoosePaymentModeListItemViewModel(@NonNull ChoosePaymentModeListViewModel viewModel, UserPaymentMode entity) {
        super(viewModel);
        this.entity.set(entity);
        imageViewSrc.set(ResourcesUtils.getPaymentModeIcon(viewModel.getApplication(),entity.getPaymentModeIcon()));
        if("BANKACCOUNT".equalsIgnoreCase(entity.getPaymentModeCode())){
            paymentModeNameText.set(ResourcesUtils.getBankName(viewModel.getApplication(),entity.getBankSwiftCode()));
        }else{
            paymentModeNameText.set(ResourcesUtils.getPaymentName(viewModel.getApplication(),entity.getPaymentModeCode()));

        }
        //ImageView的占位图片，可以解决RecyclerView中图片错误问题
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

        }
    });

    public BindingCommand<Boolean> itemCheckedChanged = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(final Boolean isChecked) {
            if(isChecked){
                viewModel.selected(ChoosePaymentModeListItemViewModel.this);
            }else{
                viewModel.remove(ChoosePaymentModeListItemViewModel.this);
            }
        }
    });

    //编辑
    public BindingCommand editClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            Bundle mBundle = new Bundle();
            mBundle.putParcelable("entity", entity.get());
            viewModel.startContainerActivity(MyAccountFormFragment.class.getCanonicalName(),mBundle);
        }
    });

    //删除
    public BindingCommand deleteClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            showNormalDialog();
        }
    });

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(viewModel.activity);
        normalDialog.setTitle(viewModel.getApplication().getString(R.string.delete_confirmation));
        normalDialog.setMessage(viewModel.getApplication().getString(R.string.hint_delete_account));
        normalDialog.setPositiveButton(viewModel.getApplication().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestDelete();
                    }
                });
        normalDialog.setNegativeButton(viewModel.getApplication().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void requestDelete() {
        RetrofitClient.getInstance().create(UserPaymentModeApiService.class)
                .delete(entity.get().getUserPaymentModeId())
                .compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        viewModel.showDialog();
                    }
                })
                .subscribe(new Consumer<BaseResponse<Integer>>() {
                    @Override
                    public void accept(BaseResponse<Integer> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            viewModel.requestNetWork();
                        }
                        viewModel.dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        viewModel.dismissDialog();
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                        //关闭对话框
                        viewModel.dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChoosePaymentModeListItemViewModel that = (ChoosePaymentModeListItemViewModel) o;
        return Objects.equals(entity.get().getUserPaymentModeId(), that.entity.get().getUserPaymentModeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }
}
